package eu.szwiec.replayview.replay;

import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import eu.szwiec.replayview.FileUtils;
import eu.szwiec.replayview.SDKConstants;

public class FilesProvider {

    private FilesProvider() {
    }

    public static List<File> getFiles(String zipPath, CharSequence[] dataTypes) {
        FilesProvider provider = new FilesProvider();
        File extractedDir = provider.getExtractedDir(zipPath);
        List<File> files = provider.getMatchingFiles(dataTypes, extractedDir);

        return files;
    }

    private List<File> getMatchingFiles(CharSequence[] types, File dir) {
        List<File> files = new ArrayList<>();

        for(CharSequence type : types) {
            files.addAll( getMatchingFiles(type, dir) );
        }
        return files;
    }

    private List<File> getMatchingFiles(CharSequence type, File dir) {
        List<File> files = new ArrayList<>();

        String[] names = dir.list(
                (d, name) -> {
                    String regex = String.format(".*\\.%s\\d+", type);
                    return name.matches(regex);
                });

        for (String name : names) {
            File file = new File(dir + "/" + name);
            files.add(file);
        }

        return files;
    }

    private File getExtractedDir(String zipPath) {
        String destinationPath = getDestinationPath(zipPath);
        boolean isUnzipSuccessful = FileUtils.unzip(zipPath, destinationPath);

        if(!isUnzipSuccessful) throw new RuntimeException("Problem with unzipping");

        return new File(destinationPath);
    }

    private String getDestinationPath(String zipPath) {
        String filenameWithExtension = zipPath.substring(zipPath.lastIndexOf("/") + 1);
        return SDKConstants.SDCARD_PATH + FilenameUtils.removeExtension(filenameWithExtension);
    }
}
