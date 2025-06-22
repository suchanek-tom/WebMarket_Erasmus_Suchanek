package com.webmarket.model;

public class PurchaseRequest {
    private int id;
    private int categoryId;
    private int purchaserId;
    private String notes;
    private String status;

    // Getters & Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getCategoryId() { return categoryId; }
    public void setCategoryId(int categoryId) { this.categoryId = categoryId; }

    public int getPurchaserId() { return purchaserId; }
    public void setPurchaserId(int purchaserId) { this.purchaserId = purchaserId; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
