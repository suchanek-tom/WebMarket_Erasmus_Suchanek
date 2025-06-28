package com.webmarket.dao;

import com.webmarket.model.PurchaseProposal;
import com.webmarket.utils.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PurchaseProposalDAO {

    public void insert(PurchaseProposal proposal) {
        String sql = "INSERT INTO PurchaseProposal (request_id, technician_id, features, price, date, is_winner) VALUES (?, ?, ?, ?, ?, false)";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, proposal.getRequestId());
            stmt.setInt(2, proposal.getTechnicianId());
            stmt.setString(3, proposal.getFeatures());
            stmt.setDouble(4, proposal.getPrice());
            stmt.setDate(5, Date.valueOf(proposal.getDate()));

            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    proposal.setId(rs.getInt(1));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<PurchaseProposal> findByRequestId(int requestId) {
        List<PurchaseProposal> list = new ArrayList<>();
        String sql =
            "SELECT pp.*, u.username AS technician_name " +
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
                proposal.setTechnicianName(rs.getString("technician_name")); // <- důležité!
                proposal.setFeatures(rs.getString("features"));
                proposal.setPrice(rs.getDouble("price"));
                proposal.setDate(rs.getDate("date").toLocalDate());
                proposal.setWinner(rs.getBoolean("is_winner"));
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

    /**
     * Nastaví jeden návrh jako vítězný a odznačí všechny ostatní návrhy u stejné žádosti.
     */
    public boolean setWinner(int proposalId, int requestId) {
        String resetSql = "UPDATE PurchaseProposal SET is_winner = false WHERE request_id = ?";
        String setWinnerSql = "UPDATE PurchaseProposal SET is_winner = true WHERE id = ? AND request_id = ?";

        try (Connection conn = DBUtil.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement resetStmt = conn.prepareStatement(resetSql);
                 PreparedStatement winnerStmt = conn.prepareStatement(setWinnerSql)) {

                resetStmt.setInt(1, requestId);
                resetStmt.executeUpdate();

                winnerStmt.setInt(1, proposalId);
                winnerStmt.setInt(2, requestId);
                int updated = winnerStmt.executeUpdate();

                if (updated != 1) {
                    conn.rollback();
                    return false;
                }

                conn.commit();
                return true;
            } catch (SQLException e) {
                conn.rollback();
                e.printStackTrace();
                return false;
            } finally {
                conn.setAutoCommit(true);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Placeholder pro pozdější implementaci, pokud bude potřeba
    public boolean rejectProposal(int proposalId) {
        // Implementace dle potřeby
        return true;
    }
}
