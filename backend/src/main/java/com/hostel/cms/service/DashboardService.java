package com.hostel.cms.service;

import com.hostel.cms.dao.ComplaintDAO;
import com.hostel.cms.dto.DashboardStatsDTO;
import org.springframework.stereotype.Service;

@Service
public class DashboardService {

    private final ComplaintDAO complaintDAO;

    public DashboardService(ComplaintDAO complaintDAO) {
        this.complaintDAO = complaintDAO;
    }

    public DashboardStatsDTO getDashboardStats() {
        DashboardStatsDTO stats = new DashboardStatsDTO();
        int total = complaintDAO.countTotal();
        int pending = complaintDAO.countByStatus("Pending");
        int inProgress = complaintDAO.countByStatus("In Progress");
        int resolved = complaintDAO.countByStatus("Resolved");

        stats.setTotalComplaints(total);
        stats.setPendingComplaints(pending);
        stats.setInProgressComplaints(inProgress);
        stats.setResolvedComplaints(resolved);

        double rate = total > 0 ? ((double) resolved / total) * 100.0 : 0.0;
        stats.setResolutionRate(Math.round(rate * 10.0) / 10.0);
        stats.setAverageRating(complaintDAO.getAverageRating());
        stats.setComplaintsByDepartment(complaintDAO.countByDepartment());
        stats.setComplaintsByStatus(complaintDAO.countByStatusMap());

        return stats;
    }
}
