package com.registration.ui;


import com.registration.model.Course;
import com.registration.service.CourseService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.List;

/**
 * Main Java Swing Application for Course Registration CRUD.
 */
public class CourseRegistrationUI extends JFrame {

    // A. Required Swing Components (5+ components)
    private final JTextField txtCode = new JTextField(10);
    private final JTextField txtName = new JTextField(20);
    private final JComboBox<Integer> cmbCredits = new JComboBox<>(new Integer[]{1, 2, 3, 4, 5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31});
    private final JTextField txtInstructor = new JTextField(20);
    private final JButton btnAdd = new JButton("Add Course");
    private final JButton btnUpdate = new JButton("Update Course");
    private final JButton btnDelete = new JButton("Delete Course");
    private final JButton btnClear = new JButton("Clear Fields");
    private final JTable courseTable = new JTable();
    private final DefaultTableModel tableModel;

    // Hidden field to store the currently selected Course ID for update/delete
    private int selectedCourseId = 0; 
    
    // Service Layer Dependency
    private final CourseService courseService = new CourseService(); 

    // Constants for Styling
    private static final Color INPUT_BG_COLOR = new Color(220, 220, 220); // Gray background
    private static final Color FONT_COLOR = Color.BLACK;

    public CourseRegistrationUI() {
        super("📚 Course Registration System (JDBC & Swing CRUD)");
        
        // 1. Initialize Table Model
        String[] columnNames = {"ID", "Code", "Name", "Credits", "Instructor"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            // Override isCellEditable to prevent direct table editing
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        courseTable.setModel(tableModel);
        
        // 2. Set up the Layout and Components
        setupUI();
        
        // 3. Set up Action Listeners
        setupListeners();
        
        // 4. Initial Load
        loadCourses();

        // 5. Frame Configuration
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setLocationRelativeTo(null); // Center the window
        setVisible(true);
    }

    private void setupUI() {
        setLayout(new BorderLayout(10, 10)); // Outer layout
        
        // --- Input Panel (North) ---
        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setBorder(BorderFactory.createTitledBorder("Course Details"));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Helper to apply styling to input fields
        applyInputStyle(txtCode);
        applyInputStyle(txtName);
        applyInputStyle(txtInstructor);
        ((JComponent) cmbCredits.getEditor().getEditorComponent()).setBackground(INPUT_BG_COLOR);
        cmbCredits.setForeground(FONT_COLOR);
        
        // Add components to the input panel
        int row = 0;
        addLabelAndField(inputPanel, gbc, "Course Code (e.g., ITLDA601):", txtCode, row++);
        addLabelAndField(inputPanel, gbc, "Course Name:", txtName, row++);
        addLabelAndField(inputPanel, gbc, "Credits (1-5):", cmbCredits, row++);
        addLabelAndField(inputPanel, gbc, "Instructor:", txtInstructor, row++);
        
        add(inputPanel, BorderLayout.NORTH);
        
        // --- Button Panel (Center - Above Table) ---
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        
        buttonPanel.add(btnAdd);
        buttonPanel.add(btnUpdate);
        btnUpdate.setEnabled(false); // Disable until a row is selected
        buttonPanel.add(btnDelete);
        btnDelete.setEnabled(false); // Disable until a row is selected
        buttonPanel.add(btnClear);
        
        // Combine buttons and table into a center panel
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(buttonPanel, BorderLayout.NORTH);
        
        // JTable (Read/View)
        JScrollPane scrollPane = new JScrollPane(courseTable);
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        
        add(centerPanel, BorderLayout.CENTER);
        
        // Initial button state management
        updateButtonStates(false);
    }
    
    /** Applies required styling to JTextFields. */
    private void applyInputStyle(JTextField field) {
        field.setBackground(INPUT_BG_COLOR);
        field.setForeground(FONT_COLOR);
    }
    
    /** Helper method for adding label and field to a GridBagLayout panel. */
    private void addLabelAndField(JPanel panel, GridBagConstraints gbc, String labelText, JComponent component, int row) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;
        panel.add(new JLabel(labelText), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        panel.add(component, gbc);
    }

    private void setupListeners() {
        // --- CRUD Button Listeners ---
        btnAdd.addActionListener(e -> handleAdd());
        btnUpdate.addActionListener(e -> handleUpdate());
        btnDelete.addActionListener(e -> handleDelete());
        btnClear.addActionListener(e -> clearFields());

        // --- Table Selection Listener (for Update/Delete) ---
        courseTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && courseTable.getSelectedRow() != -1) {
                populateFieldsFromTable();
                updateButtonStates(true);
            }
        });
    }
    
    /** Populates input fields when a row in the JTable is selected. */
    private void populateFieldsFromTable() {
        int selectedRow = courseTable.getSelectedRow();
        if (selectedRow >= 0) {
            // Get the data from the JTable model (data is stored as objects)
            selectedCourseId = (int) tableModel.getValueAt(selectedRow, 0);
            String code = (String) tableModel.getValueAt(selectedRow, 1);
            String name = (String) tableModel.getValueAt(selectedRow, 2);
            int credits = (int) tableModel.getValueAt(selectedRow, 3);
            String instructor = (String) tableModel.getValueAt(selectedRow, 4);

            // Populate the fields
            txtCode.setText(code);
            txtName.setText(name);
            cmbCredits.setSelectedItem(credits);
            txtInstructor.setText(instructor);
        }
    }
    
    /** Enables/disables Update and Delete buttons based on selection. */
    private void updateButtonStates(boolean rowSelected) {
        btnUpdate.setEnabled(rowSelected);
        btnDelete.setEnabled(rowSelected);
        btnAdd.setEnabled(!rowSelected);
        txtCode.setEditable(!rowSelected); // Prevent changing code for a new course when editing
    }

    // --- CRUD Handlers ---

    private void handleAdd() {
        try {
            // Get data from form fields
            String code = txtCode.getText().trim();
            String name = txtName.getText().trim();
            int credits = (Integer) cmbCredits.getSelectedItem();
            String instructor = txtInstructor.getText().trim();

            Course newCourse = new Course(code, name, credits, instructor);
            
            courseService.addCourse(newCourse); // Validation and DB insert
            
            JOptionPane.showMessageDialog(this, "Course Added Successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            clearFields();
            loadCourses(); // Refresh table
            
        } catch (IllegalArgumentException e) {
            // RegEx or field validation error
            JOptionPane.showMessageDialog(this, "Validation Error: " + e.getMessage(), "Input Error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException e) {
            // Database error (e.g., duplicate unique code, connection issue)
            JOptionPane.showMessageDialog(this, "Database Error: Could not add course. Check if code is unique.", "DB Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
             JOptionPane.showMessageDialog(this, "An unexpected error occurred: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleUpdate() {
        if (selectedCourseId == 0) return; // Should not happen if buttons are managed correctly
        
        try {
            // Get updated data from form fields
            String code = txtCode.getText().trim();
            String name = txtName.getText().trim();
            int credits = (Integer) cmbCredits.getSelectedItem();
            String instructor = txtInstructor.getText().trim();

            // Create the updated course object using the stored ID
            Course updatedCourse = new Course(selectedCourseId, code, name, credits, instructor);
            
            courseService.updateCourse(updatedCourse); // Validation and DB update
            
            JOptionPane.showMessageDialog(this, "Course Updated Successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            clearFields();
            loadCourses(); // Refresh table
            
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, "Validation Error: " + e.getMessage(), "Input Error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database Error: Could not update course. Check if code is unique.", "DB Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleDelete() {
        if (selectedCourseId == 0) return;

        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to delete Course ID " + selectedCourseId + "?", 
            "Confirm Deletion", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                courseService.deleteCourse(selectedCourseId);
                
                JOptionPane.showMessageDialog(this, "Course Deleted Successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                clearFields();
                loadCourses(); // Refresh table
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Database Error: Could not delete course.", "DB Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void clearFields() {
        txtCode.setText("");
        txtName.setText("");
        cmbCredits.setSelectedIndex(2); // Default to 3 credits
        txtInstructor.setText("");
        selectedCourseId = 0;
        courseTable.clearSelection();
        updateButtonStates(false);
        txtCode.requestFocusInWindow();
    }

    private void loadCourses() {
        // Clear existing rows
        tableModel.setRowCount(0); 

        try {
            List<Course> courses = courseService.getAllCourses();
            
            for (Course course : courses) {
                // Add course data as a new row in the JTable
                tableModel.addRow(new Object[]{
                    course.getCourseId(),
                    course.getCourseCode(),
                    course.getCourseName(),
                    course.getCredits(),
                    course.getInstructor()
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Could not load data from database. Check connection settings in DatabaseConfig.java.", "Connection Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        // Use the Swing default look and feel
        SwingUtilities.invokeLater(() -> new CourseRegistrationUI());
    }
}