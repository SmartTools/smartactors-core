package info.smart_tools.smartactors.http_endpoint.deserialize_strategy_post_multipart_form_data;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.interfaces.iaction.IActionTwoArgs;
import info.smart_tools.smartactors.base.interfaces.iaction.exception.ActionExecutionException;
import info.smart_tools.smartactors.base.interfaces.istrategy.IStrategy;
import info.smart_tools.smartactors.base.interfaces.istrategy.exception.StrategyException;
import io.netty.handler.codec.http.multipart.FileUpload;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * Strategy that saving incoming file
 *
 * args[0] - saving file
 * args[1] - if file exists - does action:
 *   0 - overwrite
 *   1 - append to the end
 *   2 - save with new name (by pattern name(count).extension )
 *   3 - thrown exception
 * args[2] - the directory which must contain a saved file, if null or absent - will not be performed
 */
public class FileSavingStrategy implements IStrategy {

    private static final String EXTENSION_DELIMITER = ".";
    private static final String LEFT_BRACKET = "(";
    private static final String RIGHT_BRACKET = ")";

    private Map<Integer, IActionTwoArgs<File, FileUpload>> onFileExistsActions = new HashMap<>();

    /**
     * Default constructor
     */
    public FileSavingStrategy() {
        this.onFileExistsActions.put(
                0,
                (file, source) -> {
                    try {
                        saveFile(file, source);
                    } catch (IOException e) {
                        throw new RuntimeException("Issues with IO has occurred.", e);
                    }
                }
        );
        this.onFileExistsActions.put(
                1, (file, source) -> {
                    throw new NotImplementedException();
                }
        );
        this.onFileExistsActions.put(
                2, (file, source) -> {
                    try {
                        file = validateFileExists(file);
                        saveFile(file, source);
                    } catch (IOException e) {
                        throw new RuntimeException("Issues with IO has occurred.", e);
                    }
                }
        );
        this.onFileExistsActions.put(
                3, (file, source) -> {
                    if (file.exists()) {
                        throw new RuntimeException("File already exists.");
                    } else {
                        try {
                            saveFile(file, source);
                        } catch (IOException e) {
                            throw new RuntimeException("Issues with IO has occurred.", e);
                        }
                    }
                }
        );
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T resolve(Object... args) throws StrategyException {
        FileUpload file = (FileUpload) args[0];
        Integer onFileExists = null;
        String targetDirectory = null;
        if (args.length > 1) {
            Integer value = (Integer) args[1];
            onFileExists = null == value || value > 3 || value < 0 ? 0 : value;
        }
        if (args.length > 2) {
            targetDirectory = (String) args[2];
        }
        IActionTwoArgs<File, FileUpload> action = this.onFileExistsActions.get(onFileExists);
        File newFile = new File(targetDirectory + File.separator + file.getName());
        try {
            validatePathAccessibility(targetDirectory, newFile.getParent());
            action.execute(newFile, file);
        } catch (InvalidArgumentException | ActionExecutionException | IOException e) {
            throw new StrategyException(e);
        }

        return (T) newFile;
    }

    private File validateFileExists(final File file) throws IOException {
        String path = file.getParentFile().getCanonicalPath();
        File newFile = new File(file.getCanonicalPath());
        int count = 0;
        while (newFile.exists()) {
            count++;
            newFile = new File(
                    path + File.separator + getFileName(file) +
                            LEFT_BRACKET + count + RIGHT_BRACKET +
                            (!getFileExtension(file).isEmpty() ? EXTENSION_DELIMITER + getFileExtension(file) : "")
            );
        }

        return newFile;
    }

    private void validatePathAccessibility(final String allowedDir, final String resultDir)
            throws IOException {
        if (allowedDir == null || allowedDir.isEmpty()) {
            return;
        }
        File f1 = new File(allowedDir);
        String canonicalAllowedDir = f1.getCanonicalPath();
        File f2 = new File(resultDir);
        String canonicalResultDir = f2.getCanonicalPath();

        if (!canonicalResultDir.startsWith(canonicalAllowedDir)) {
            throw new RuntimeException("Try to save file to an unauthorized directory.");
        }
    }

    private String getFileExtension(final File file) {
        String name = file.getName();
        if(name.lastIndexOf(EXTENSION_DELIMITER) != -1 && name.lastIndexOf(EXTENSION_DELIMITER) != 0)
            return name.substring(name.lastIndexOf(EXTENSION_DELIMITER) + 1);
        else return "";
    }

    private String getFileName(final File file) {
        String name = file.getName();
        if(name.lastIndexOf(EXTENSION_DELIMITER) != -1 && name.lastIndexOf(EXTENSION_DELIMITER) != 0)
            return name.substring(0, name.lastIndexOf(EXTENSION_DELIMITER));
        else return name;
    }

    private void saveFile(final File file, final FileUpload source) throws IOException {
        Files.createDirectories(Paths.get(file.getParent()));
        try (OutputStream os = new FileOutputStream(file)) {
            Files.copy(source.getFile().toPath(), os);
            os.flush();
        }
    }
}
