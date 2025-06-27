package com.webmarket.dao;

import com.webmarket.model.User;
import com.webmarket.utils.DBUtil;

import java.sql.*;

public class UserDAO {

    public User findByUsernameAndPassword(String username, String password) {
        User user = null;

        String sql = "SELECT * FROM user WHERE username = ? AND password = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                user = new User();
                user.setId(rs.getInt("id"));
                user.setUsername(rs.getString("username"));
                user.setRole(rs.getString("role"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return user;
    }
    
    public boolean insertUser(User user) {
    String sql = "INSERT INTO User (username, password, role) VALUES (?, ?, ?)";

    try (Connection conn = DBUtil.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setString(1, user.getUsername());
        stmt.setString(2, user.getPassword());
        stmt.setString(3, user.getRole());

        return stmt.executeUpdate() > 0;

    } catch (SQLException e) {
        e.printStackTrace();
        return false;
    }
}

}
