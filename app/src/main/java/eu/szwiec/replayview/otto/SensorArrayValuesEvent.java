package eu.szwiec.replayview.otto;


import eu.szwiec.replayview.replay.ReplayEvent;

/**
 * Created by damian on 29/07/2016.
 */
public class SensorArrayValuesEvent implements ReplayEvent {

    public int sensorCount;
    public long[] sensorTime;
    public int[] sensorType;
    public double[] sensorDataX;
    public double[] sensorDataY;
    public double[] sensorDataZ;
    public int accuracy;

    public SensorArrayValuesEvent(int accuracy, int sensorCount, long[] sensorTime, int[] sensorType, double[] sensorDataX, double[] sensorDataY, double[] sensorDataZ) {
        this.accuracy = accuracy;
        this.sensorCount = sensorCount;
        this.sensorTime = sensorTime;
        this.sensorType = sensorType;
        this.sensorDataX = sensorDataX;
        this.sensorDataY = sensorDataY;
        this.sensorDataZ = sensorDataZ;
    }

    @Override
    public long getNanoTimestamp() {
        return sensorTime[0];
    }
}
