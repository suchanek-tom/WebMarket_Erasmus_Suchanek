package com.webmarket.dao;

import com.webmarket.model.PurchaseProposal;
import com.webmarket.utils.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PurchaseProposalDAO {

    public void insert(PurchaseProposal proposal) {
        String sql = "INSERT INTO PurchaseProposal (request_id, technician_id, features, price, date) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, proposal.getRequestId());
            stmt.setInt(2, proposal.getTechnicianId());
            stmt.setString(3, proposal.getFeatures());
            stmt.setDouble(4, proposal.getPrice());
            stmt.setDate(5, Date.valueOf(proposal.getDate()));

            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<PurchaseProposal> findByRequestId(int requestId) {
        List<PurchaseProposal> list = new ArrayList<>();
        String sql = "SELECT pp.*, u.username AS technician_name " +
                     "FROM PurchaseProposal pp " +
                     "JOIN User u ON pp.technician_id = u.id " +
                     "WHERE pp.request_id = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, requestId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                PurchaseProposal proposal = new PurchaseProposal();
                proposal.setId(rs.getInt("id"));
                proposal.setRequestId(rs.getInt("request_id"));
                proposal.setTechnicianId(rs.getInt("technician_id"));
                proposal.setTechnicianName(rs.getString("technician_name"));
                proposal.setFeatures(rs.getString("features"));
                proposal.setPrice(rs.getDouble("price"));
                proposal.setDate(rs.getDate("date").toLocalDate());
                list.add(proposal);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    public boolean existsProposal(int requestId, int technicianId) {
        String sql = "SELECT COUNT(*) FROM PurchaseProposal WHERE request_id = ? AND technician_id = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, requestId);
            stmt.setInt(2, technicianId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }
}
