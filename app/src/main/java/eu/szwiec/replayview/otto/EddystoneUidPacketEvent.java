package eu.szwiec.replayview.otto;


import eu.szwiec.replayview.replay.ReplayEvent;

/**
 * Created by damian on 10/01/2016.
 */
public class EddystoneUidPacketEvent implements ReplayEvent {

    public String deviceAddress;
    public int rssi;
    public String id;
    public long timestamp;

    public EddystoneUidPacketEvent(String deviceAddress, int rssi, String id) {
        this.rssi = rssi;
        this.deviceAddress = deviceAddress;
        this.id = id;
        timestamp = System.currentTimeMillis();
    }

    public EddystoneUidPacketEvent(String deviceAddress, int rssi, long timestamp) {
        this.rssi = rssi;
        this.deviceAddress = deviceAddress;
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "EddystoneUidPacketEvent{"
                + "mDeviceAddress='" + deviceAddress + '\''
                + ", rssi=" + rssi
                + ", id='" + id + '\''
                + ", timestamp=" + timestamp
                + '}';
    }

    @Override
    public long getNanoTimestamp() {
        return timestamp;
    }
}
