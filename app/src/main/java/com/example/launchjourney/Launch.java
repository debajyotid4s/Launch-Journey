package com.example.launchjourney;

public class Launch {
    private String name;
    private int deck;
    private int scabin;
    private int vip;
    private String route;
    private String time;

    // Default constructor required for calls to DataSnapshot.getValue(Launch.class)
    public Launch() {
    }



    public Launch(String name, int vip, int deck, int scabin, String route, String time) {
        this.name = name;
        this.deck = deck;
        this.scabin = scabin;
        this.vip = vip;
        this.route = route;
        this.time = time;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getDeck() {
        return deck;
    }

    public void setDeck(int deck) {
        this.deck = deck;
    }

    public int getScabin() {
        return scabin;
    }

    public void setScabin(int scabin) {
        this.scabin = scabin;
    }

    public int getVip() {
        return vip;
    }

    public void setVip(int vip) {
        this.vip = vip;
    }

    public String getRoute() {
        return route;
    }

    public void setRoute(String route) {
        this.route = route;
    }
    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}