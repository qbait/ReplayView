package eu.szwiec.replayview.replay;

import android.location.Location;

import com.annimon.stream.IntStream;
import com.annimon.stream.Stream;
import com.google.common.primitives.Doubles;
import com.google.common.primitives.Ints;
import com.google.common.primitives.Longs;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import eu.szwiec.replayview.otto.EddystoneUidPacketEvent;
import eu.szwiec.replayview.otto.NvGeofenceEvent;
import eu.szwiec.replayview.otto.SDKWifiScanResultEvent;
import eu.szwiec.replayview.otto.SensorArrayValuesEvent;

public class ReplayEventsParser {

    public static List<ReplayEvent> getEvents(List<ReplayFile> files) {
        List<ReplayEvent> allEvents = new ArrayList<>();

        for (ReplayFile file : files) {
            try {
                InputStream stream = file.getStream();

                switch (file.getType()) {
                    case WIFI:
                        allEvents.addAll(getWifiEvents(stream));
                        break;
                    case BLUETOOTH:
                        allEvents.addAll(getBluetoothEvents(stream));
                        break;
                    case GPS:
                        allEvents.addAll(getGpsEvents(stream));
                        break;
                    default:
                        allEvents.addAll(getSensorEvents(stream));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return allEvents;
    }

    private static List<NvGeofenceEvent> getGpsEvents(InputStream stream) throws IOException {
        BufferedReader reader = getReader(stream);
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

    private static List<EddystoneUidPacketEvent> getBluetoothEvents(InputStream stream) throws IOException {
        BufferedReader reader = getReader(stream);
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

    private static BufferedReader getReader(InputStream stream) throws UnsupportedEncodingException {
        return new BufferedReader(new InputStreamReader(stream, "UTF-8"));
    }

    private static List<SDKWifiScanResultEvent> getWifiEvents(InputStream stream) throws IOException {
        BufferedReader reader = getReader(stream);
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

    private static List<SensorArrayValuesEvent> getSensorEvents(InputStream stream) throws IOException {

        List<Long> time = new ArrayList<>();
        List<Integer> type = new ArrayList<>();
        List<Double> x = new ArrayList<>();
        List<Double> y = new ArrayList<>();
        List<Double> z = new ArrayList<>();

        List<Integer> newArrayIndexes = new ArrayList<>();

        BufferedReader reader = getReader(stream);
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
