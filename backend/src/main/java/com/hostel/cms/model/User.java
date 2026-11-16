package com.hostel.cms.model;

import java.sql.Timestamp;

public class User {
    private Integer userId;
    private String name;
    private String email;
    private String roomNumber;
    private String hostelBlock;
    private String role; // STUDENT, WARDEN, ADMIN
    private Timestamp createdAt;

    public User() {}

    public User(Integer userId, String name, String email, String roomNumber, String hostelBlock, String role) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.roomNumber = roomNumber;
        this.hostelBlock = hostelBlock;
        this.role = role;
    }

    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getRoomNumber() { return roomNumber; }
    public void setRoomNumber(String roomNumber) { this.roomNumber = roomNumber; }

    public String getHostelBlock() { return hostelBlock; }
    public void setHostelBlock(String hostelBlock) { this.hostelBlock = hostelBlock; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}
