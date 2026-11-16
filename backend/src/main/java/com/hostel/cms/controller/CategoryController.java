package com.hostel.cms.controller;

import com.hostel.cms.dao.ComplaintCategoryDAO;
import com.hostel.cms.model.ComplaintCategory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final ComplaintCategoryDAO categoryDAO;

    public CategoryController(ComplaintCategoryDAO categoryDAO) {
        this.categoryDAO = categoryDAO;
    }

    @GetMapping
    public ResponseEntity<List<ComplaintCategory>> getAllCategories() {
        return ResponseEntity.ok(categoryDAO.findAll());
    }
}
