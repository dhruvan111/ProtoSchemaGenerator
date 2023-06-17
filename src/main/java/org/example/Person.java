package org.example;

import org.example.edu.edusrc.School;
import org.example.livinglife.People;
import org.example.livinglife.info.Address;

import java.net.Inet4Address;
import java.util.List;

public class Person extends People {
    int id;
    String name;
    Long phoneNumber;
    List<List<List<Address>>> address;

    Person(int id,String  name,Long phoneNumber,List<List<List<Address>>> address){
        this.id = id;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.address = address;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(Long phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public List<List<List<Address>>> getAddress() {
        return address;
    }

    public void setAddress(List<List<List<Address>>> address) {
        this.address = address;
    }
}
