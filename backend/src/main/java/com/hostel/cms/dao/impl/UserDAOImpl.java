package com.hostel.cms.dao.impl;

import com.hostel.cms.dao.UserDAO;
import com.hostel.cms.model.User;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

@Repository
public class UserDAOImpl implements UserDAO {

    private final JdbcTemplate jdbcTemplate;

    public UserDAOImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<User> userRowMapper = (rs, rowNum) -> {
        User user = new User();
        user.setUserId(rs.getInt("user_id"));
        user.setName(rs.getString("name"));
        user.setEmail(rs.getString("email"));
        user.setRoomNumber(rs.getString("room_number"));
        user.setHostelBlock(rs.getString("hostel_block"));
        user.setRole(rs.getString("role"));
        user.setCreatedAt(rs.getTimestamp("created_at"));
        return user;
    };

    @Override
    public List<User> findAll() {
        String sql = "SELECT user_id, name, email, room_number, hostel_block, role, created_at FROM users ORDER BY user_id ASC";
        return jdbcTemplate.query(sql, userRowMapper);
    }

    @Override
    public Optional<User> findById(int userId) {
        String sql = "SELECT user_id, name, email, room_number, hostel_block, role, created_at FROM users WHERE user_id = ?";
        List<User> users = jdbcTemplate.query(sql, userRowMapper, userId);
        return users.stream().findFirst();
    }

    @Override
    public Optional<User> findByEmail(String email) {
        String sql = "SELECT user_id, name, email, room_number, hostel_block, role, created_at FROM users WHERE LOWER(email) = LOWER(?)";
        List<User> users = jdbcTemplate.query(sql, userRowMapper, email);
        return users.stream().findFirst();
    }

    @Override
    public User save(User user) {
        String sql = "INSERT INTO users (name, email, room_number, hostel_block, role) VALUES (?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"user_id"});
            ps.setString(1, user.getName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getRoomNumber() != null ? user.getRoomNumber() : "A-101");
            ps.setString(4, user.getHostelBlock() != null ? user.getHostelBlock() : "Block A");
            ps.setString(5, user.getRole() != null ? user.getRole() : "STUDENT");
            return ps;
        }, keyHolder);

        Number key = null;
        try {
            key = keyHolder.getKey();
        } catch (Exception e) {
            if (keyHolder.getKeys() != null && !keyHolder.getKeys().isEmpty()) {
                Object val = keyHolder.getKeys().values().iterator().next();
                if (val instanceof Number) key = (Number) val;
            }
        }
        if (key != null) {
            user.setUserId(key.intValue());
        }
        return user;
    }
}
