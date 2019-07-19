package sammie.com.truecrime.Model;

public class EmergencyRequest {
    private String message,username,lat,lng;
    private String TimeStamp;


    public EmergencyRequest() {
    }

    public EmergencyRequest(String message, String username, String lat, String lng) {
        this.message = message;
        this.username = username;
        this.lat = lat;
        this.lng = lng;
    }

    public EmergencyRequest(String timeStamp) {
        TimeStamp = timeStamp;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUsername(String usersname) {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public String getUsername() {
        return username;
    }

    public String getTimeStamp() {
        return TimeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        TimeStamp = timeStamp;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }
}
