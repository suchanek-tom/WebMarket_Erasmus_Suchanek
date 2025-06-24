package com.webmarket.dao;

import com.webmarket.model.PurchaseRequest;
import com.webmarket.utils.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PurchaseRequestDAO {

    public List<PurchaseRequest> findByPurchaserId(int purchaserId) {
        List<PurchaseRequest> list = new ArrayList<>();

        String sql = "SELECT pr.*, c.name AS category_name " +
                     "FROM purchase_requests pr " +
                     "JOIN categories c ON pr.category_id = c.id " +
                     "WHERE pr.purchaser_id = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, purchaserId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                PurchaseRequest pr = new PurchaseRequest();
                pr.setId(rs.getInt("id"));
                pr.setCategoryId(rs.getInt("category_id"));
                pr.setCategoryName(rs.getString("category_name"));
                pr.setPurchaserId(rs.getInt("purchaser_id"));
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
            stmt.setString(4, "pending"); // výchozí stav

            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<PurchaseRequest> findAllPendingWithCategoryName() {
        List<PurchaseRequest> list = new ArrayList<>();

        String sql = "SELECT pr.*, c.name AS category_name " +
                     "FROM purchase_requests pr " +
                     "JOIN categories c ON pr.category_id = c.id " +
                     "WHERE pr.status = 'pending'";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                PurchaseRequest pr = new PurchaseRequest();
                pr.setId(rs.getInt("id"));
                pr.setCategoryId(rs.getInt("category_id"));
                pr.setCategoryName(rs.getString("category_name"));
                pr.setPurchaserId(rs.getInt("purchaser_id"));
                pr.setNotes(rs.getString("notes"));
                pr.setStatus(rs.getString("status"));
                list.add(pr);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    public void updateStatus(int requestId, String newStatus) {
        String sql = "UPDATE purchase_requests SET status = ? WHERE id = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, newStatus);
            stmt.setInt(2, requestId);
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public List<PurchaseRequest> findAllWithCategoryAndUser() {
    List<PurchaseRequest> list = new ArrayList<>();
    String sql = "SELECT pr.id, pr.notes, pr.status, c.name AS category_name, u.username AS purchaser_name " +
                 "FROM purchase_requests pr " +
                 "JOIN categories c ON pr.category_id = c.id " +
                 "JOIN users u ON pr.purchaser_id = u.id";

    try (Connection conn = DBUtil.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql);
         ResultSet rs = stmt.executeQuery()) {

        while (rs.next()) {
            PurchaseRequest pr = new PurchaseRequest();
            pr.setId(rs.getInt("id"));
            pr.setNotes(rs.getString("notes"));
            pr.setStatus(rs.getString("status"));
            pr.setCategoryName(rs.getString("category_name"));
            pr.setPurchaserName(rs.getString("purchaser_name"));
            list.add(pr);
        }

    } catch (SQLException e) {
        e.printStackTrace();
    }

    return list;
    }
    
    public void deleteById(int requestId) {
    String sql = "DELETE FROM purchase_requests WHERE id = ?";

    try (Connection conn = DBUtil.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setInt(1, requestId);
        stmt.executeUpdate();

    } catch (SQLException e) {
        e.printStackTrace();
    }
   }
    public List<PurchaseRequest> findApprovedWithCategory() {
    List<PurchaseRequest> list = new ArrayList<>();
    String sql = "SELECT pr.*, c.name AS category_name " +
                 "FROM purchase_requests pr " +
                 "JOIN categories c ON pr.category_id = c.id " +
                 "WHERE pr.status = 'approved'";

    try (Connection conn = DBUtil.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql);
         ResultSet rs = stmt.executeQuery()) {

        while (rs.next()) {
            PurchaseRequest pr = new PurchaseRequest();
            pr.setId(rs.getInt("id"));
            pr.setCategoryId(rs.getInt("category_id"));
            pr.setCategoryName(rs.getString("category_name"));
            pr.setPurchaserId(rs.getInt("purchaser_id"));
            pr.setNotes(rs.getString("notes"));
            pr.setStatus(rs.getString("status"));
            list.add(pr);
        }

    } catch (SQLException e) {
        e.printStackTrace();
    }

    return list;
}
}
