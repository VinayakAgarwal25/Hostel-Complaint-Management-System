package com.hostel.cms.dao.impl;

import com.hostel.cms.dao.ComplaintDAO;
import com.hostel.cms.model.Complaint;
import com.hostel.cms.model.ComplaintStatusHistory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.*;

@Repository
public class ComplaintDAOImpl implements ComplaintDAO {

    private final JdbcTemplate jdbcTemplate;

    public ComplaintDAOImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Complaint> complaintRowMapper = (rs, rowNum) -> {
        Complaint c = new Complaint();
        c.setComplaintId(rs.getInt("complaint_id"));
        c.setUserId(rs.getInt("user_id"));
        c.setCategoryId(rs.getInt("category_id"));
        c.setDepartmentId(rs.getInt("department_id"));
        c.setDescription(rs.getString("description"));
        c.setStatus(rs.getString("status"));
        c.setPriority(rs.getString("priority"));
        c.setDateRegistered(rs.getTimestamp("date_registered"));
        c.setDateResolved(rs.getTimestamp("date_resolved"));

        // Joined fields
        c.setUserName(rs.getString("user_name"));
        c.setUserEmail(rs.getString("user_email"));
        c.setRoomNumber(rs.getString("room_number"));
        c.setHostelBlock(rs.getString("hostel_block"));
        c.setCategoryName(rs.getString("category_name"));
        c.setDepartmentName(rs.getString("department_name"));

        int staffId = rs.getInt("assigned_staff_id");
        if (!rs.wasNull()) {
            c.setAssignedStaffId(staffId);
            c.setAssignedStaffName(rs.getString("assigned_staff_name"));
        }

        int rating = rs.getInt("feedback_rating");
        if (!rs.wasNull()) {
            c.setFeedbackRating(rating);
            c.setFeedbackComments(rs.getString("feedback_comments"));
        }

        return c;
    };

    private static final String BASE_SELECT =
            "SELECT c.complaint_id, c.user_id, c.category_id, c.department_id, c.description, " +
            "       c.status, c.priority, c.date_registered, c.date_resolved, " +
            "       u.name AS user_name, u.email AS user_email, u.room_number, u.hostel_block, " +
            "       cat.category_name, d.department_name, " +
            "       s.staff_id AS assigned_staff_id, s.name AS assigned_staff_name, " +
            "       fb.rating AS feedback_rating, fb.comments AS feedback_comments " +
            "FROM complaint c " +
            "JOIN users u ON c.user_id = u.user_id " +
            "JOIN complaint_category cat ON c.category_id = cat.category_id " +
            "JOIN department d ON c.department_id = d.department_id " +
            "LEFT JOIN complaint_assignment ca ON c.complaint_id = ca.complaint_id " +
            "LEFT JOIN staff s ON ca.staff_id = s.staff_id " +
            "LEFT JOIN complaint_feedback fb ON c.complaint_id = fb.complaint_id ";

    @Override
    public List<Complaint> findFiltered(String status, Integer departmentId, String priority, Integer userId) {
        StringBuilder sql = new StringBuilder(BASE_SELECT).append(" WHERE 1=1 ");
        List<Object> params = new ArrayList<>();

        if (status != null && !status.trim().isEmpty() && !status.equalsIgnoreCase("All")) {
            sql.append(" AND LOWER(c.status) = LOWER(?) ");
            params.add(status.trim());
        }
        if (departmentId != null && departmentId > 0) {
            sql.append(" AND c.department_id = ? ");
            params.add(departmentId);
        }
        if (priority != null && !priority.trim().isEmpty() && !priority.equalsIgnoreCase("All")) {
            sql.append(" AND LOWER(c.priority) = LOWER(?) ");
            params.add(priority.trim());
        }
        if (userId != null && userId > 0) {
            sql.append(" AND c.user_id = ? ");
            params.add(userId);
        }

        sql.append(" ORDER BY c.complaint_id DESC");
        return jdbcTemplate.query(sql.toString(), complaintRowMapper, params.toArray());
    }

    @Override
    public Optional<Complaint> findById(int complaintId) {
        String sql = BASE_SELECT + " WHERE c.complaint_id = ?";
        List<Complaint> list = jdbcTemplate.query(sql, complaintRowMapper, complaintId);
        return list.stream().findFirst();
    }

    @Override
    public Complaint create(Complaint complaint) {
        String sql = "INSERT INTO complaint (user_id, category_id, department_id, description, status, priority, date_registered) " +
                     "VALUES (?, ?, ?, ?, 'Pending', ?, CURRENT_TIMESTAMP)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"complaint_id"});
            ps.setInt(1, complaint.getUserId());
            ps.setInt(2, complaint.getCategoryId());
            ps.setInt(3, complaint.getDepartmentId());
            ps.setString(4, complaint.getDescription());
            ps.setString(5, complaint.getPriority() != null ? complaint.getPriority() : "Medium");
            return ps;
        }, keyHolder);

        Number key = null;
        try {
            key = keyHolder.getKey();
        } catch (Exception e) {
            if (keyHolder.getKeys() != null && !keyHolder.getKeys().isEmpty()) {
                for (Map.Entry<String, Object> entry : keyHolder.getKeys().entrySet()) {
                    if (entry.getKey().equalsIgnoreCase("complaint_id") || 
                        entry.getKey().equalsIgnoreCase("id") || 
                        entry.getKey().equalsIgnoreCase("scope_identity()")) {
                        if (entry.getValue() instanceof Number) {
                            key = (Number) entry.getValue();
                            break;
                        }
                    }
                }
                if (key == null) {
                    Object firstVal = keyHolder.getKeys().values().iterator().next();
                    if (firstVal instanceof Number) {
                        key = (Number) firstVal;
                    }
                }
            }
        }

        // Fallback: Query max id if key is still null
        if (key == null) {
            Integer maxId = jdbcTemplate.queryForObject(
                "SELECT MAX(complaint_id) FROM complaint WHERE user_id = ?", Integer.class, complaint.getUserId());
            if (maxId != null) {
                key = maxId;
            }
        }

        if (key != null) {
            int newId = key.intValue();
            complaint.setComplaintId(newId);
            recordStatusHistory(newId, "Pending");
        }
        return complaint;
    }

    @Override
    public boolean updateStatus(int complaintId, String newStatus, Timestamp dateResolved) {
        String sql = "UPDATE complaint SET status = ?, date_resolved = ? WHERE complaint_id = ?";
        int rows = jdbcTemplate.update(sql, newStatus, dateResolved, complaintId);
        if (rows > 0) {
            recordStatusHistory(complaintId, newStatus);
            return true;
        }
        return false;
    }

    @Override
    public boolean assignStaff(int complaintId, int staffId) {
        // Upsert assignment record
        String deleteSql = "DELETE FROM complaint_assignment WHERE complaint_id = ?";
        jdbcTemplate.update(deleteSql, complaintId);

        String insertSql = "INSERT INTO complaint_assignment (complaint_id, staff_id, assigned_date) VALUES (?, ?, CURRENT_TIMESTAMP)";
        int rows = jdbcTemplate.update(insertSql, complaintId, staffId);

        // Also update complaint status to 'In Progress' if it was 'Pending'
        String updateStatusSql = "UPDATE complaint SET status = 'In Progress' WHERE complaint_id = ? AND status = 'Pending'";
        int statusUpdated = jdbcTemplate.update(updateStatusSql, complaintId);
        if (statusUpdated > 0) {
            recordStatusHistory(complaintId, "In Progress");
        }
        return rows > 0;
    }

    @Override
    public void recordStatusHistory(int complaintId, String status) {
        String sql = "INSERT INTO complaint_status_history (complaint_id, status, updated_on) VALUES (?, ?, CURRENT_TIMESTAMP)";
        jdbcTemplate.update(sql, complaintId, status);
    }

    @Override
    public List<ComplaintStatusHistory> getStatusHistory(int complaintId) {
        String sql = "SELECT history_id, complaint_id, status, updated_on FROM complaint_status_history " +
                     "WHERE complaint_id = ? ORDER BY history_id ASC";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            ComplaintStatusHistory h = new ComplaintStatusHistory();
            h.setHistoryId(rs.getInt("history_id"));
            h.setComplaintId(rs.getInt("complaint_id"));
            h.setStatus(rs.getString("status"));
            h.setUpdatedOn(rs.getTimestamp("updated_on"));
            return h;
        }, complaintId);
    }

    @Override
    public int countTotal() {
        Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM complaint", Integer.class);
        return count != null ? count : 0;
    }

    @Override
    public int countByStatus(String status) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM complaint WHERE LOWER(status) = LOWER(?)", Integer.class, status);
        return count != null ? count : 0;
    }

    @Override
    public Map<String, Integer> countByDepartment() {
        String sql = "SELECT d.department_name, COUNT(c.complaint_id) as total " +
                     "FROM department d LEFT JOIN complaint c ON d.department_id = c.department_id " +
                     "GROUP BY d.department_name ORDER BY total DESC";
        Map<String, Integer> result = new LinkedHashMap<>();
        jdbcTemplate.query(sql, rs -> {
            result.put(rs.getString("department_name"), rs.getInt("total"));
        });
        return result;
    }

    @Override
    public Map<String, Integer> countByStatusMap() {
        String sql = "SELECT status, COUNT(*) as cnt FROM complaint GROUP BY status";
        Map<String, Integer> result = new LinkedHashMap<>();
        result.put("Pending", 0);
        result.put("In Progress", 0);
        result.put("Resolved", 0);
        jdbcTemplate.query(sql, rs -> {
            result.put(rs.getString("status"), rs.getInt("cnt"));
        });
        return result;
    }

    @Override
    public double getAverageRating() {
        Double avg = jdbcTemplate.queryForObject(
                "SELECT COALESCE(AVG(rating), 0.0) FROM complaint_feedback", Double.class);
        return avg != null ? Math.round(avg * 10.0) / 10.0 : 0.0;
    }
}
