package info.smart_tools.smartactors.das;

import info.smart_tools.smartactors.base.interfaces.iaction.IAction;
import info.smart_tools.smartactors.das.commands.AddOnActorCreationUploadRepository;
import info.smart_tools.smartactors.das.commands.AddOnFeatureCreationUploadRepository;
import info.smart_tools.smartactors.das.commands.AddOnPluginCreationUploadRepository;
import info.smart_tools.smartactors.das.commands.AddOrUpdateActorUploadRepository;
import info.smart_tools.smartactors.das.commands.AddOrUpdateFeatureUploadRepository;
import info.smart_tools.smartactors.das.commands.AddOrUpdatePluginUploadRepository;
import info.smart_tools.smartactors.das.commands.CreateActor;
import info.smart_tools.smartactors.das.commands.CreateFeature;
import info.smart_tools.smartactors.das.commands.CreatePlugin;
import info.smart_tools.smartactors.das.commands.CreateProject;
import info.smart_tools.smartactors.das.commands.CreateServer;
import info.smart_tools.smartactors.das.commands.DownloadCore;
import info.smart_tools.smartactors.das.commands.ImportProject;
import info.smart_tools.smartactors.das.commands.UpdateActorVersion;
import info.smart_tools.smartactors.das.commands.UpdateFeatureVersion;
import info.smart_tools.smartactors.das.commands.UpdatePluginVersion;
import info.smart_tools.smartactors.das.utilities.CommandLineArgsResolver;
import info.smart_tools.smartactors.das.utilities.ProjectResolver;
import info.smart_tools.smartactors.das.utilities.interfaces.ICommandLineArgsResolver;
import info.smart_tools.smartactors.das.utilities.interfaces.IProjectResolver;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.internal.HelpScreenException;

import java.util.HashMap;
import java.util.Map;

public final class Das {

    private Das() {
    }

    @SuppressWarnings("unchecked")
    public static void main(final String[] args)
            throws Exception {

        Map<String, IAction> commands = initCommands();

        System.out.println("Distributed Actor System. Design, assembly and deploy tools.");
        System.out.println("Version 0.6.0.");

        if (args.length == 0) {
            return;
        }
        ICommandLineArgsResolver clar = null;
        try {
            clar = new CommandLineArgsResolver(args);
        } catch (HelpScreenException e) {
            System.out.println();
            return;
        } catch (ArgumentParserException e) {
            System.out.println("Could not parse commandline arguments: " + e);
            return;
        }
        if (!clar.isCommand()) {
            clar.printHelp();
            return;
        }
        String command = clar.getCommand();
        if (null == command) {
            clar.printHelp();
            return;
        }
        IAction action = commands.get(command);
        if (null == action) {
            System.out.println("Unavailable command.");
            System.out.println("Available commands: ");
            return;
        }
        try {
            IProjectResolver pr = new ProjectResolver();
            action.execute(new Object[]{clar, pr});
        } catch (Throwable e) {
            System.out.println("Command execution has been failed with error: ");
            System.out.println(e.getMessage());
            throw new Exception(e);
        }
    }

    private static Map<String, IAction> initCommands() {
        Map<String, IAction> commands = new HashMap<>();

        commands.put("import_project", new ImportProject());
        commands.put("import", new ImportProject());

        commands.put("create_project", new CreateProject());
        commands.put("cp", new CreateProject());

        commands.put("create_feature", new CreateFeature());
        commands.put("cf", new CreateFeature());

        commands.put("create_actor", new CreateActor());
        commands.put("ca", new CreateActor());

        commands.put("create_plugin", new CreatePlugin());
        commands.put("cpl", new CreatePlugin());

        commands.put("add_feature_repository", new AddOrUpdateFeatureUploadRepository());
        commands.put("afr", new AddOrUpdateFeatureUploadRepository());
        commands.put("update_feature_repository", new AddOrUpdateFeatureUploadRepository());
        commands.put("ufr", new AddOrUpdateFeatureUploadRepository());

        commands.put("add_actor_repository", new AddOrUpdateActorUploadRepository());
        commands.put("aar", new AddOrUpdateActorUploadRepository());
        commands.put("update_actor_repository", new AddOrUpdateActorUploadRepository());
        commands.put("uar", new AddOrUpdateActorUploadRepository());

        commands.put("add_plugin_repository", new AddOrUpdatePluginUploadRepository());
        commands.put("aplr", new AddOrUpdatePluginUploadRepository());
        commands.put("update_plugin_repository", new AddOrUpdatePluginUploadRepository());
        commands.put("uplr", new AddOrUpdatePluginUploadRepository());

        commands.put("add_on_feature_creation_upload_repository", new AddOnFeatureCreationUploadRepository());
        commands.put("aofcur", new AddOnFeatureCreationUploadRepository());

        commands.put("add_on_actor_creation_upload_repository", new AddOnActorCreationUploadRepository());
        commands.put("aoacur", new AddOnActorCreationUploadRepository());

        commands.put("add_on_plugin_creation_upload_repository", new AddOnPluginCreationUploadRepository());
        commands.put("aoplcur", new AddOnPluginCreationUploadRepository());

        commands.put("update_feature_version", new UpdateFeatureVersion());
        commands.put("ufv", new UpdateFeatureVersion());

        commands.put("update_actor_version", new UpdateActorVersion());
        commands.put("uav", new UpdateActorVersion());

        commands.put("update_plugin_version", new UpdatePluginVersion());
        commands.put("uplv", new UpdatePluginVersion());

        commands.put("cs", new CreateServer());
        commands.put("create_server", new CreateServer());

        commands.put("dc", new DownloadCore());
        commands.put("download_core", new DownloadCore());

        return commands;
    }
}
