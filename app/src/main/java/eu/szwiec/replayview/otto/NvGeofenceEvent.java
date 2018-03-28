package eu.szwiec.replayview.otto;

import android.location.Location;


import java.io.Serializable;

import eu.szwiec.replayview.replay.ReplayEvent;

/**
 * Created by damian on 25/01/2016.
 */
public class NvGeofenceEvent implements ReplayEvent, Serializable {

    public String name;

    public String transition;

    public Location location;

    public String venueId;

    public NvGeofenceEvent(String name, String transition, Location location, String venueId) {
        this.name = name;
        this.transition = transition;
        this.location = location;
        this.venueId = venueId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof NvGeofenceEvent)) {
            return false;
        }

        NvGeofenceEvent event = (NvGeofenceEvent) o;

        if (name != null ? !name.equals(event.name) : event.name != null) {
            return false;
        }
        if (transition != null ? !transition.equals(event.transition) : event.transition != null) {
            return false;
        }
        return venueId != null ? venueId.equals(event.venueId) : event.venueId == null;

    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (transition != null ? transition.hashCode() : 0);
        result = 31 * result + (venueId != null ? venueId.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "{\"geofence\": " +
            "{" +
            "\"name\":" + (name == null ? "null" : "\"" + name + "\"") + ", " +
            "\"transition\":" + (transition == null ? "null" : "\"" + transition + "\"") + ", " +
            "\"lat\":\"" + location.getLatitude() + "\"" + ", " +
            "\"lon\":\"" + location.getLongitude() + "\"" + ", " +
            "\"venueId\":\"" + venueId + "\"" +
            "}" +
            "}";
    }

    @Override
    public long getNanoTimestamp() {
        return location.getTime();
    }
}
