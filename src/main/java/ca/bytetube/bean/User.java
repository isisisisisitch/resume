package ca.bytetube.bean;

import ca.bytetube.bean.base.Bean;
import ca.bytetube.util.Times;

import java.util.Date;

public class User extends Bean {
    private String password;
    private String email;
    private Date birthday;
    private String photo;
    private String intro;
    private String name;
    private String address;
    private String phone;
    private String job;
    //逗号隔开
    private String trait;
    //逗号隔开
    private String interests;

    public String[] getTraitList() {
        return trait == null ? null : trait.split(",");
    }

    public String[] getInterestsList() {
        return interests == null ? null : interests.split(",");
    }

    public String getBirthdayString() {
        return Times.formatDate(birthday);
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


    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public String getTrait() {
        return trait;
    }

    public void setTrait(String trait) {
        this.trait = trait;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public String getInterests() {
        return interests;
    }

    public void setInterests(String interests) {
        this.interests = interests;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }


}
