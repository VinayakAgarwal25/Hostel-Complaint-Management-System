package com.hostel.cms.dao.impl;

import com.hostel.cms.dao.FeedbackDAO;
import com.hostel.cms.model.ComplaintFeedback;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

@Repository
public class FeedbackDAOImpl implements FeedbackDAO {

    private final JdbcTemplate jdbcTemplate;

    public FeedbackDAOImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public ComplaintFeedback save(ComplaintFeedback feedback) {
        // Delete old feedback if exists (upsert)
        jdbcTemplate.update("DELETE FROM complaint_feedback WHERE complaint_id = ?", feedback.getComplaintId());

        String sql = "INSERT INTO complaint_feedback (complaint_id, rating, comments, feedback_date) " +
                     "VALUES (?, ?, ?, CURRENT_TIMESTAMP)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"feedback_id"});
            ps.setInt(1, feedback.getComplaintId());
            ps.setInt(2, feedback.getRating());
            ps.setString(3, feedback.getComments());
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
            feedback.setFeedbackId(key.intValue());
        }
        return feedback;
    }

    @Override
    public Optional<ComplaintFeedback> findByComplaintId(int complaintId) {
        String sql = "SELECT feedback_id, complaint_id, rating, comments, feedback_date FROM complaint_feedback WHERE complaint_id = ?";
        List<ComplaintFeedback> list = jdbcTemplate.query(sql, (rs, rowNum) -> {
            ComplaintFeedback fb = new ComplaintFeedback();
            fb.setFeedbackId(rs.getInt("feedback_id"));
            fb.setComplaintId(rs.getInt("complaint_id"));
            fb.setRating(rs.getInt("rating"));
            fb.setComments(rs.getString("comments"));
            fb.setFeedbackDate(rs.getTimestamp("feedback_date"));
            return fb;
        }, complaintId);
        return list.stream().findFirst();
    }
}
