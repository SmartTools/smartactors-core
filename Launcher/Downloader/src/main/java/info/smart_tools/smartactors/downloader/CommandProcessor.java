package info.smart_tools.smartactors.downloader;

import com.fasterxml.jackson.databind.ObjectMapper;
import info.smart_tools.smartactors.downloader.commons.CommonParameters;
import info.smart_tools.smartactors.downloader.commons.IActor;
import info.smart_tools.smartactors.downloader.commons.IMessage;
import info.smart_tools.smartactors.downloader.commons.IRoutingSlip;
import info.smart_tools.smartactors.downloader.commons.IStep;
import info.smart_tools.smartactors.downloader.commons.actors.InsertRoutingSlipActor;
import info.smart_tools.smartactors.downloader.commons.actors.WhileActor;
import info.smart_tools.smartactors.downloader.feature_downloader.MavenFeatureDownloader;
import info.smart_tools.smartactors.downloader.features.actors.CollectDependencyDataActor;
import info.smart_tools.smartactors.downloader.features.actors.DownloadFeatureActor;
import info.smart_tools.smartactors.downloader.features.actors.DownloadFeaturesActor;
import info.smart_tools.smartactors.downloader.features.actors.FeaturesAndRepositoriesStorageActor;
import info.smart_tools.smartactors.downloader.features.actors.ReadFeatureConfigActor;
import info.smart_tools.smartactors.downloader.files.actors.FileOperationsActor;
import info.smart_tools.smartactors.downloader.commons.Message;
import info.smart_tools.smartactors.downloader.jcommander.Args;
import info.smart_tools.smartactors.downloader.jcommander.actors.ArgsToMessageActor;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class CommandProcessor {

    private final Map<String, IRoutingSlip> slips = new HashMap<>();
    private ObjectMapper objectMapper;


    public CommandProcessor(final ObjectMapper mapper) {
        this.objectMapper = mapper;
        try (
                Stream<Path> paths = Files
                        .walk(Paths.get(this.getClass().getClassLoader().getResource("routing_slips").toURI()))
        ) {
            paths
                    .filter(Files::isRegularFile)
                    .forEach(
                            f -> {
                                try {
                                    this.slips.put(
                                            f.toFile().getName().split("\\.")[0],
                                            this.objectMapper.readValue(f.toFile(), IRoutingSlip.class)
                                    );
                                } catch (Exception e) {
                                    throw  new RuntimeException("Could not parse json file - " + f.getFileName(), e);
                                }
                            }
                    );
        } catch (Exception e) {
            System.err.println("Could not read routing slips.");
            e.printStackTrace();
        }

    }

    private Map<String, IActor> actors = new HashMap<String, IActor>() {{
        put("fileOperationActor", new FileOperationsActor());
        put("readFeatureConfigActor", new ReadFeatureConfigActor());
        put("downloadFeaturesActor", new DownloadFeaturesActor());
        put("downloadFeatureActor", new DownloadFeatureActor(new MavenFeatureDownloader()));
        put("collectDependencyDataActor", new CollectDependencyDataActor());
        put("ArgsToMessageActor", new ArgsToMessageActor());
        put("whileActor", new WhileActor());
        put("insertRoutingSlipActor", new InsertRoutingSlipActor());
        put("featuresAndRepositoriesStoragesActor", new FeaturesAndRepositoriesStorageActor());
    }};

    public void process(final Args arguments) {
        IRoutingSlip slip = this.slips.get(arguments.getCommand());
        IMessage message = Message
                .builder()
                .add(CommonParameters.ARGUMENTS, arguments)
                .add(CommonParameters.SLIP_STORAGE, this.slips)
                .build();
        if (null != slip) {
            message.put(CommonParameters.SLIP, slip);
            while (slip.hasNext()) {
                IStep step = slip.next();
                IActor actor = this.actors.get(step.getActorName());
                if (null != actor) {
                    message.put(CommonParameters.METHOD_PARAMS, step.getMethodParameters());
                    actor.execute(step.getMethodName(), message);
                }
            }
        }
    }
}
