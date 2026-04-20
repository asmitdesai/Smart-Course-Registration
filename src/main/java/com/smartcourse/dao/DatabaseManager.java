package com.smartcourse.dao;

import java.sql.*;

public class DatabaseManager {
    private static final String DB_URL = "jdbc:sqlite:smartcourse.db";
    private static DatabaseManager instance;
    private Connection connection;

    private DatabaseManager() {
        try {
            connection = DriverManager.getConnection(DB_URL);
            connection.createStatement().execute("PRAGMA foreign_keys = ON");
            createTables();
            seedData();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to initialize database", e);
        }
    }

    public static synchronized DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    public synchronized Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(DB_URL);
                connection.createStatement().execute("PRAGMA foreign_keys = ON");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to re-establish database connection", e);
        }
        return connection;
    }

    private void createTables() throws SQLException {
        Statement stmt = connection.createStatement();

        stmt.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS users (
                        user_id INTEGER PRIMARY KEY AUTOINCREMENT,
                        name TEXT NOT NULL,
                        email TEXT UNIQUE NOT NULL,
                        password TEXT NOT NULL,
                        role TEXT NOT NULL,
                        program TEXT,
                        semester INTEGER DEFAULT 1,
                        department TEXT,
                        student_id INTEGER,
                        faculty_id INTEGER,
                        admin_id INTEGER,
                        is_active INTEGER DEFAULT 1
                    )
                """);

        stmt.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS courses (
                        course_id INTEGER PRIMARY KEY AUTOINCREMENT,
                        name TEXT NOT NULL,
                        credits INTEGER NOT NULL,
                        seats_available INTEGER NOT NULL,
                        total_seats INTEGER NOT NULL,
                        description TEXT
                    )
                """);

        stmt.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS course_prerequisites (
                        course_id INTEGER NOT NULL,
                        prerequisite_id INTEGER NOT NULL,
                        PRIMARY KEY (course_id, prerequisite_id),
                        FOREIGN KEY (course_id) REFERENCES courses(course_id),
                        FOREIGN KEY (prerequisite_id) REFERENCES courses(course_id)
                    )
                """);

        stmt.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS registrations (
                        reg_id INTEGER PRIMARY KEY AUTOINCREMENT,
                        student_id INTEGER NOT NULL,
                        course_id INTEGER NOT NULL,
                        status TEXT NOT NULL DEFAULT 'PENDING',
                        registration_date TEXT NOT NULL,
                        FOREIGN KEY (student_id) REFERENCES users(user_id),
                        FOREIGN KEY (course_id) REFERENCES courses(course_id)
                    )
                """);

        stmt.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS payments (
                        payment_id INTEGER PRIMARY KEY AUTOINCREMENT,
                        student_id INTEGER NOT NULL,
                        course_id INTEGER NOT NULL,
                        amount REAL NOT NULL,
                        status TEXT NOT NULL DEFAULT 'PENDING',
                        payment_date TEXT,
                        FOREIGN KEY (student_id) REFERENCES users(user_id),
                        FOREIGN KEY (course_id) REFERENCES courses(course_id)
                    )
                """);

        stmt.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS schedules (
                        schedule_id INTEGER PRIMARY KEY AUTOINCREMENT,
                        course_id INTEGER NOT NULL,
                        faculty_id INTEGER NOT NULL,
                        time_slot TEXT NOT NULL,
                        room TEXT,
                        status TEXT NOT NULL DEFAULT 'PENDING',
                        FOREIGN KEY (course_id) REFERENCES courses(course_id),
                        FOREIGN KEY (faculty_id) REFERENCES users(user_id)
                    )
                """);

        stmt.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS grades (
                        grade_id INTEGER PRIMARY KEY AUTOINCREMENT,
                        student_id INTEGER NOT NULL,
                        course_id INTEGER NOT NULL,
                        marks REAL NOT NULL DEFAULT 0,
                        letter_grade TEXT NOT NULL DEFAULT 'N/A',
                        FOREIGN KEY (student_id) REFERENCES users(user_id),
                        FOREIGN KEY (course_id) REFERENCES courses(course_id)
                    )
                """);

        stmt.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS attendance (
                        attendance_id INTEGER PRIMARY KEY AUTOINCREMENT,
                        student_id INTEGER NOT NULL,
                        course_id INTEGER NOT NULL,
                        total_classes INTEGER NOT NULL DEFAULT 0,
                        attended_classes INTEGER NOT NULL DEFAULT 0,
                        FOREIGN KEY (student_id) REFERENCES users(user_id),
                        FOREIGN KEY (course_id) REFERENCES courses(course_id)
                    )
                """);
    }

    private void seedData() throws SQLException {
        // Check if already seeded
        ResultSet rs = connection.createStatement().executeQuery("SELECT COUNT(*) FROM users");
        if (rs.next() && rs.getInt(1) > 0)
            return;

        // Users - passwords stored as plain text for demo (bcrypt in production)
        String insertUser = "INSERT INTO users (name, email, password, role, program, semester, department, student_id, faculty_id, admin_id) VALUES (?,?,?,?,?,?,?,?,?,?)";
        PreparedStatement ps = connection.prepareStatement(insertUser);

        String demoPassword = org.mindrot.jbcrypt.BCrypt.hashpw("password123", org.mindrot.jbcrypt.BCrypt.gensalt());

        // Admin
        ps.setString(1, "Dr. Admin");
        ps.setString(2, "admin@uni.edu");
        ps.setString(3, demoPassword);
        ps.setString(4, "ADMIN");
        ps.setNull(5, Types.VARCHAR);
        ps.setNull(6, Types.INTEGER);
        ps.setNull(7, Types.VARCHAR);
        ps.setNull(8, Types.INTEGER);
        ps.setNull(9, Types.INTEGER);
        ps.setInt(10, 1001);
        ps.executeUpdate();

        // Students
        ps.setString(1, "Alice Johnson");
        ps.setString(2, "student@uni.edu");
        ps.setString(3, demoPassword);
        ps.setString(4, "STUDENT");
        ps.setString(5, "Computer Science");
        ps.setInt(6, 3);
        ps.setNull(7, Types.VARCHAR);
        ps.setInt(8, 2001);
        ps.setNull(9, Types.INTEGER);
        ps.setNull(10, Types.INTEGER);
        ps.executeUpdate();

        ps.setString(1, "Bob Smith");
        ps.setString(2, "bob@uni.edu");
        ps.setString(3, demoPassword);
        ps.setString(4, "STUDENT");
        ps.setString(5, "Software Engineering");
        ps.setInt(6, 2);
        ps.setNull(7, Types.VARCHAR);
        ps.setInt(8, 2002);
        ps.setNull(9, Types.INTEGER);
        ps.setNull(10, Types.INTEGER);
        ps.executeUpdate();

        ps.setString(1, "Carol White");
        ps.setString(2, "carol@uni.edu");
        ps.setString(3, demoPassword);
        ps.setString(4, "STUDENT");
        ps.setString(5, "Information Systems");
        ps.setInt(6, 4);
        ps.setNull(7, Types.VARCHAR);
        ps.setInt(8, 2003);
        ps.setNull(9, Types.INTEGER);
        ps.setNull(10, Types.INTEGER);
        ps.executeUpdate();

        // Faculty
        ps.setString(1, "Prof. David Lee");
        ps.setString(2, "faculty@uni.edu");
        ps.setString(3, demoPassword);
        ps.setString(4, "FACULTY");
        ps.setNull(5, Types.VARCHAR);
        ps.setNull(6, Types.INTEGER);
        ps.setString(7, "Computer Science");
        ps.setNull(8, Types.INTEGER);
        ps.setInt(9, 3001);
        ps.setNull(10, Types.INTEGER);
        ps.executeUpdate();

        ps.setString(1, "Prof. Emma Davis");
        ps.setString(2, "emma@uni.edu");
        ps.setString(3, demoPassword);
        ps.setString(4, "FACULTY");
        ps.setNull(5, Types.VARCHAR);
        ps.setNull(6, Types.INTEGER);
        ps.setString(7, "Mathematics");
        ps.setNull(8, Types.INTEGER);
        ps.setInt(9, 3002);
        ps.setNull(10, Types.INTEGER);
        ps.executeUpdate();

        // Courses
        String insertCourse = "INSERT INTO courses (name, credits, seats_available, total_seats, description) VALUES (?,?,?,?,?)";
        PreparedStatement cp = connection.prepareStatement(insertCourse, Statement.RETURN_GENERATED_KEYS);

        int[] courseIds = new int[8];
        String[][] courses = {
                { "Introduction to Programming", "3", "30", "30", "Fundamentals of programming using Python" },
                { "Data Structures", "4", "25", "25", "Arrays, linked lists, trees, graphs" },
                { "Algorithms", "4", "20", "20", "Algorithm design and analysis" },
                { "Database Systems", "3", "15", "15", "SQL, NoSQL, database design" },
                { "Operating Systems", "3", "0", "15", "Process management, memory, file systems" },
                { "Software Engineering", "3", "10", "10", "SDLC, design patterns, testing" },
                { "Calculus I", "3", "35", "35", "Differential and integral calculus" },
                { "Linear Algebra", "3", "2", "30", "Vectors, matrices, transformations" }
        };

        for (int i = 0; i < courses.length; i++) {
            cp.setString(1, courses[i][0]);
            cp.setInt(2, Integer.parseInt(courses[i][1]));
            cp.setInt(3, Integer.parseInt(courses[i][2]));
            cp.setInt(4, Integer.parseInt(courses[i][3]));
            cp.setString(5, courses[i][4]);
            cp.executeUpdate();
            ResultSet keys = cp.getGeneratedKeys();
            if (keys.next())
                courseIds[i] = keys.getInt(1);
        }

        // Prerequisites: Data Structures requires Intro to Programming
        String insertPre = "INSERT INTO course_prerequisites VALUES (?,?)";
        PreparedStatement pp = connection.prepareStatement(insertPre);
        pp.setInt(1, courseIds[1]);
        pp.setInt(2, courseIds[0]);
        pp.executeUpdate(); // DS requires Intro
        pp.setInt(1, courseIds[2]);
        pp.setInt(2, courseIds[1]);
        pp.executeUpdate(); // Algorithms requires DS
        pp.setInt(1, courseIds[3]);
        pp.setInt(2, courseIds[0]);
        pp.executeUpdate(); // DB requires Intro
        pp.setInt(1, courseIds[5]);
        pp.setInt(2, courseIds[1]);
        pp.executeUpdate(); // SE requires DS
        pp.setInt(1, courseIds[7]);
        pp.setInt(2, courseIds[6]);
        pp.executeUpdate(); // Linear Algebra requires Calc I

        // Schedules
        String insertSched = "INSERT INTO schedules (course_id, faculty_id, time_slot, room, status) VALUES (?,?,?,?,?)";
        PreparedStatement sp = connection.prepareStatement(insertSched);
        int[][] schedData = { { courseIds[0], 4 }, { courseIds[1], 4 }, { courseIds[2], 4 },
                { courseIds[6], 5 }, { courseIds[7], 5 } };
        String[] slots = { "MON 09:00-11:00", "TUE 11:00-13:00", "WED 14:00-16:00",
                "THU 09:00-11:00", "FRI 11:00-13:00" };
        String[] rooms = { "Room 101", "Room 102", "Room 103", "Room 201", "Room 202" };
        for (int i = 0; i < schedData.length; i++) {
            sp.setInt(1, schedData[i][0]);
            sp.setInt(2, schedData[i][1]);
            sp.setString(3, slots[i]);
            sp.setString(4, rooms[i]);
            sp.setString(5, "APPROVED");
            sp.executeUpdate();
        }

        // Seed a registration for Alice (student id=2) in Intro to Programming
        // (courseIds[0]) as REGISTERED
        PreparedStatement rp = connection.prepareStatement(
                "INSERT INTO registrations (student_id, course_id, status, registration_date) VALUES (?,?,?,?)");
        rp.setInt(1, 2);
        rp.setInt(2, courseIds[0]);
        rp.setString(3, "REGISTERED");
        rp.setString(4, "2026-01-15T10:00:00");
        rp.executeUpdate();

        // Seed grade and attendance for Alice in Intro to Programming
        PreparedStatement gp = connection.prepareStatement(
                "INSERT INTO grades (student_id, course_id, marks, letter_grade) VALUES (?,?,?,?)");
        gp.setInt(1, 2);
        gp.setInt(2, courseIds[0]);
        gp.setDouble(3, 85.5);
        gp.setString(4, "A");
        gp.executeUpdate();

        PreparedStatement ap = connection.prepareStatement(
                "INSERT INTO attendance (student_id, course_id, total_classes, attended_classes) VALUES (?,?,?,?)");
        ap.setInt(1, 2);
        ap.setInt(2, courseIds[0]);
        ap.setInt(3, 20);
        ap.setInt(4, 18);
        ap.executeUpdate();
    }
}
