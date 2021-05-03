package com.example.cp;

import java.sql.Timestamp;

public class ListItem {

    int Quantity;
    String Group;
    String Num;
    String Brand;
    String CatNum;
    String Name;
    float Price;
    String Available;
    String Customer;
    Timestamp Date;
    String Status;
    String orStatus;

    public String getNum() {
        return Num;
    }

    public void setNum(String num) {
        Num = num;
    }

    public String getOrStatus() {
        return orStatus;
    }

    public void setOrStatus(String orStatus) {
        this.orStatus = orStatus;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public int getQuantity() {
        return Quantity;
    }

    public void setQuantity(int quantity) {
        Quantity = quantity;
    }

    public String getCustomer() {
        return Customer;
    }

    public void setCustomer(String customer) {
        Customer = customer;
    }

    public Timestamp getDate() {
        return Date;
    }

    public void setDate(Timestamp date) {
        Date = date;
    }

    public String getBrand() {
        return Brand;
    }

    public void setBrand(String brand) {
        Brand = brand;
    }

    public String getGroup() {
        return Group;
    }

    public void setGroup(String group) {
        Group = group;
    }

    public String getCatNum() {
        return CatNum;
    }

    public void setCatNum(String catNum) {
        CatNum = catNum;
    }

    public String getAvailable() {
        return Available;
    }

    public void setAvailable(String available) {
        Available = available;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        this.Name = name;
    }

    public float getPrice() {
        return Price;
    }

    public void setPrice(float price) {
        this.Price = price;
    }
}
