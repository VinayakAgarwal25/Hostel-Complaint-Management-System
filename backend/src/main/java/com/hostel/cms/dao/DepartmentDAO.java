package com.hostel.cms.dao;

import com.hostel.cms.model.Department;
import java.util.List;

public interface DepartmentDAO {
    List<Department> findAll();
}
