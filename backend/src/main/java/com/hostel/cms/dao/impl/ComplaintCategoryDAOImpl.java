package com.hostel.cms.dao.impl;

import com.hostel.cms.dao.ComplaintCategoryDAO;
import com.hostel.cms.model.ComplaintCategory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ComplaintCategoryDAOImpl implements ComplaintCategoryDAO {

    private final JdbcTemplate jdbcTemplate;

    public ComplaintCategoryDAOImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<ComplaintCategory> findAll() {
        String sql = "SELECT category_id, category_name FROM complaint_category ORDER BY category_id ASC";
        return jdbcTemplate.query(sql, (rs, rowNum) ->
                new ComplaintCategory(rs.getInt("category_id"), rs.getString("category_name"))
        );
    }
}
