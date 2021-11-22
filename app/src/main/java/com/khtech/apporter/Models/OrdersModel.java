package com.khtech.apporter.Models;

public class OrdersModel {

  private String orderby,orderto,orderid,totalAmount,address,phone,date,time,orderstatus,acceptedby;

    public OrdersModel() {
    }

    public OrdersModel(String orderby, String orderto, String orderid, String totalAmount, String address, String phone, String date, String time, String orderstatus, String acceptedby) {
        this.orderby = orderby;
        this.orderto = orderto;
        this.orderid = orderid;
        this.totalAmount = totalAmount;
        this.address = address;
        this.phone = phone;
        this.date = date;
        this.time = time;
        this.orderstatus = orderstatus;
        this.acceptedby = acceptedby;
    }

    public String getOrderby() {
        return orderby;
    }

    public void setOrderby(String orderby) {
        this.orderby = orderby;
    }

    public String getOrderto() {
        return orderto;
    }

    public void setOrderto(String orderto) {
        this.orderto = orderto;
    }

    public String getOrderid() {
        return orderid;
    }

    public void setOrderid(String orderid) {
        this.orderid = orderid;
    }

    public String getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(String totalAmount) {
        this.totalAmount = totalAmount;
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

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getOrderstatus() {
        return orderstatus;
    }

    public void setOrderstatus(String orderstatus) {
        this.orderstatus = orderstatus;
    }

    public String getAcceptedby() {
        return acceptedby;
    }

    public void setAcceptedby(String acceptedby) {
        this.acceptedby = acceptedby;
    }
}
