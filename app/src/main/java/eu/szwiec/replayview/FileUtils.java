package eu.szwiec.replayview;


import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;

public class FileUtils {
    public static boolean unzip(String zipPath, String destinationPath) {
        try {
            ZipFile zipFile = new ZipFile(zipPath);
            zipFile.extractAll(destinationPath);
            return true;
        } catch (ZipException e) {
            e.printStackTrace();
            return false;
        }
    }
}
