package com.webmarket.dao;

import com.webmarket.model.PurchaseRequest;
import com.webmarket.utils.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PurchaseRequestDAO {

    public void insert(PurchaseRequest request) {
    String sql = "INSERT INTO PurchaseRequest (purchaser_id, category_id, notes, status) VALUES (?, ?, ?, ?)";

    try (Connection conn = DBUtil.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

        stmt.setInt(1, request.getPurchaserId());
        stmt.setInt(2, request.getCategoryId());
        stmt.setString(3, request.getNotes());
        stmt.setString(4, request.getStatus());

        stmt.executeUpdate();

        ResultSet rs = stmt.getGeneratedKeys();
        if (rs.next()) {
            request.setId(rs.getInt(1));
        }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }   

    public List<PurchaseRequest> findByPurchaserId(int purchaserId) {
        List<PurchaseRequest> list = new ArrayList<>();
        String sql =
            "SELECT pr.*, c.name AS category_name " +
            "FROM PurchaseRequest pr " +
            "JOIN Category c ON pr.category_id = c.id " +
            "WHERE pr.purchaser_id = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, purchaserId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                PurchaseRequest req = new PurchaseRequest();
                req.setId(rs.getInt("id"));
                req.setPurchaserId(rs.getInt("purchaser_id"));
                req.setCategoryId(rs.getInt("category_id"));
                req.setNotes(rs.getString("notes"));
                req.setStatus(rs.getString("status"));
                req.setCategoryName(rs.getString("category_name"));
                list.add(req);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    public List<PurchaseRequest> findAllPendingWithCategoryName() {
        List<PurchaseRequest> list = new ArrayList<>();
        String sql =
            "SELECT pr.*, c.name AS category_name " +
            "FROM PurchaseRequest pr " +
            "JOIN Category c ON pr.category_id = c.id " +
            "WHERE pr.status = 'pending'";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                PurchaseRequest req = new PurchaseRequest();
                req.setId(rs.getInt("id"));
                req.setPurchaserId(rs.getInt("purchaser_id"));
                req.setCategoryId(rs.getInt("category_id"));
                req.setNotes(rs.getString("notes"));
                req.setStatus(rs.getString("status"));
                req.setCategoryName(rs.getString("category_name"));
                list.add(req);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    public List<PurchaseRequest> findAllWithCategoryAndUser() {
        List<PurchaseRequest> list = new ArrayList<>();
        String sql =
            "SELECT pr.*, c.name AS category_name, u.username AS purchaser_name " +
            "FROM PurchaseRequest pr " +
            "JOIN Category c ON pr.category_id = c.id " +
            "JOIN User u ON pr.purchaser_id = u.id";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                PurchaseRequest req = new PurchaseRequest();
                req.setId(rs.getInt("id"));
                req.setPurchaserId(rs.getInt("purchaser_id"));
                req.setCategoryId(rs.getInt("category_id"));
                req.setNotes(rs.getString("notes"));
                req.setStatus(rs.getString("status"));
                req.setCategoryName(rs.getString("category_name"));
                req.setPurchaserName(rs.getString("purchaser_name"));
                list.add(req);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    public PurchaseRequest findById(int id) {
        String sql = "SELECT * FROM PurchaseRequest WHERE id = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                PurchaseRequest req = new PurchaseRequest();
                req.setId(rs.getInt("id"));
                req.setPurchaserId(rs.getInt("purchaser_id"));
                req.setCategoryId(rs.getInt("category_id"));
                req.setNotes(rs.getString("notes"));
                req.setStatus(rs.getString("status"));
                return req;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public PurchaseRequest findByIdWithCategoryAndUser(int id) {
        String sql =
            "SELECT pr.*, c.name AS category_name, u.username AS purchaser_name " +
            "FROM PurchaseRequest pr " +
            "JOIN Category c ON pr.category_id = c.id " +
            "JOIN User u ON pr.purchaser_id = u.id " +
            "WHERE pr.id = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                PurchaseRequest req = new PurchaseRequest();
                req.setId(rs.getInt("id"));
                req.setPurchaserId(rs.getInt("purchaser_id"));
                req.setCategoryId(rs.getInt("category_id"));
                req.setNotes(rs.getString("notes"));
                req.setStatus(rs.getString("status"));
                req.setCategoryName(rs.getString("category_name"));
                req.setPurchaserName(rs.getString("purchaser_name"));
                return req;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public boolean updateStatus(int requestId, String newStatus) {
        String sql = "UPDATE PurchaseRequest SET status = ? WHERE id = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, newStatus);
            stmt.setInt(2, requestId);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteById(int id) {
        String sql = "DELETE FROM PurchaseRequest WHERE id = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public List<PurchaseRequest> findUnassignedRequests() {
        List<PurchaseRequest> list = new ArrayList<>();
        String sql = 
            "SELECT pr.*, c.name AS category_name" +
            "FROM PurchaseRequest pr" +
            "JOIN Category c ON pr.category_id = c.id" +
            "WHERE pr.technician_id IS NULL";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                PurchaseRequest req = new PurchaseRequest();
                req.setId(rs.getInt("id"));
                req.setPurchaserId(rs.getInt("purchaser_id"));
                req.setCategoryId(rs.getInt("category_id"));
                req.setNotes(rs.getString("notes"));
                req.setStatus(rs.getString("status"));
                req.setCategoryName(rs.getString("category_name"));
                list.add(req);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }
    public List<PurchaseRequest> findByTechnicianId(int technicianId) {
        List<PurchaseRequest> list = new ArrayList<>();
        String sql =
            "SELECT pr.*, c.name AS category_name" +
            "FROM PurchaseRequest pr" +
            "JOIN Category c ON pr.category_id = c.id" +
            "WHERE pr.technician_id = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, technicianId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                PurchaseRequest req = new PurchaseRequest();
                req.setId(rs.getInt("id"));
                req.setPurchaserId(rs.getInt("purchaser_id"));
                req.setCategoryId(rs.getInt("category_id"));
                req.setNotes(rs.getString("notes"));
                req.setStatus(rs.getString("status"));
                req.setCategoryName(rs.getString("category_name"));
                list.add(req);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }
    
    public boolean assignTechnician(int requestId, int technicianId) {
        String sql = "UPDATE PurchaseRequest SET technician_id = ? WHERE id = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, technicianId);
            stmt.setInt(2, requestId);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
}
