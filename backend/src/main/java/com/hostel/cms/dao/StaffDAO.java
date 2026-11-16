package com.hostel.cms.dao;

import com.hostel.cms.dto.StaffPerformanceDTO;
import com.hostel.cms.model.Staff;
import java.util.List;

public interface StaffDAO {
    List<Staff> findAll();
    List<Staff> findByDepartmentId(int departmentId);
    List<StaffPerformanceDTO> getStaffPerformance();
    Staff save(Staff staff);
}
