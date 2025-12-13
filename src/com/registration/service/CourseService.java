package com.registration.service;

import com.registration.db.CourseDAO;
import com.registration.db.CourseDAOImpl;
import com.registration.model.Course;

import java.sql.SQLException;
import java.util.List;

// Service layer for business logic and data validation
public class CourseService {
    private final CourseDAO courseDAO;

    public CourseService() {
        this.courseDAO = new CourseDAOImpl(); // Initializes the DAO
    }

    // --- Validation (RegEx) ---
    private void validateCourse(Course course) throws IllegalArgumentException {
        // RegEx Requirement: 2-4 letters followed by 3 digits (e.g., CS101)
        String codeRegex = "[A-Za-z]{4,6}[0-9]{3}"; 
        if (!course.getCourseCode().matches(codeRegex)) {
            throw new IllegalArgumentException("Invalid Course Code. Must be 2-4 letters + 3 digits (e.g., CS101).");
        }
        
        if (course.getCourseName() == null || course.getCourseName().trim().isEmpty()) {
            throw new IllegalArgumentException("Course Name cannot be empty.");
        }
        
        // Credits must be a reasonable number
        int credits = course.getCredits(); 
        if (credits < 1 || credits > 35) {
            throw new IllegalArgumentException("Course Credits must be between 1 and 35.");
        }

        if (course.getInstructor() == null || course.getInstructor().trim().isEmpty()) {
            throw new IllegalArgumentException("Instructor name cannot be empty.");
        }
    }

    // --- CRUD Service Methods ---

    public void addCourse(Course course) throws IllegalArgumentException, SQLException {
        validateCourse(course);
        courseDAO.addCourse(course);
    }

    public List<Course> getAllCourses() throws SQLException {
        return courseDAO.getAllCourses();
    }

    public void updateCourse(Course course) throws IllegalArgumentException, SQLException {
        validateCourse(course);
        if (course.getCourseId() == 0) {
            throw new IllegalArgumentException("Cannot update a course without a valid ID.");
        }
        courseDAO.updateCourse(course);
    }

    public void deleteCourse(int courseId) throws SQLException {
        if (courseId <= 0) {
             throw new IllegalArgumentException("Invalid Course ID for deletion.");
        }
        courseDAO.deleteCourse(courseId);
    }
}