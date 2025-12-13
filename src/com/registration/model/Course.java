package com.registration.model;


//Course class representing the data model (Encapsulation)
public class Course {
 private int courseId;       // Primary Key
 private String courseCode;  
 private String courseName;
 private int credits;        
 private String instructor;

 // Constructor for creating new Course
 public Course(String courseCode, String courseName, int credits, String instructor) {
     this.courseCode = courseCode;
     this.courseName = courseName;
     this.credits = credits;
     this.instructor = instructor;
 }

 // Constructor for loading from DB
 public Course(int courseId, String courseCode, String courseName, int credits, String instructor) {
     this.courseId = courseId;
     this.courseCode = courseCode;
     this.courseName = courseName;
     this.credits = credits;
     this.instructor = instructor;
 }

 // --- Getters and Setters ---
 public int getCourseId() { return courseId; }
 public void setCourseId(int courseId) { this.courseId = courseId; }

 public String getCourseCode() { return courseCode; }
 public void setCourseCode(String courseCode) { this.courseCode = courseCode; }

 public String getCourseName() { return courseName; }
 public void setCourseName(String courseName) { this.courseName = courseName; }

 public int getCredits() { return credits; }
 public void setCredits(int credits) { this.credits = credits; }

 public String getInstructor() { return instructor; }
 public void setInstructor(String instructor) { this.instructor = instructor; }
}