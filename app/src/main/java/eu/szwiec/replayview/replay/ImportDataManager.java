package eu.szwiec.replayview.replay;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by szwiec on 07/07/2017.
 */

public class ImportDataManager {

    public static final String TYPE_GPS = "gps";
    public static final String TYPE_SENSOR = "sensor";
    public static final String TYPE_BLUETOOTH = "bluetooth";
    public static final String TYPE_WIFI = "wifi";

    private final List<CharSequence> dataTypes;

    public static List<ReplayEvent> importData(String zipPath, CharSequence[] dataTypes) {
        ImportDataManager manager = new ImportDataManager(dataTypes);

        List<File> files = FilesProvider.getFiles(zipPath, dataTypes);
        File[] filesArray = files.toArray(new File[files.size()]);
        List<ReplayEvent> events = EventsParser.getEvents(filesArray);
        List<ReplayEvent> sorted = manager.sortByTimestamp(events);

        return sorted;
    }

    private ImportDataManager(CharSequence[] types) {
        dataTypes = Arrays.asList(types);
    }

    private List<ReplayEvent> sortByTimestamp(List<ReplayEvent> events) {
        if (dataTypes.size() > 1) {
            Collections.sort(events, (o1, o2) -> Long.compare(o1.getNanoTimestamp(), o2.getNanoTimestamp()));
        }

        return events;
    }

}
