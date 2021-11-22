package com.khtech.apporter.Models;

public class Cart {

    private String pid, pname, pprice, pquantity, storename;

    public Cart() {
    }

    public Cart(String pid, String pname, String pprice, String pquantity, String storename) {
        this.pid = pid;
        this.pname = pname;
        this.pprice = pprice;
        this.pquantity = pquantity;
        this.storename = storename;
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

    public String getStorename() {
        return storename;
    }

    public void setStorename(String storename) {
        this.storename = storename;
    }
}
