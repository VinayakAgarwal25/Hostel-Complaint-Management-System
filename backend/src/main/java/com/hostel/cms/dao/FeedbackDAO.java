package com.hostel.cms.dao;

import com.hostel.cms.model.ComplaintFeedback;
import java.util.Optional;

public interface FeedbackDAO {
    ComplaintFeedback save(ComplaintFeedback feedback);
    Optional<ComplaintFeedback> findByComplaintId(int complaintId);
}
