package com.webmarket.model;

import java.time.LocalDate;

public class PurchaseProposal {

    private int id;
    private int requestId;
    private int technicianId;
    private String technicianName;
    private String features;
    private double price;
    private LocalDate date;
    private boolean isWinner;

    // Gettery a settery
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRequestId() {
        return requestId;
    }

    public void setRequestId(int requestId) {
        this.requestId = requestId;
    }

    public int getTechnicianId() {
        return technicianId;
    }

    public void setTechnicianId(int technicianId) {
        this.technicianId = technicianId;
    }

    public String getTechnicianName() {
        return technicianName;
    }

    public void setTechnicianName(String technicianName) {
        this.technicianName = technicianName;
    }

    public String getFeatures() {
        return features;
    }

    public void setFeatures(String features) {
        this.features = features;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }
    
    public boolean isWinner() {
        return isWinner;
    }
    
    public void setWinner(boolean isWinner) {
        this.isWinner = isWinner;
    }
}
