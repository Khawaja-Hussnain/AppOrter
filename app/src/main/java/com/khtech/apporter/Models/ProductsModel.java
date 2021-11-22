package com.khtech.apporter.Models;

public class ProductsModel {

    private String pname,pdescription,pimage,pprice,city,storename,pid;

    public ProductsModel() {
    }

    public ProductsModel(String pname, String pdescription, String pimage, String pprice, String city, String storename, String pid) {
        this.pname = pname;
        this.pdescription = pdescription;
        this.pimage = pimage;
        this.pprice = pprice;
        this.city = city;
        this.storename = storename;
        this.pid = pid;
    }

    public String getPname() {
        return pname;
    }

    public void setPname(String pname) {
        this.pname = pname;
    }

    public String getPdescription() {
        return pdescription;
    }

    public void setPdescription(String pdescription) {
        this.pdescription = pdescription;
    }

    public String getPimage() {
        return pimage;
    }

    public void setPimage(String pimage) {
        this.pimage = pimage;
    }

    public String getPprice() {
        return pprice;
    }

    public void setPprice(String pprice) {
        this.pprice = pprice;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getStorename() {
        return storename;
    }

    public void setStorename(String storename) {
        this.storename = storename;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }
}
