package eu.szwiec.replayview.replay;

import android.location.Location;

import com.annimon.stream.IntStream;
import com.annimon.stream.Stream;
import com.google.common.primitives.Doubles;
import com.google.common.primitives.Ints;
import com.google.common.primitives.Longs;

import org.apache.commons.io.FilenameUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import eu.szwiec.replayview.FileUtils;
import eu.szwiec.replayview.SDKConstants;
import eu.szwiec.replayview.otto.EddystoneUidPacketEvent;
import eu.szwiec.replayview.otto.NvGeofenceEvent;
import eu.szwiec.replayview.otto.SDKWifiScanResultEvent;
import eu.szwiec.replayview.otto.SensorArrayValuesEvent;

/**
 * Created by szwiec on 07/07/2017.
 */

public class ImportDataManager {

    static final String TYPE_GPS = "gps";
    static final String TYPE_SENSOR = "sensor";
    static final String TYPE_BLUETOOTH = "bluetooth";
    static final String TYPE_WIFI = "wifi";

    private File mDir;
    private final List<ReplayEvent> mData = new ArrayList<>();

    public List<ReplayEvent> importData(String zipPath, CharSequence[] dataTypes) throws IOException {

        mData.clear();

        String destinationPath = getDestinationPath(zipPath);
        if (FileUtils.unzip(zipPath, destinationPath)) {
            mDir = new File(destinationPath);
        }

        List<CharSequence> dataTypesList = Arrays.asList(dataTypes);

        if (dataTypesList.contains(TYPE_WIFI)) {
            for (File file : getFiles("wifi")) {
                importWifi(file);
            }
        }

        if (dataTypesList.contains(TYPE_BLUETOOTH)) {
            for (File file : getFiles("edyuid")) {
                importBluetooth(file);
            }
        }

        if (dataTypesList.contains(TYPE_SENSOR)) {
            for (File file : getFiles("sensor")) {
                importSensors(file);
            }
        }

        if (dataTypesList.contains(TYPE_GPS)) {
            for (File file : getFiles("gps")) {
                importGps(file);
            }
        }

        if (dataTypesList.size() > 1) {
            Collections.sort(mData, (o1, o2) -> Long.compare(o1.getNanoTimestamp(), o2.getNanoTimestamp()));
        }

        return mData;
    }

    private String getDestinationPath(String zipPath) {
        String filenameWithExtension = zipPath.substring(zipPath.lastIndexOf("/") + 1);
        return SDKConstants.SDCARD_PATH + FilenameUtils.removeExtension(filenameWithExtension);
    }

    private List<File> getFiles(String type) {
        List<File> files = new ArrayList<>();

        String[] names = mDir.list(
                (dir, name) -> {
                    String regex = String.format(".*\\.%s\\d+", type);
                    return name.matches(regex);
                });

        for (String name : names) {
            File file = new File(mDir + "/" + name);
            files.add(file);
        }

        return files;
    }

    private void importGps(File file) throws IOException {
        BufferedReader reader = getReader(file);
        String line;
        while ((line = reader.readLine()) != null) {
            if (line.startsWith("#")) {
                continue;
            }

            String[] parts = line.split(",");
            long time = Long.parseLong(parts[0]);
            double lat = Double.parseDouble(parts[2]);
            double lon = Double.parseDouble(parts[3]);
            float accuracy = Float.parseFloat(parts[4]);

            Location location = new Location("gps");
            location.setLatitude(lat);
            location.setLongitude(lon);
            location.setAccuracy(accuracy);
            location.setTime(time);

            NvGeofenceEvent event = null;//= FenceUtils.getGeofenceEvent(PreferenceManager.getDefaultSharedPreferences(mContext), new Gson(), location);

            mData.add(event);
        }
    }

    private void importBluetooth(File file) throws IOException {
        BufferedReader reader = getReader(file);
        String line;
        while ((line = reader.readLine()) != null) {
            if (line.startsWith("#")) {
                continue;
            }

            String[] parts = line.split(",");
            long timestamp = Long.parseLong(parts[0]);
            String deviceAddress = parts[2];
            int rssi = Integer.parseInt(parts[3]);

            EddystoneUidPacketEvent event = new EddystoneUidPacketEvent(deviceAddress, rssi, timestamp);

            mData.add(event);
        }
    }

    private BufferedReader getReader(File file) throws FileNotFoundException {
        return new BufferedReader(new FileReader(file));
    }

    private void importWifi(File file) throws IOException {
        BufferedReader reader = getReader(file);
        String line;
        while ((line = reader.readLine()) != null) {
            if (line.startsWith("#")) {
                continue;
            }

            String[] wifiParts = line.split(",");
            String[] strMacAddr = Stream.range(3, wifiParts.length)
                    .filter(n -> n % 2 == 1)
                    .map(i -> wifiParts[i])
                    .toArray(String[]::new);
            int[] rss = IntStream.range(3, wifiParts.length)
                    .filter(n -> n % 2 == 0)
                    .map(i -> Integer.parseInt(wifiParts[i]))
                    .toArray();
            long[] macAddr = Stream.of(strMacAddr)
                    .mapToLong(s -> {
                        String[] sAddr = s.split(":");
                        long lmacAddr = Long.parseLong(sAddr[0], 16);
                        for (int index = 1; index < sAddr.length; index++) {
                            lmacAddr <<= 8;
                            lmacAddr += Long.parseLong(sAddr[index], 16);
                        }
                        return lmacAddr;
                    }).toArray();
            long nanotime = Long.parseLong(wifiParts[0]);

            SDKWifiScanResultEvent event = new SDKWifiScanResultEvent(nanotime,
                    Long.parseLong(wifiParts[1]),
                    Integer.parseInt(wifiParts[2]),
                    macAddr, strMacAddr, rss, true, 0);

            mData.add(event);
        }
    }

    private void importSensors(File file) throws IOException {

        List<Long> time = new ArrayList<>();
        List<Integer> type = new ArrayList<>();
        List<Double> x = new ArrayList<>();
        List<Double> y = new ArrayList<>();
        List<Double> z = new ArrayList<>();

        List<Integer> newArrayIndexes = new ArrayList<>();

        BufferedReader reader = getReader(file);
        String line;
        while ((line = reader.readLine()) != null) {
            if (line.startsWith("#")) {
                continue;
            }

            String[] parts = line.split(",");
            long timestamp = Long.parseLong(parts[0]);

            if (newArrayIndexes.size() == 0 || timestamp - time.get(newArrayIndexes.get(newArrayIndexes.size() - 1)) > (long) 500 * 1000000) {
                newArrayIndexes.add(time.size());
            }

            time.add(timestamp);
            type.add(Integer.parseInt(parts[1]));
            x.add(Double.parseDouble(parts[2]));
            y.add(Double.parseDouble(parts[3]));
            z.add(Double.parseDouble(parts[4]));
        }

        for (int i = 1; i < newArrayIndexes.size(); i++) {
            int fromIndex = newArrayIndexes.get(i - 1);
            int toIndex = newArrayIndexes.get(i);

            long[] timeArray = Longs.toArray(time.subList(fromIndex, toIndex));
            int[] typeArray = Ints.toArray(type.subList(fromIndex, toIndex));
            double[] xArray = Doubles.toArray(x.subList(fromIndex, toIndex));
            double[] yArray = Doubles.toArray(y.subList(fromIndex, toIndex));
            double[] zArray = Doubles.toArray(z.subList(fromIndex, toIndex));

            SensorArrayValuesEvent event = new SensorArrayValuesEvent(0, 0, timeArray, typeArray, xArray, yArray, zArray);

            mData.add(event);
        }
    }

}
