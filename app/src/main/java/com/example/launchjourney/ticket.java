package com.example.launchjourney;

public class ticket {
    private String launchName;
    private String route;
    private String date;
    private String time;
    private String ticketType;
    private String ticketQuantity;
    private  String paymentID;
    private String docID;
    //public ticket() {}
    public ticket(String launchName, String route, String date, String time, String ticketType, String ticketQuantity, String paymentID, String docID){
        this.launchName = launchName;
        this.route = route;
        this.date = date;
        this.time = time;
        this.ticketType = ticketType;
        this.ticketQuantity = ticketQuantity;
        this.paymentID = paymentID;
        this.docID = docID;
    }

    public void setDocID(String docID) {
        this.docID = docID;
    }

    public String getDocID() {
        return docID;
    }

    public String getLaunchName() {
        return launchName;
    }

    public void setLaunchName(String finalName) {
        this.launchName = launchName;
    }

    public String getRoute() {
        return route;
    }

    public void setRoute(String route) {
        this.route = route;
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

    public String getTicketType() {
        return ticketType;
    }

    public void setTicketType(String ticketType) {
        this.ticketType = ticketType;
    }

    public String getTicketQuantity() {
        return ticketQuantity;
    }

    public void setTicketQuantity(String ticketQuantity) {
        this.ticketQuantity = ticketQuantity;
    }

    public String getPaymentID() {
        return paymentID;
    }

    public void setPaymentID(String paymentID) {
        this.paymentID = paymentID;
    }
}
