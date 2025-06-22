package com.example.launchjourney;

public class User {
    public String name;
    public String address;
    public String email;
    public String password;
    public String contact;

    // Default constructor required for Firebase
    public User() {
    }

    public User(String name, String address, String password, String contact, String email) {
        this.name = name;
        this.address = address;
        this.contact = contact;
        this.email = email;
        this.password = password;
    }
}
