package info.smart_tools.smartactors.downloader;

import com.beust.jcommander.Parameter;

public class Args {

    @Parameter(names = {"-file"}, description = "Name of feature config file.")
    private String fileName = "config.json";

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
