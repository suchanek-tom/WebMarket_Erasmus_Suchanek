package com.webmarket.dao;

import com.webmarket.model.PurchaseRequest;
import com.webmarket.utils.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PurchaseRequestDAO {

    public List<PurchaseRequest> findByPurchaserId(int purchaserId) {
        List<PurchaseRequest> list = new ArrayList<>();

        String sql = "SELECT * FROM purchase_requests WHERE purchaser_id = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, purchaserId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                PurchaseRequest pr = new PurchaseRequest();
                pr.setId(rs.getInt("id"));
                pr.setCategoryId(rs.getInt("category_id"));
                pr.setPurchaserId(purchaserId);
                pr.setNotes(rs.getString("notes"));
                pr.setStatus(rs.getString("status"));
                list.add(pr);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    public void insert(PurchaseRequest pr) {
        String sql = "INSERT INTO purchase_requests (category_id, purchaser_id, notes, status) VALUES (?, ?, ?, ?)";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, pr.getCategoryId());
            stmt.setInt(2, pr.getPurchaserId());
            stmt.setString(3, pr.getNotes());
            stmt.setString(4, "pending"); 

            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
