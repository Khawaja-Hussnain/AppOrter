package com.khtech.apporter.Models;

public class Riders {

    private String name, email, phone, uid, city;

    public Riders() {
    }

    public Riders(String name, String email, String phone, String uid, String city) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.uid = uid;
        this.city = city;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
}
