package com.registration.db;


import com.registration.model.Course;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CourseDAOImpl implements CourseDAO {

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
            DatabaseConfig.URL, 
            DatabaseConfig.USER, 
            DatabaseConfig.PASSWORD
        );
    }

    private Course extractCourseFromResultSet(ResultSet rs) throws SQLException {
        return new Course(
            rs.getInt("course_id"),
            rs.getString("course_code"),
            rs.getString("course_name"),
            rs.getInt("credits"),
            rs.getString("instructor")
        );
    }

    @Override
    public void addCourse(Course course) throws SQLException {
        String sql = "INSERT INTO courses (course_code, course_name, credits, instructor) VALUES (?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) { 
            
            stmt.setString(1, course.getCourseCode());
            stmt.setString(2, course.getCourseName());
            stmt.setInt(3, course.getCredits());
            stmt.setString(4, course.getInstructor());
            
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Database Error (Add): " + e.getMessage());
            throw e; 
        }
    }

    @Override
    public List<Course> getAllCourses() throws SQLException {
        List<Course> courses = new ArrayList<>();
        String sql = "SELECT * FROM courses ORDER BY course_id";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                courses.add(extractCourseFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Database Error (Read): " + e.getMessage());
            throw e; 
        }
        return courses;
    }

    @Override
    public void updateCourse(Course course) throws SQLException {
        String sql = "UPDATE courses SET course_code = ?, course_name = ?, credits = ?, instructor = ? WHERE course_id = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, course.getCourseCode());
            stmt.setString(2, course.getCourseName());
            stmt.setInt(3, course.getCredits());
            stmt.setString(4, course.getInstructor());
            stmt.setInt(5, course.getCourseId());
            
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Database Error (Update): " + e.getMessage());
            throw e; 
        }
    }

    @Override
    public void deleteCourse(int id) throws SQLException {
        String sql = "DELETE FROM courses WHERE course_id = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Database Error (Delete): " + e.getMessage());
            throw e; 
        }
    }
    
    // Required by interface but not strictly used by UI
    @Override
    public Course getCourseById(int id) throws SQLException { return null; }
}