package cubex.mahesh.yoursaloon.pojos;

/**
 * Created by srikanthk on 10/13/2018.
 */

public class UserPojo {
   public String Cname;
    String password;
    String email;
    String mobilenumber;
    String city;
    String fcm_id;
//    String cnameup;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    String type;

    public UserPojo() {
    }

    String profile_pic;

    public UserPojo(String username, String password, String email, String mobilenumber, String city, String fcm_id, String profile_pic,String type) {
        this.Cname = username;
        this.password = password;
        this.email = email;
        this.mobilenumber = mobilenumber;
        this.city = city;
        this.fcm_id = fcm_id;
        this.profile_pic = profile_pic;
        this.type = type;
//        this.cnameup = cnameup;
    }

    public String getUsername() {
        return Cname;
    }

    public void setUsername(String username) {
        this.Cname = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobilenumber() {
        return mobilenumber;
    }

    public void setMobilenumber(String mobilenumber) {
        this.mobilenumber = mobilenumber;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getFcm_id() {
        return fcm_id;
    }

    public void setFcm_id(String fcm_id) {
        this.fcm_id = fcm_id;
    }

    public String getProfile_pic() {
        return profile_pic;
    }

    public void setProfile_pic(String profile_pic) {
        this.profile_pic = profile_pic;
    }
}
