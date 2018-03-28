package eu.szwiec.replayview.otto;


import java.util.Arrays;

import eu.szwiec.replayview.replay.ReplayEvent;

/**
 * Created by damian on 02/08/2016.
 */
//@SuppressFBWarnings
public class SDKWifiScanResultEvent implements ReplayEvent {

    public long nanotime;

    public long millitime;

    public int scanSize;

    public long[] macAddr;

    public String[] strMacAddr;

    public int[] rss;

    public boolean isMock;

    public long scanStartTime;

    public SDKWifiScanResultEvent() {
    }

    public SDKWifiScanResultEvent(long nanotime, long millitime, int scanSize, long[] macAddr, String[] strMacAddr, int[] rss, boolean isMock, long scanStartTime) {
        this.nanotime = nanotime;
        this.millitime = millitime;
        this.scanSize = scanSize;
        this.macAddr = macAddr;
        this.strMacAddr = strMacAddr;
        this.rss = rss;
        this.isMock = isMock;

        //if scan time not provided e.g. because provided through replay tool then just set to event time - 4s
        if (scanStartTime == 0) {
            this.scanStartTime = millitime - 4000;
        } else {
            this.scanStartTime = scanStartTime;
        }
    }

    public SDKWifiScanResultEvent(long nanotime, long millitime, int scanSize, long[] macAddr, String[] strMacAddr, int[] rss, long scanStartTime) {
        this(nanotime, millitime, scanSize, macAddr, strMacAddr, rss, false, scanStartTime);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SDKWifiScanResultEvent)) {
            return false;
        }

        SDKWifiScanResultEvent that = (SDKWifiScanResultEvent) o;

        if (nanotime != that.nanotime) {
            return false;
        }
        if (millitime != that.millitime) {
            return false;
        }
        if (scanSize != that.scanSize) {
            return false;
        }
        if (!Arrays.equals(macAddr, that.macAddr)) {
            return false;
        }
        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        if (!Arrays.equals(strMacAddr, that.strMacAddr)) {
            return false;
        }
        return Arrays.equals(rss, that.rss);

    }

    @Override
    public int hashCode() {
        int result = (int) (nanotime ^ (nanotime >>> 32));
        result = 31 * result + (int) (millitime ^ (millitime >>> 32));
        result = 31 * result + scanSize;
        return result;
    }

    @Override
    public String toString() {
        return "SDKWifiScanResultEvent{" +
            "nanotime=" + nanotime +
            ", millitime=" + millitime +
            ", scanSize=" + scanSize +
            ", macAddr=" + Arrays.toString(macAddr) +
            ", strMacAddr=" + Arrays.toString(strMacAddr) +
            ", rss=" + Arrays.toString(rss) +
            ", isMock=" + isMock +
            ", scanStartTime=" + scanStartTime +
            ", duration=" + (millitime - scanStartTime / 1000) +
            '}';
    }

    @Override
    public long getNanoTimestamp() {
        return nanotime;
    }
}
