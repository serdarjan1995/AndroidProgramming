package semesterproject.mobileprogramming.com.trackme;

/**
 * Created by Sardor on 10/06/2017.
 */

public class LocationInfo {

    private double longitude;
    private double latitude;
    private double altitude;
    private String time;



    private String activityType;

    public LocationInfo(double alt, double lng, double lat, String time, String activityType){
        this.longitude = lng;
        this.latitude = lat;
        this.altitude = alt;
        this.time = time;
        this.activityType = activityType;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longtitude) {
        this.longitude = longtitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getAltitude() {
        return altitude;
    }

    public void setAltitude(double altitude) {
        this.altitude = altitude;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getActivityType() {
        return activityType;
    }

    public void setActivityType(String activityType) {
        this.activityType = activityType;
    }

    public String toString(){ return "Lat:"+getLatitude()+" Lng:"+getLongitude()+" Alt:"+getAltitude()
                                + " Time:"+getTime()+ " Activity type: "+getActivityType();}
}
