package eu.szwiec.replayview.replay

import android.hardware.Sensor

enum class Type constructor(val id: Int, val label: String, val fileExtension: String) {

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

    override fun toString(): String {
        return label
    }

    companion object {

        fun getType(label: String): Type? {

            return when(label) {
                GPS.label -> GPS
                BLUETOOTH.label -> BLUETOOTH
                WIFI.label -> WIFI
                ACCELEROMETER.label -> ACCELEROMETER
                ACCELEROMETER_UNCALIBRATED.label -> ACCELEROMETER_UNCALIBRATED
                MAGNETIC_FIELD.label -> MAGNETIC_FIELD
                MAGNETIC_FIELD_UNCALIBRATED.label -> MAGNETIC_FIELD_UNCALIBRATED
                MAGNETIC_PRESSURE.label -> MAGNETIC_PRESSURE
                MAGNETIC_GYROSCOPE.label -> MAGNETIC_GYROSCOPE
                MAGNETIC_GYROSCOPE_UNCALIBRATED.label -> MAGNETIC_GYROSCOPE_UNCALIBRATED
                LIGHT.label -> LIGHT
                RELATIVE_HUMIDITY.label -> RELATIVE_HUMIDITY
                PROXIMITY.label -> PROXIMITY
                else -> null
            }
        }
    }
}
