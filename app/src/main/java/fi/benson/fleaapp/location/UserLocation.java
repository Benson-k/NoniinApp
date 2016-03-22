package fi.benson.fleaapp.location;

/**
 * Created by bkamau on 3/20/16.
 */
public class UserLocation {
    private double latitude;
    private  double longitude;
    private String address;

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public UserLocation() {

    }
}
