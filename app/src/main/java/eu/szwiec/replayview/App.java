package eu.szwiec.replayview;

import android.app.Application;

import java.io.File;
import java.util.List;

import eu.szwiec.replayview.replay.FilesProvider;
import eu.szwiec.replayview.replay.ReplayFile;
import eu.szwiec.replayview.replay.Type;

public class App extends Application {
    private List<ReplayFile> files;

    public List<ReplayFile> getFiles(String zipPath, List<Type> extensions) {
        if(files == null) {
            files = FilesProvider.INSTANCE.getFiles(zipPath, extensions);
        }
        return files;
    }
}
