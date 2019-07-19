package sammie.com.truecrime.Model;

public class Users {

    private String fulName, gender,
            emerg_contact_one,
            emerg_contact_two,
            emerg_contact_three,
            currentId,
            userPhoneNumber,houseNumber,address;

    ;

    public Users() {
    }

    public Users(String fulName, String gender, String emerg_contact_one, String emerg_contact_two, String emerg_contact_three, String currentId, String userPhoneNumber, String houseNumber, String address) {
        this.fulName = fulName;
        this.gender = gender;
        this.emerg_contact_one = emerg_contact_one;
        this.emerg_contact_two = emerg_contact_two;
        this.emerg_contact_three = emerg_contact_three;
        this.currentId = currentId;
        this.userPhoneNumber = userPhoneNumber;
        this.houseNumber = houseNumber;
        this.address = address;
    }

    public String getFulName() {
        return fulName;
    }

    public void setFulName(String fulName) {
        this.fulName = fulName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getEmerg_contact_one() {
        return emerg_contact_one;
    }

    public void setEmerg_contact_one(String emerg_contact_one) {
        this.emerg_contact_one = emerg_contact_one;
    }

    public String getEmerg_contact_two() {
        return emerg_contact_two;
    }

    public void setEmerg_contact_two(String emerg_contact_two) {
        this.emerg_contact_two = emerg_contact_two;
    }

    public String getEmerg_contact_three() {
        return emerg_contact_three;
    }

    public void setEmerg_contact_three(String emerg_contact_three) {
        this.emerg_contact_three = emerg_contact_three;
    }

    public String getCurrentId() {
        return currentId;
    }

    public void setCurrentId(String currentId) {
        this.currentId = currentId;
    }

    public String getUserPhoneNumber() {
        return userPhoneNumber;
    }

    public void setUserPhoneNumber(String userPhoneNumber) {
        this.userPhoneNumber = userPhoneNumber;
    }

    public String getHouseNumber() {
        return houseNumber;
    }

    public void setHouseNumber(String houseNumber) {
        this.houseNumber = houseNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
