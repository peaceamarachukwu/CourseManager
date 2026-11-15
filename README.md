
Functional Operations 
Add Course: entering valid course records (course code, course title, and positive
integer credit units) should store the data in the internal list to be rendered in the display. 

View All Courses: displaying all recorded courses across multiple entries with accurate formatting and
completeness. 

Search Course by Code: Test searching for an existing course code (confirming full course details are
returned) as well as searching for a non-existent course code (confirming a "not found" message is shown).

Compute Total Credit Units: Test calculating the total credit units when the list is empty (0), 
after adding several courses, and after loading courses from a file. Save to File:
Test exporting the current course list to a file and verifying the saved records persist in comma-separated format 
(CODE,Title,Units). Load from File: Test reading a previously saved file back into the application and confirming
the course table/list and total credit units update accurately.

Exit Program: Test terminating the application cleanly via the window close button (GUI) or option 7 (console mode).
Input Validation, String Processing & Exception-Handling Tests Blank/Empty Fields: Attempt to add a course or run
a search with empty inputs to verify validation prevents submission and triggers an error/warning message.

Invalid Credit Units: Enter non-numeric strings (e.g., "abc"), 0, or negative numbers (e.g., -3) in the credit units field
to verify NumberFormatException / IllegalArgumentException handling rejects the input.

String Normalization & Case-Insensitivity: Enter a lowercase course code with leading/trailing spaces (e.g., " cos 201 ")
to verify it is trimmed and converted to uppercase ("COS 201"), and test searching with lowercase input to confirm
case-insensitive matching. Invalid Menu Choice & File Errors: In console mode, enter out-of-range numbers or text at the
menu prompt to verify invalid-choice handling, and test loading a missing or corrupted file to verify 
IOException / malformed-line handling.

Technical Feature Verification Tests Recursion Verification: 
The recursive credit-unit summation (computeUnitsRecursive) and recursive course lookup 
(recursiveSearch in GUI mode) return exact results without stack errors.
Iteration Verification: Verify that loops properly traverse the course collection when populating the UI table,
printing console listings, and reading/writing file lines. 

CourseApp.java implements a dual-mode user interface consisting of a primary Java Swing Graphical User Interface (GUI) 
and an automatic fallback Console/Text-Based Menu Interface.

Primary Interface: Java Swing GUI (CourseApp extends JFrame) 

When a graphical display is available (or when launched with --gui), the application renders a desktop window titled
"Student Course Management System" (800 × 550 pixels, centered on screen, styled with the OS native Look and Feel). 
Its main JPanel uses a BorderLayout divided into three sections:

Layout RegionUI Components & Purpose Top (NORTH)A centered JLabel title banner displaying 
"Student Course Management System" in bold 22pt Segoe UI. Center (CENTER)
A horizontal JSplitPane (divider at 300px, resize weight 0.35) separating the input form on the left from the search and
table view on the right. Center-Left (createFormPanel)
A white JPanel with a " Add New Course " titled border and vertical BoxLayout containing three labeled JTextField inputs
(Course Code, Course Title, Credit Units) and a green "Add Course" JButton. Center-Right (createTableAndSearchPanel)
A top search bar ("Search: " label, searchField text box, and teal "Search Code" JButton) paired with a JScrollPane 
housing a read-only JTable (Course Code, Course Title, Units columns). Bottom (SOUTH)A status/action bar displaying the 
live Total Credit Units: 0 JLabel on the left and a FlowLayout button group on the right containing
"Calculate Total Units", "Save to File", and "Load from File" buttons. Popups & Dialogs: The GUI uses JOptionPane 
modal dialogs to display success confirmations, search results, total unit summaries, warnings, and validation errors,
alongside JFileChooser dialogs for selecting save/load file paths. 

Secondary Interface: Headless Console Menu (HeadlessCourseManager) 

If --console is passed at startup or GraphicsEnvironment.isHeadless() detects no graphical display,
the program switches to a command-line interface driven by Scanner(System.in) and System.out. 
It automatically loads courses.txt if present and loops through a numbered text menu 
(1. Add course,
2. List courses,
3. Search course by code,
4. Save to file, 
5. Load from file, 
6. Display total credit units,
7. Exit).

