package com.apps.hari.coursesforum;

/**
 * Created by Hari on 03/02/17.
 */

public class User {
    private String userId;
    private String userName;
    private String email;
    private String department;
    private boolean faculty;

    public User() {
    }

    public User(String userId,String userName, String email,String department, boolean faculty) {
        this.userId = userId;
        this.userName = userName;
        this.email = email;
        this.department=department;
        this.faculty = faculty;
    }

    public String getUserName() {
        return userName;
    }

    public String getEmail() {
        return email;
    }

    public boolean isFaculty() {
        return faculty;
    }



    public String getDepartment(){return department;}

    public String getUserId() {
        return userId;
    }



}