package com.hostel.cms.dao;

import com.hostel.cms.model.ComplaintCategory;
import java.util.List;

public interface ComplaintCategoryDAO {
    List<ComplaintCategory> findAll();
}
