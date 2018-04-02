package eu.szwiec.replayview.replay;

import android.location.Location;

import com.annimon.stream.IntStream;
import com.annimon.stream.Stream;
import com.google.common.primitives.Doubles;
import com.google.common.primitives.Ints;
import com.google.common.primitives.Longs;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import eu.szwiec.replayview.otto.EddystoneUidPacketEvent;
import eu.szwiec.replayview.otto.NvGeofenceEvent;
import eu.szwiec.replayview.otto.SDKWifiScanResultEvent;
import eu.szwiec.replayview.otto.SensorArrayValuesEvent;

import static org.apache.commons.io.FilenameUtils.getExtension;

public class EventsParser {

    public static List<ReplayEvent> getEvents(List<File> files) {
        List<ReplayEvent> allEvents = new ArrayList<>();

        for (File file : files) {
            String extension = getExtensionWithoutNumber(file);

            try {
                if (extension.equals(Type.WIFI.getFileExtension())) {
                    allEvents.addAll(getWifiEvents(file));
                } else if (extension.equals(Type.BLUETOOTH.getFileExtension())) {
                    allEvents.addAll(getBluetoothEvents(file));
                } else if (extension.equals(Type.GPS.getFileExtension())) {
                    allEvents.addAll(getGpsEvents(file));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return allEvents;
    }

    private static String getExtensionWithoutNumber(File file) {
        String extenstion = getExtension(file.getName());
        return extenstion.replaceAll("[0-9]", "");
    }

    private static List<NvGeofenceEvent> getGpsEvents(File file) throws IOException {
        BufferedReader reader = getReader(file);
        List<NvGeofenceEvent> events = new ArrayList<>();

        for (String line = reader.readLine(); line != null; line = reader.readLine()) {
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

            events.add(event);
        }

        return events;
    }

    private static List<EddystoneUidPacketEvent> getBluetoothEvents(File file) throws IOException {
        BufferedReader reader = getReader(file);
        List<EddystoneUidPacketEvent> events = new ArrayList<>();

        for (String line = reader.readLine(); line != null; line = reader.readLine()) {
            if (line.startsWith("#")) {
                continue;
            }

            String[] parts = line.split(",");
            long timestamp = Long.parseLong(parts[0]);
            String deviceAddress = parts[2];
            int rssi = Integer.parseInt(parts[3]);

            EddystoneUidPacketEvent event = new EddystoneUidPacketEvent(deviceAddress, rssi, timestamp);

            events.add(event);
        }

        return events;
    }

    private static BufferedReader getReader(File file) throws FileNotFoundException {
        return new BufferedReader(new FileReader(file));
    }

    private static List<SDKWifiScanResultEvent> getWifiEvents(File file) throws IOException {
        BufferedReader reader = getReader(file);
        List<SDKWifiScanResultEvent> events = new ArrayList<>();

        for (String line = reader.readLine(); line != null; line = reader.readLine()) {
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

            events.add(event);
        }

        return events;
    }

    private static List<SensorArrayValuesEvent> getSensorEvents(File file) throws IOException {

        List<Long> time = new ArrayList<>();
        List<Integer> type = new ArrayList<>();
        List<Double> x = new ArrayList<>();
        List<Double> y = new ArrayList<>();
        List<Double> z = new ArrayList<>();

        List<Integer> newArrayIndexes = new ArrayList<>();

        BufferedReader reader = getReader(file);
        List<SensorArrayValuesEvent> events = new ArrayList<>();

        for (String line = reader.readLine(); line != null; line = reader.readLine()) {
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

            events.add(event);
        }

        return events;
    }
}
