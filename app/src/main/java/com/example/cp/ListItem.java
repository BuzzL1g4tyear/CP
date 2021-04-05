package com.example.cp;

public class ListItem {

    String Group;
    String Brand;
    String CatNum;
    String Name;
    float Price;
    String Available;

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
