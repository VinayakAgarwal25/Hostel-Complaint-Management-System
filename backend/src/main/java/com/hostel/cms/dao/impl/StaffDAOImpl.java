package com.hostel.cms.dao.impl;

import com.hostel.cms.dao.StaffDAO;
import com.hostel.cms.dto.StaffPerformanceDTO;
import com.hostel.cms.model.Staff;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.util.List;

@Repository
public class StaffDAOImpl implements StaffDAO {

    private final JdbcTemplate jdbcTemplate;

    public StaffDAOImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Staff> staffRowMapper = (rs, rowNum) -> {
        Staff staff = new Staff();
        staff.setStaffId(rs.getInt("staff_id"));
        staff.setName(rs.getString("name"));
        staff.setDepartmentId(rs.getInt("department_id"));
        staff.setDepartmentName(rs.getString("department_name"));
        staff.setPhone(rs.getString("phone"));
        return staff;
    };

    @Override
    public List<Staff> findAll() {
        String sql = "SELECT s.staff_id, s.name, s.department_id, d.department_name, s.phone " +
                     "FROM staff s JOIN department d ON s.department_id = d.department_id " +
                     "ORDER BY s.staff_id ASC";
        return jdbcTemplate.query(sql, staffRowMapper);
    }

    @Override
    public List<Staff> findByDepartmentId(int departmentId) {
        String sql = "SELECT s.staff_id, s.name, s.department_id, d.department_name, s.phone " +
                     "FROM staff s JOIN department d ON s.department_id = d.department_id " +
                     "WHERE s.department_id = ? ORDER BY s.staff_id ASC";
        return jdbcTemplate.query(sql, staffRowMapper, departmentId);
    }

    @Override
    public List<StaffPerformanceDTO> getStaffPerformance() {
        String sql = "SELECT " +
                     "    s.staff_id, " +
                     "    s.name, " +
                     "    s.department_id, " +
                     "    d.department_name, " +
                     "    s.phone, " +
                     "    COUNT(ca.complaint_id) AS total_assigned, " +
                     "    SUM(CASE WHEN c.status = 'Resolved' THEN 1 ELSE 0 END) AS resolved_count, " +
                     "    SUM(CASE WHEN c.status IN ('Pending', 'In Progress') THEN 1 ELSE 0 END) AS pending_count, " +
                     "    COALESCE(AVG(fb.rating), 0.0) AS avg_rating " +
                     "FROM staff s " +
                     "JOIN department d ON s.department_id = d.department_id " +
                     "LEFT JOIN complaint_assignment ca ON s.staff_id = ca.staff_id " +
                     "LEFT JOIN complaint c ON ca.complaint_id = c.complaint_id " +
                     "LEFT JOIN complaint_feedback fb ON c.complaint_id = fb.complaint_id " +
                     "GROUP BY s.staff_id, s.name, s.department_id, d.department_name, s.phone " +
                     "ORDER BY total_assigned DESC, s.name ASC";

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            StaffPerformanceDTO dto = new StaffPerformanceDTO();
            dto.setStaffId(rs.getInt("staff_id"));
            dto.setName(rs.getString("name"));
            dto.setDepartmentId(rs.getInt("department_id"));
            dto.setDepartmentName(rs.getString("department_name"));
            dto.setPhone(rs.getString("phone"));
            dto.setTotalAssigned(rs.getInt("total_assigned"));
            dto.setResolvedCount(rs.getInt("resolved_count"));
            dto.setPendingCount(rs.getInt("pending_count"));
            double rating = rs.getDouble("avg_rating");
            dto.setAvgRating(Math.round(rating * 10.0) / 10.0);
            return dto;
        });
    }

    @Override
    public Staff save(Staff staff) {
        String sql = "INSERT INTO staff (name, department_id, phone) VALUES (?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"staff_id"});
            ps.setString(1, staff.getName());
            ps.setInt(2, staff.getDepartmentId());
            ps.setString(3, staff.getPhone() != null ? staff.getPhone() : "9876543210");
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
            staff.setStaffId(key.intValue());
        }
        return staff;
    }
}
