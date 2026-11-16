package com.hostel.cms.dao;

import com.hostel.cms.model.Complaint;
import com.hostel.cms.model.ComplaintStatusHistory;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ComplaintDAO {
    List<Complaint> findFiltered(String status, Integer departmentId, String priority, Integer userId);
    Optional<Complaint> findById(int complaintId);
    Complaint create(Complaint complaint);
    boolean updateStatus(int complaintId, String newStatus, Timestamp dateResolved);
    boolean assignStaff(int complaintId, int staffId);
    void recordStatusHistory(int complaintId, String status);
    List<ComplaintStatusHistory> getStatusHistory(int complaintId);
    
    // Analytics methods
    int countTotal();
    int countByStatus(String status);
    Map<String, Integer> countByDepartment();
    Map<String, Integer> countByStatusMap();
    double getAverageRating();
}
