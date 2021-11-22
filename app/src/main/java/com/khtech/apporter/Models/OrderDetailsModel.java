package com.khtech.apporter.Models;

public class OrderDetailsModel {

    private String pid,pname,pprice,pquantity;

    public OrderDetailsModel() {
    }

    public OrderDetailsModel(String pid, String pname, String pprice, String pquantity) {
        this.pid = pid;
        this.pname = pname;
        this.pprice = pprice;
        this.pquantity = pquantity;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getPname() {
        return pname;
    }

    public void setPname(String pname) {
        this.pname = pname;
    }

    public String getPprice() {
        return pprice;
    }

    public void setPprice(String pprice) {
        this.pprice = pprice;
    }

    public String getPquantity() {
        return pquantity;
    }

    public void setPquantity(String pquantity) {
        this.pquantity = pquantity;
    }
}
