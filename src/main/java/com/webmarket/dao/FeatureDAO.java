package com.webmarket.dao;

import com.webmarket.model.Feature;
import com.webmarket.utils.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FeatureDAO {

    public List<Feature> findAll() {
        List<Feature> features = new ArrayList<>();
        String sql = "SELECT id, name FROM Feature";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Feature f = new Feature();
                f.setId(rs.getInt("id"));
                f.setName(rs.getString("name"));
                features.add(f);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return features;
    }
}
