import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

// Course Class representing individual course records
class Course implements Serializable {
    private static final long serialVersionUID = 1L;
    private String courseCode;
    private String courseTitle;
    private int creditUnit;

    public Course(String courseCode, String courseTitle, int creditUnit) {
        this.courseCode = courseCode.trim().toUpperCase();
        this.courseTitle = courseTitle.trim();
        this.creditUnit = creditUnit;
    }

    public String getCourseCode() { return courseCode; }
    public String getCourseTitle() { return courseTitle; }
    public int getCreditUnit() { return creditUnit; }

    @Override
    public String toString() {
        return courseCode + "," + courseTitle + "," + creditUnit;
    }

    public static Course fromString(String line) {
        String[] parts = line.split(",");
        if (parts.length == 3) {
            try {
                return new Course(parts[0], parts[1], Integer.parseInt(parts[2].trim()));
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }
}

public class CourseApp extends JFrame {
    private List<Course> courseList;
    private DefaultTableModel tableModel;
    private JTable courseTable;
    private JTextField codeField, titleField, unitField, searchField;
    private JLabel totalUnitsLabel;

    public CourseApp() {
        courseList = new ArrayList<>();
        
        // Setup Window Properties
        setTitle("Student Course Management System");
        setSize(800, 550);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Use modern look and feel if available
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        initUI();
    }

    private void initUI() {
        // Main Layout Container
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        mainPanel.setBackground(new Color(245, 247, 250));

        // --- TOP: Title Banner ---
        JLabel titleLabel = new JLabel("Student Course Management System", JLabel.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(new Color(33, 37, 41));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // --- CENTER: Split Pane (Form on Left, Table & Search on Right) ---
        JPanel leftPanel = createFormPanel();
        JPanel rightPanel = createTableAndSearchPanel();

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel);
        splitPane.setDividerLocation(300);
        splitPane.setResizeWeight(0.35);
        mainPanel.add(splitPane, BorderLayout.CENTER);

        // --- BOTTOM: Status Bar / Total Units & File Actions ---
        JPanel bottomPanel = createBottomPanel();
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private JPanel createFormPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)), " Add New Course ",
                0, 0, new Font("Segoe UI", Font.BOLD, 14), new Color(40, 40, 40)
        ));
        panel.setBackground(Color.WHITE);

        codeField = new JTextField();
        titleField = new JTextField();
        unitField = new JTextField();

        panel.add(Box.createVerticalStrut(10));
        panel.add(createInputRow("Course Code:", codeField));
        panel.add(Box.createVerticalStrut(10));
        panel.add(createInputRow("Course Title:", titleField));
        panel.add(Box.createVerticalStrut(10));
        panel.add(createInputRow("Credit Units:", unitField));
        panel.add(Box.createVerticalStrut(20));

        JButton addButton = new JButton("Add Course");
        styleButton(addButton, new Color(40, 167, 69), Color.WHITE);
        addButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        addButton.addActionListener(e -> addCourseAction());
        panel.add(addButton);

        panel.add(Box.createVerticalGlue());
        return panel;
    }

    private JPanel createInputRow(String labelText, JTextField textField) {
        JPanel row = new JPanel(new BorderLayout(5, 5));
        row.setBackground(Color.WHITE);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        
        textField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        textField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

        row.add(label, BorderLayout.NORTH);
        row.add(textField, BorderLayout.CENTER);
        return row;
    }

    private JPanel createTableAndSearchPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBackground(new Color(245, 247, 250));

        // Search Bar Panel
        JPanel searchPanel = new JPanel(new BorderLayout(5, 5));
        searchPanel.setBackground(new Color(245, 247, 250));
        searchField = new JTextField();
        JButton searchButton = new JButton("Search Code");
        styleButton(searchButton, new Color(23, 162, 184), Color.WHITE);
        searchButton.addActionListener(e -> searchCourseAction());

        searchPanel.add(new JLabel("Search: "), BorderLayout.WEST);
        searchPanel.add(searchField, BorderLayout.CENTER);
        searchPanel.add(searchButton, BorderLayout.EAST);
        panel.add(searchPanel, BorderLayout.NORTH);

        // Course Table Setup
        String[] columns = {"Course Code", "Course Title", "Units"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        courseTable = new JTable(tableModel);
        courseTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        courseTable.setRowHeight(22);
        JScrollPane scrollPane = new JScrollPane(courseTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createBottomPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(245, 247, 250));
        panel.setBorder(new EmptyBorder(5, 0, 0, 0));

        totalUnitsLabel = new JLabel("Total Credit Units: 0");
        totalUnitsLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        panel.add(totalUnitsLabel, BorderLayout.WEST);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnPanel.setBackground(new Color(245, 247, 250));

        JButton totalButton = new JButton("Calculate Total Units");
        JButton saveButton = new JButton("Save to File");
        JButton loadButton = new JButton("Load from File");
        
        styleButton(totalButton, new Color(23, 162, 184), Color.WHITE);
        styleButton(saveButton, new Color(108, 117, 125), Color.WHITE);
        styleButton(loadButton, new Color(108, 117, 125), Color.WHITE);

        totalButton.addActionListener(e -> showTotalUnitsAction());
        saveButton.addActionListener(e -> saveToFileAction());
        loadButton.addActionListener(e -> loadFromFileAction());

        btnPanel.add(totalButton);
        btnPanel.add(saveButton);
        btnPanel.add(loadButton);
        panel.add(btnPanel, BorderLayout.EAST);

        return panel;
    }

    private void styleButton(JButton button, Color bgColor, Color fgColor) {
        button.setBackground(bgColor);
        button.setForeground(fgColor);
        button.setFocusPainted(false);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    // --- Core Functionality Implementation ---

    private void addCourseAction() {
        try {
            String code = codeField.getText().trim();
            String title = titleField.getText().trim();
            String unitStr = unitField.getText().trim();

            if (code.isEmpty() || title.isEmpty() || unitStr.isEmpty()) {
                throw new IllegalArgumentException("All fields must be filled out.");
            }

            int units;
            try {
                units = Integer.parseInt(unitStr);
                if (units <= 0) throw new NumberFormatException();
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Credit units must be a valid positive integer.");
            }

            Course newCourse = new Course(code, title, units);
            courseList.add(newCourse);
            
            refreshTableData();
            clearFormFields();
            JOptionPane.showMessageDialog(this, "Course added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);

        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void refreshTableData() {
        tableModel.setRowCount(0);
        for (Course c : courseList) {
            tableModel.addRow(new Object[]{c.getCourseCode(), c.getCourseTitle(), c.getCreditUnit()});
        }
        updateTotalUnitsRecursive();
    }

    // Recursive feature: compute total credit units recursively using sublists (demonstrating recursion)
    private static int computeUnitsRecursive(List<Course> list, int index) {
        if (index >= list.size()) {
            return 0;
        }
        return list.get(index).getCreditUnit() + computeUnitsRecursive(list, index + 1);
    }

    private void updateTotalUnitsRecursive() {
        int total = computeUnitsRecursive(courseList, 0);
        totalUnitsLabel.setText("Total Credit Units: " + total);
    }

    private void showTotalUnitsAction() {
        int total = computeUnitsRecursive(courseList, 0);
        totalUnitsLabel.setText("Total Credit Units: " + total);
        JOptionPane.showMessageDialog(this, "Total Credit Units: " + total,
                "Credit Unit Total", JOptionPane.INFORMATION_MESSAGE);
    }

    // Recursive search implementation
    private Course recursiveSearch(List<Course> list, String targetCode, int index) {
        if (index >= list.size()) {
            return null;
        }
        if (list.get(index).getCourseCode().equalsIgnoreCase(targetCode)) {
            return list.get(index);
        }
        return recursiveSearch(list, targetCode, index + 1);
    }

    private void searchCourseAction() {
        String query = searchField.getText().trim();
        if (query.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a course code to search.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Course found = recursiveSearch(courseList, query, 0);
        if (found != null) {
            JOptionPane.showMessageDialog(this, 
                    "Course Found!\n\nCode: " + found.getCourseCode() + 
                    "\nTitle: " + found.getCourseTitle() + 
                    "\nUnits: " + found.getCreditUnit(), 
                    "Search Result", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "No course found with code: " + query.toUpperCase(), "Not Found", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void saveToFileAction() {
        JFileChooser fileChooser = new JFileChooser();
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
                for (Course c : courseList) {
                    writer.println(c.toString());
                }
                JOptionPane.showMessageDialog(this, "Courses saved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Error saving file: " + e.getMessage(), "File Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void loadFromFileAction() {
        JFileChooser fileChooser = new JFileChooser();
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                courseList.clear();
                while ((line = reader.readLine()) != null) {
                    Course c = Course.fromString(line);
                    if (c != null) {
                        courseList.add(c);
                    }
                }
                refreshTableData();
                JOptionPane.showMessageDialog(this, "Courses loaded successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Error loading file: " + e.getMessage(), "File Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void clearFormFields() {
        codeField.setText("");
        titleField.setText("");
        unitField.setText("");
        codeField.requestFocus();
    }

    public static void main(String[] args) {
        boolean forceGui = false;
        boolean forceConsole = false;
        for (String arg : args) {
            if ("--gui".equals(arg)) {
                forceGui = true;
            } else if ("--console".equals(arg)) {
                forceConsole = true;
            } else {
                System.err.println("Unknown option: " + arg);
                printUsage();
                System.exit(2);
                return;
            }
        }

        if (forceGui && forceConsole) {
            System.err.println("Choose either --gui or --console, not both.");
            printUsage();
            System.exit(2);
            return;
        }

        boolean headless = GraphicsEnvironment.isHeadless();
        if (forceGui && headless) {
            System.err.println("Cannot start GUI mode: no graphical display is available.");
            System.exit(1);
            return;
        }

        if (forceConsole || (!forceGui && headless)) {
            if (!forceConsole) {
                System.out.println("No graphical display detected. Starting headless console mode...");
            }
            new HeadlessCourseManager().run();
            return;
        }

        SwingUtilities.invokeLater(() -> {
            new CourseApp().setVisible(true);
        });
    }

    private static void printUsage() {
        System.err.println("Usage: java CourseApp [--gui | --console]");
    }

    private static class HeadlessCourseManager {
        private final List<Course> courseList = new ArrayList<>();
        private final Scanner scanner = new Scanner(System.in);

        public void run() {
            loadFromDefaultFile();
            while (true) {
                printMenu();
                String choice = scanner.nextLine().trim();

                try {
                    switch (choice) {
                        case "1":
                            addCourse();
                            break;
                        case "2":
                            listCourses();
                            break;
                        case "3":
                            searchCourse();
                            break;
                        case "4":
                            saveCourses();
                            break;
                        case "5":
                            loadCourses();
                            break;
                        case "6":
                            displayTotalUnits();
                            break;
                        case "7":
                            System.out.println("Exiting headless course manager.");
                            return;
                        default:
                            System.out.println("Invalid choice. Please enter a number from 1 to 7.");
                    }
                } catch (IllegalArgumentException e) {
                    System.out.println("Input error: " + e.getMessage());
                }
            }
        }

        private void printMenu() {
            System.out.println();
            System.out.println("Student Course Management System - Headless Mode");
            System.out.println("1. Add course");
            System.out.println("2. List courses");
            System.out.println("3. Search course by code");
            System.out.println("4. Save to file");
            System.out.println("5. Load from file");
            System.out.println("6. Display total credit units");
            System.out.println("7. Exit");
            System.out.print("Choose an option: ");
        }

        private void addCourse() {
            System.out.print("Course code: ");
            String code = scanner.nextLine().trim();
            System.out.print("Course title: ");
            String title = scanner.nextLine().trim();
            System.out.print("Credit units: ");
            String unitsStr = scanner.nextLine().trim();

            if (code.isEmpty() || title.isEmpty() || unitsStr.isEmpty()) {
                throw new IllegalArgumentException("All fields must be filled out.");
            }

            int units;
            try {
                units = Integer.parseInt(unitsStr);
                if (units <= 0) {
                    throw new NumberFormatException();
                }
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Credit units must be a valid positive integer.");
            }

            courseList.add(new Course(code, title, units));
            System.out.println("Course added successfully.");
        }

        private void listCourses() {
            if (courseList.isEmpty()) {
                System.out.println("No courses available.");
                return;
            }

            System.out.println("Courses:");
            for (Course course : courseList) {
                System.out.printf("- %s | %s | %d units%n",
                        course.getCourseCode(), course.getCourseTitle(), course.getCreditUnit());
            }
        }

        private void displayTotalUnits() {
            int total = computeUnitsRecursive(courseList, 0);
            System.out.println("Total Credit Units: " + total);
        }

        private void searchCourse() {
            System.out.print("Enter course code to search: ");
            String code = scanner.nextLine().trim();
            if (code.isEmpty()) {
                throw new IllegalArgumentException("Course code cannot be empty.");
            }

            Course found = null;
            for (Course course : courseList) {
                if (course.getCourseCode().equalsIgnoreCase(code)) {
                    found = course;
                    break;
                }
            }

            if (found == null) {
                System.out.println("No course found with code: " + code.toUpperCase());
            } else {
                System.out.println("Course found:");
                System.out.printf("Code: %s%nTitle: %s%nUnits: %d%n",
                        found.getCourseCode(), found.getCourseTitle(), found.getCreditUnit());
            }
        }

        private void saveCourses() {
            System.out.print("Enter file path to save (default: courses.txt): ");
            String path = scanner.nextLine().trim();
            if (path.isEmpty()) {
                path = "courses.txt";
            }

            saveToFile(new File(path));
            System.out.println("Courses saved successfully to " + path);
        }

        private void loadCourses() {
            System.out.print("Enter file path to load (default: courses.txt): ");
            String path = scanner.nextLine().trim();
            if (path.isEmpty()) {
                path = "courses.txt";
            }

            loadFromFile(new File(path));
            System.out.println("Courses loaded successfully from " + path);
        }

        private void loadFromDefaultFile() {
            File defaultFile = new File("courses.txt");
            if (defaultFile.exists()) {
                loadFromFile(defaultFile);
            }
        }

        private void saveToFile(File file) {
            try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
                for (Course course : courseList) {
                    writer.println(course.toString());
                }
            } catch (IOException e) {
                throw new IllegalArgumentException("Error saving file: " + e.getMessage());
            }
        }

        private void loadFromFile(File file) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                courseList.clear();
                String line;
                while ((line = reader.readLine()) != null) {
                    Course course = Course.fromString(line);
                    if (course != null) {
                        courseList.add(course);
                    }
                }
            } catch (IOException e) {
                throw new IllegalArgumentException("Error loading file: " + e.getMessage());
            }
        }
    }
}