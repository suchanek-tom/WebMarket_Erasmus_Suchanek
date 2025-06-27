package com.webmarket.model;

public class PurchaseRequest {
    private int id;
    private int categoryId;
    private int purchaserId;
    private String notes;
    private String status;
    private String categoryName;
    private String purchaserName;
    private Integer assignedTechnicianId; 

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

    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }

    public String getPurchaserName() { return purchaserName; }
    public void setPurchaserName(String purchaserName) { this.purchaserName = purchaserName; }

    public Integer getAssignedTechnicianId() { return assignedTechnicianId; }
    public void setAssignedTechnicianId(Integer assignedTechnicianId) { this.assignedTechnicianId = assignedTechnicianId; }
}
