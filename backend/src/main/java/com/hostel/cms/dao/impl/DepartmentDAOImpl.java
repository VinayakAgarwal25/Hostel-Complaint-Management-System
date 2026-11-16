package com.hostel.cms.dao.impl;

import com.hostel.cms.dao.DepartmentDAO;
import com.hostel.cms.model.Department;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class DepartmentDAOImpl implements DepartmentDAO {

    private final JdbcTemplate jdbcTemplate;

    public DepartmentDAOImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Department> findAll() {
        String sql = "SELECT department_id, department_name FROM department ORDER BY department_id ASC";
        return jdbcTemplate.query(sql, (rs, rowNum) ->
                new Department(rs.getInt("department_id"), rs.getString("department_name"))
        );
    }
}
