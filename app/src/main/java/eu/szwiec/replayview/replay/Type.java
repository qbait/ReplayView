package eu.szwiec.replayview.replay;

import android.hardware.Sensor;

public enum Type {


    GPS(100, "GPS", "gps"),
    BLUETOOTH(101, "Bluetooth", "edyuid"),
    WIFI(102, "Wifi", "wifi"),

    ACCELEROMETER(Sensor.TYPE_ACCELEROMETER, "Accelerometer", "sensor"),
    ACCELEROMETER_UNCALIBRATED(Sensor.TYPE_ACCELEROMETER_UNCALIBRATED, "Accelerometer Uncalibrated", "sensor"),
    MAGNETIC_FIELD(Sensor.TYPE_MAGNETIC_FIELD, "Magnetic field", "sensor"),
    MAGNETIC_FIELD_UNCALIBRATED(Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED, "Magnetic field Uncalibrated", "sensor"),
    MAGNETIC_PRESSURE(Sensor.TYPE_PRESSURE, "Pressure", "sensor"),
    MAGNETIC_GYROSCOPE(Sensor.TYPE_GYROSCOPE, "Gyroscope", "sensor"),
    MAGNETIC_GYROSCOPE_UNCALIBRATED(Sensor.TYPE_GYROSCOPE_UNCALIBRATED, "Gyroscope Uncalibrated", "sensor"),
    LIGHT(Sensor.TYPE_LIGHT, "Light", "sensor"),
    RELATIVE_HUMIDITY(Sensor.TYPE_RELATIVE_HUMIDITY, "Relative Humidity", "sensor"),
    AMBIENT_TEMPERATURE(Sensor.TYPE_AMBIENT_TEMPERATURE, "Ambient Temperature", "sensor"),
    PROXIMITY(Sensor.TYPE_PROXIMITY, "Proximity", "sensor");

    private int id;
    private String name;
    private String fileExtension;

    public static final String SENSOR_FILE_EXTENSION = "sensor";


    Type(int id, String name, String fileExtension) {
        this.id = id;
        this.name = name;
        this.fileExtension = fileExtension;
    }

    public static Type getType(String value) {
        if (value.equals(GPS.name)) return GPS;
        if (value.equals(BLUETOOTH.name)) return BLUETOOTH;
        if (value.equals(WIFI.name)) return WIFI;
        if (value.equals(ACCELEROMETER.name)) return ACCELEROMETER;
        if (value.equals(ACCELEROMETER_UNCALIBRATED.name)) return ACCELEROMETER_UNCALIBRATED;
        if (value.equals(MAGNETIC_FIELD.name)) return MAGNETIC_FIELD;
        if (value.equals(MAGNETIC_FIELD_UNCALIBRATED.name)) return MAGNETIC_FIELD_UNCALIBRATED;
        if (value.equals(MAGNETIC_PRESSURE.name)) return MAGNETIC_PRESSURE;
        if (value.equals(MAGNETIC_GYROSCOPE.name)) return MAGNETIC_GYROSCOPE;
        if (value.equals(MAGNETIC_GYROSCOPE_UNCALIBRATED.name))
            return MAGNETIC_GYROSCOPE_UNCALIBRATED;
        if (value.equals(LIGHT.name)) return LIGHT;
        if (value.equals(RELATIVE_HUMIDITY.name)) return RELATIVE_HUMIDITY;
        if (value.equals(AMBIENT_TEMPERATURE.name)) return AMBIENT_TEMPERATURE;
        if (value.equals(PROXIMITY.name)) return PROXIMITY;

        return null;
    }

    public int getId() {
        return id;
    }

    public String getFileExtension() {
        return fileExtension;
    }

    @Override
    public String toString() {
        return name;
    }
}
