package com.registration.db;



import com.registration.model.Course;
import java.sql.SQLException;
import java.util.List;

// Interface defining the Data Access Object contract
public interface CourseDAO {
    // Note the exact method signatures here:
    void addCourse(Course course) throws SQLException;
    List<Course> getAllCourses() throws SQLException;
    Course getCourseById(int id) throws SQLException;
    void updateCourse(Course course) throws SQLException;
    void deleteCourse(int id) throws SQLException;
}