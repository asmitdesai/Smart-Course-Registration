package com.smartcourse.controller;

import com.smartcourse.dao.UserDAO;
import com.smartcourse.model.*;
import com.smartcourse.service.*;
import com.smartcourse.util.SceneManager;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

public class AdminDashboardController implements Initializable {
    @FXML
    private StackPane contentPane;
    @FXML
    private Label sidebarName;
    @FXML
    private Label sidebarEmail;

    private Admin currentAdmin;
    private final CourseService courseService = CourseService.getInstance();
    private final ScheduleService scheduleService = ScheduleService.getInstance();
    private final RegistrationService registrationService = RegistrationService.getInstance();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        User user = AuthService.getInstance().getCurrentUser();
        currentAdmin = (Admin) user;
        sidebarName.setText(user.getName());
        sidebarEmail.setText(user.getEmail());
        showDashboard();
    }

    @FXML
    void showDashboard() {
        setContent(buildDashboardView());
    }

    @FXML
    void showCourses() {
        setContent(buildCoursesView());
    }

    @FXML
    void showUsers() {
        setContent(buildUsersView());
    }

    @FXML
    void showScheduling() {
        setContent(buildSchedulingView());
    }

    @FXML
    void showRegistrations() {
        setContent(buildRegistrationsView());
    }

    @FXML
    void handleLogout() {
        AuthService.getInstance().logout();
        SceneManager.switchScene("/fxml/login.fxml", "Smart Course Registration");
    }

    private void setContent(Node node) {
        contentPane.getChildren().setAll(node);
    }

    // ===== DASHBOARD =====
    private Node buildDashboardView() {
        VBox root = new VBox(24);
        Label title = new Label("Admin Control Panel 🛡️");
        title.getStyleClass().add("page-title");
        Label sub = new Label("Manage the university's academic structure, users, and schedules.");
        sub.getStyleClass().add("page-subtitle");

        HBox stats = new HBox(16);
        try {
            long courseCount = courseService.getAllCourses().size();
            long regCount = registrationService.getAllRegistrations().size();
            long schedCount = scheduleService.getAllSchedules().size();
            stats.getChildren().addAll(
                    buildStatCard("📚 Total Courses", String.valueOf(courseCount), "#6366f1"),
                    buildStatCard("✅ Total Registrations", String.valueOf(regCount), "#10b981"),
                    buildStatCard("📅 Scheduled Slots", String.valueOf(schedCount), "#06b6d4"));
        } catch (SQLException e) {
        }

        Label tip = new Label(
                "💡 Use 'Scheduling' to assign faculty to courses and detect time clashes automatically.");
        tip.setStyle("-fx-text-fill: #818cf8; -fx-font-size: 13px;");
        tip.setWrapText(true);

        root.getChildren().addAll(title, sub, stats, tip);
        ScrollPane sp = new ScrollPane(root);
        sp.setFitToWidth(true);
        sp.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
        return sp;
    }

    private HBox buildStatCard(String label, String value, String color) {
        VBox card = new VBox(6);
        card.getStyleClass().add("card");
        card.setPrefWidth(240);
        Label val = new Label(value);
        val.setStyle("-fx-font-size: 36px; -fx-font-weight: 700; -fx-text-fill: " + color + ";");
        Label lbl = new Label(label);
        lbl.getStyleClass().add("metric-label");
        card.getChildren().addAll(val, lbl);
        HBox box = new HBox(card);
        HBox.setHgrow(card, Priority.ALWAYS);
        return box;
    }

    // ===== COURSES MANAGEMENT =====
    private Node buildCoursesView() {
        VBox root = new VBox(20);
        Label title = new Label("🏛️ Manage Academic Structure");
        title.getStyleClass().add("page-title");

        TableView<Course> table = new TableView<>();
        table.getStyleClass().add("table-view");
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        VBox.setVgrow(table, Priority.ALWAYS);

        TableColumn<Course, String> nameCol = new TableColumn<>("Course Name");
        nameCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getName()));
        TableColumn<Course, String> creditsCol = new TableColumn<>("Credits");
        creditsCol.setCellValueFactory(d -> new SimpleStringProperty(String.valueOf(d.getValue().getCredits())));
        creditsCol.setMaxWidth(80);
        TableColumn<Course, String> seatsCol = new TableColumn<>("Seats (Avail/Total)");
        seatsCol.setCellValueFactory(
                d -> new SimpleStringProperty(d.getValue().getSeatsAvailable() + "/" + d.getValue().getTotalSeats()));
        TableColumn<Course, String> descCol = new TableColumn<>("Description");
        descCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getDescription()));
        table.getColumns().addAll(nameCol, creditsCol, seatsCol, descCol);

        Label statusLabel = new Label("");

        // Form for add/edit
        VBox formCard = new VBox(12);
        formCard.getStyleClass().add("card");
        formCard.setMaxWidth(600);
        Label formTitle = new Label("Add / Edit Course");
        formTitle.getStyleClass().add("card-title");
        TextField nameField = new TextField();
        nameField.setPromptText("Course Name");
        TextField credField = new TextField();
        credField.setPromptText("Credits");
        TextField seatsField = new TextField();
        seatsField.setPromptText("Total Seats");
        TextField descField = new TextField();
        descField.setPromptText("Description");

        // Populate form on selection
        table.getSelectionModel().selectedItemProperty().addListener((obs, old, sel) -> {
            if (sel != null) {
                nameField.setText(sel.getName());
                credField.setText(String.valueOf(sel.getCredits()));
                seatsField.setText(String.valueOf(sel.getTotalSeats()));
                descField.setText(sel.getDescription());
            }
        });

        Button addBtn = new Button("Add Course");
        addBtn.getStyleClass().add("btn-primary");
        Button updateBtn = new Button("Update Selected");
        updateBtn.getStyleClass().add("btn-secondary");
        Button deleteBtn = new Button("Delete Selected");
        deleteBtn.getStyleClass().add("btn-danger");

        addBtn.setOnAction(e -> {
            try {
                Course c = new Course(0, nameField.getText(), Integer.parseInt(credField.getText()),
                        Integer.parseInt(seatsField.getText()), Integer.parseInt(seatsField.getText()),
                        descField.getText());
                courseService.createCourse(c);
                statusLabel.setText("✅ Course added!");
                statusLabel.getStyleClass().setAll("success-label");
                table.setItems(FXCollections.observableArrayList(courseService.getAllCourses()));
                nameField.clear();
                credField.clear();
                seatsField.clear();
                descField.clear();
            } catch (Exception ex) {
                statusLabel.setText("Error: " + ex.getMessage());
                statusLabel.getStyleClass().setAll("error-label");
            }
        });

        updateBtn.setOnAction(e -> {
            Course sel = table.getSelectionModel().getSelectedItem();
            if (sel == null) {
                statusLabel.setText("Select a course first.");
                return;
            }
            try {
                sel.setName(nameField.getText());
                sel.setCredits(Integer.parseInt(credField.getText()));
                sel.setTotalSeats(Integer.parseInt(seatsField.getText()));
                sel.setDescription(descField.getText());
                courseService.updateCourse(sel);
                statusLabel.setText("✅ Course updated!");
                statusLabel.getStyleClass().setAll("success-label");
                table.setItems(FXCollections.observableArrayList(courseService.getAllCourses()));
            } catch (Exception ex) {
                statusLabel.setText("Error: " + ex.getMessage());
            }
        });

        deleteBtn.setOnAction(e -> {
            Course sel = table.getSelectionModel().getSelectedItem();
            if (sel == null)
                return;
            try {
                courseService.deleteCourse(sel.getCourseId());
                statusLabel.setText("✅ Course deleted!");
                statusLabel.getStyleClass().setAll("success-label");
                table.setItems(FXCollections.observableArrayList(courseService.getAllCourses()));
            } catch (Exception ex) {
                statusLabel.setText("Error: " + ex.getMessage());
            }
        });

        HBox btnRow = new HBox(12, addBtn, updateBtn, deleteBtn, statusLabel);
        btnRow.setAlignment(Pos.CENTER_LEFT);
        formCard.getChildren().addAll(formTitle, nameField, credField, seatsField, descField, btnRow);

        try {
            table.setItems(FXCollections.observableArrayList(courseService.getAllCourses()));
        } catch (SQLException e) {
        }
        root.getChildren().addAll(title, table, formCard);
        ScrollPane sp = new ScrollPane(root);
        sp.setFitToWidth(true);
        sp.setFitToHeight(true);
        sp.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
        return sp;
    }

    // ===== USERS MANAGEMENT =====
    private Node buildUsersView() {
        VBox root = new VBox(20);
        Label title = new Label("👥 Manage Users & Records");
        title.getStyleClass().add("page-title");

        TableView<User> table = new TableView<>();
        table.getStyleClass().add("table-view");
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        VBox.setVgrow(table, Priority.ALWAYS);

        TableColumn<User, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getName()));
        TableColumn<User, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getEmail()));
        TableColumn<User, String> roleCol = new TableColumn<>("Role");
        roleCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getRole()));
        roleCol.setMaxWidth(100);
        TableColumn<User, String> detailCol = new TableColumn<>("Details");
        detailCol.setCellValueFactory(d -> {
            User u = d.getValue();
            if (u instanceof Student s)
                return new SimpleStringProperty("Sem " + s.getSemester() + " | " + s.getProgram());
            if (u instanceof Faculty f)
                return new SimpleStringProperty("Dept: " + f.getDepartment());
            return new SimpleStringProperty("Admin");
        });
        table.getColumns().addAll(nameCol, emailCol, roleCol, detailCol);

        Label statusLabel = new Label("");
        UserDAO userDAO = new UserDAO();

        // Add user form
        VBox form = new VBox(12);
        form.getStyleClass().add("card");
        form.setMaxWidth(600);
        Label formTitle = new Label("Add New User");
        formTitle.getStyleClass().add("card-title");
        TextField nameField = new TextField();
        nameField.setPromptText("Full Name");
        TextField emailField = new TextField();
        emailField.setPromptText("Email");
        PasswordField passField = new PasswordField();
        passField.setPromptText("Password");
        ComboBox<String> roleCombo = new ComboBox<>(FXCollections.observableArrayList("STUDENT", "FACULTY", "ADMIN"));
        roleCombo.setPromptText("Select Role");
        TextField extraField = new TextField();
        extraField.setPromptText("Program (Student) / Department (Faculty)");

        Button addBtn = new Button("Add User");
        addBtn.getStyleClass().add("btn-primary");
        Button deactivateBtn = new Button("Deactivate Selected");
        deactivateBtn.getStyleClass().add("btn-danger");

        addBtn.setOnAction(e -> {
            if (nameField.getText().isEmpty() || emailField.getText().isEmpty() || passField.getText().isEmpty()
                    || roleCombo.getValue() == null) {
                statusLabel.setText("Please fill all required fields.");
                return;
            }
            try {
                User newUser = switch (roleCombo.getValue()) {
                    case "STUDENT" -> new Student(0, nameField.getText(), emailField.getText(), passField.getText(), 0,
                            extraField.getText(), 1);
                    case "FACULTY" -> new Faculty(0, nameField.getText(), emailField.getText(), passField.getText(), 0,
                            extraField.getText());
                    default -> new Admin(0, nameField.getText(), emailField.getText(), passField.getText(), 0);
                };
                userDAO.insert(newUser);
                statusLabel.setText("✅ User added!");
                statusLabel.getStyleClass().setAll("success-label");
                table.setItems(FXCollections.observableArrayList(userDAO.findAll()));
                nameField.clear();
                emailField.clear();
                passField.clear();
                extraField.clear();
            } catch (Exception ex) {
                statusLabel.setText("Error: " + ex.getMessage());
            }
        });

        deactivateBtn.setOnAction(e -> {
            User sel = table.getSelectionModel().getSelectedItem();
            if (sel == null)
                return;
            try {
                userDAO.deactivate(sel.getUserId());
                statusLabel.setText("✅ User deactivated.");
                statusLabel.getStyleClass().setAll("success-label");
                table.setItems(FXCollections.observableArrayList(userDAO.findAll()));
            } catch (Exception ex) {
                statusLabel.setText("Error: " + ex.getMessage());
            }
        });

        HBox btnRow = new HBox(12, addBtn, deactivateBtn, statusLabel);
        btnRow.setAlignment(Pos.CENTER_LEFT);
        form.getChildren().addAll(formTitle, nameField, emailField, passField, roleCombo, extraField, btnRow);

        try {
            table.setItems(FXCollections.observableArrayList(userDAO.findAll()));
        } catch (SQLException e) {
        }
        root.getChildren().addAll(title, table, form);
        ScrollPane sp = new ScrollPane(root);
        sp.setFitToWidth(true);
        sp.setFitToHeight(true);
        sp.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
        return sp;
    }

    // ===== SCHEDULING =====
    private Node buildSchedulingView() {
        VBox root = new VBox(20);
        Label title = new Label("📅 Scheduling & Approval");
        title.getStyleClass().add("page-title");

        // Current schedules table
        TableView<Schedule> table = new TableView<>();
        table.getStyleClass().add("table-view");
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        VBox.setVgrow(table, Priority.ALWAYS);

        TableColumn<Schedule, String> courseCol = new TableColumn<>("Course");
        courseCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getCourseName()));
        TableColumn<Schedule, String> facultyCol = new TableColumn<>("Faculty");
        facultyCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getFacultyName()));
        TableColumn<Schedule, String> slotCol = new TableColumn<>("Time Slot");
        slotCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getTimeSlot()));
        TableColumn<Schedule, String> roomCol = new TableColumn<>("Room");
        roomCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getRoom()));
        TableColumn<Schedule, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getStatus()));
        table.getColumns().addAll(courseCol, facultyCol, slotCol, roomCol, statusCol);

        Label statusLabel = new Label("");
        Label clashLabel = new Label("");

        // Form
        VBox form = new VBox(12);
        form.getStyleClass().add("card");
        form.setMaxWidth(700);
        Label formTitle = new Label("Assign Schedule (Clash Detection Enabled)");
        formTitle.getStyleClass().add("card-title");

        ComboBox<Course> courseCombo = new ComboBox<>();
        courseCombo.setPromptText("Select Course");
        courseCombo.setConverter(new javafx.util.StringConverter<>() {
            public String toString(Course c) {
                return c == null ? "" : c.getName();
            }

            public Course fromString(String s) {
                return null;
            }
        });

        UserDAO userDAO = new UserDAO();
        ComboBox<User> facultyCombo = new ComboBox<>();
        facultyCombo.setPromptText("Select Faculty");
        facultyCombo.setConverter(new javafx.util.StringConverter<>() {
            public String toString(User u) {
                return u == null ? "" : u.getName();
            }

            public User fromString(String s) {
                return null;
            }
        });

        String[] slots = { "MON 09:00-11:00", "MON 11:00-13:00", "MON 14:00-16:00",
                "TUE 09:00-11:00", "TUE 11:00-13:00", "TUE 14:00-16:00",
                "WED 09:00-11:00", "WED 11:00-13:00", "WED 14:00-16:00",
                "THU 09:00-11:00", "THU 11:00-13:00", "THU 14:00-16:00",
                "FRI 09:00-11:00", "FRI 11:00-13:00", "FRI 14:00-16:00" };
        ComboBox<String> slotCombo = new ComboBox<>(FXCollections.observableArrayList(slots));
        slotCombo.setPromptText("Select Time Slot");

        TextField roomField = new TextField();
        roomField.setPromptText("Room (e.g. Room 101)");

        try {
            courseCombo.setItems(FXCollections.observableArrayList(courseService.getAllCourses()));
            facultyCombo.setItems(FXCollections.observableArrayList(userDAO.findAllFaculty()));
            table.setItems(FXCollections.observableArrayList(scheduleService.getAllSchedules()));
        } catch (SQLException e) {
        }

        // Clash check on slot select
        slotCombo.setOnAction(e -> {
            User faculty = facultyCombo.getValue();
            String slot = slotCombo.getValue();
            if (faculty != null && slot != null) {
                try {
                    boolean clash = scheduleService.detectClash(faculty.getUserId(), slot, -1);
                    if (clash) {
                        clashLabel.setText("⚠️ CLASH DETECTED: This faculty already has a class at " + slot);
                        clashLabel.getStyleClass().setAll("error-label");
                    } else {
                        clashLabel.setText("✅ No clash detected for this time slot.");
                        clashLabel.getStyleClass().setAll("success-label");
                    }
                } catch (SQLException ex) {
                }
            }
        });
        facultyCombo.setOnAction(e -> slotCombo.fireEvent(new javafx.event.ActionEvent()));

        Button addBtn = new Button("Assign Schedule");
        addBtn.getStyleClass().add("btn-primary");
        Button deleteBtn = new Button("Delete Selected");
        deleteBtn.getStyleClass().add("btn-danger");

        addBtn.setOnAction(e -> {
            if (courseCombo.getValue() == null || facultyCombo.getValue() == null
                    || slotCombo.getValue() == null || roomField.getText().isEmpty()) {
                statusLabel.setText("Please fill all fields.");
                return;
            }
            try {
                boolean clash = scheduleService.detectClash(facultyCombo.getValue().getUserId(), slotCombo.getValue(),
                        -1);
                if (clash) {
                    statusLabel.setText("❌ Cannot assign: Clash detected! Choose a different time slot.");
                    statusLabel.getStyleClass().setAll("error-label");
                    return;
                }
                Schedule sched = new Schedule(0, courseCombo.getValue().getCourseId(),
                        facultyCombo.getValue().getUserId(), slotCombo.getValue(), roomField.getText(), "APPROVED");
                scheduleService.createSchedule(sched);
                statusLabel.setText("✅ Schedule created!");
                statusLabel.getStyleClass().setAll("success-label");
                table.setItems(FXCollections.observableArrayList(scheduleService.getAllSchedules()));
            } catch (Exception ex) {
                statusLabel.setText("Error: " + ex.getMessage());
            }
        });

        deleteBtn.setOnAction(e -> {
            Schedule sel = table.getSelectionModel().getSelectedItem();
            if (sel == null)
                return;
            try {
                scheduleService.deleteSchedule(sel.getScheduleId());
                statusLabel.setText("✅ Schedule deleted!");
                statusLabel.getStyleClass().setAll("success-label");
                table.setItems(FXCollections.observableArrayList(scheduleService.getAllSchedules()));
            } catch (Exception ex) {
                statusLabel.setText("Error: " + ex.getMessage());
            }
        });

        HBox btnRow = new HBox(12, addBtn, deleteBtn);
        btnRow.setAlignment(Pos.CENTER_LEFT);
        form.getChildren().addAll(formTitle, courseCombo, facultyCombo, slotCombo, roomField, clashLabel, btnRow,
                statusLabel);

        root.getChildren().addAll(title, table, form);
        ScrollPane sp = new ScrollPane(root);
        sp.setFitToWidth(true);
        sp.setFitToHeight(true);
        sp.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
        return sp;
    }

    // ===== ALL REGISTRATIONS =====
    private Node buildRegistrationsView() {
        VBox root = new VBox(20);
        Label title = new Label("📋 All Registrations");
        title.getStyleClass().add("page-title");

        TableView<Registration> table = new TableView<>();
        table.getStyleClass().add("table-view");
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        VBox.setVgrow(table, Priority.ALWAYS);

        TableColumn<Registration, String> studentCol = new TableColumn<>("Student");
        studentCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getStudentName()));
        TableColumn<Registration, String> courseCol = new TableColumn<>("Course");
        courseCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getCourseName()));
        TableColumn<Registration, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(
                d -> new SimpleStringProperty(d.getValue().getRegistrationDate().toLocalDate().toString()));
        TableColumn<Registration, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getStatus()));
        statusCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                    return;
                }
                Label lbl = new Label(item);
                lbl.getStyleClass().add(switch (item) {
                    case "REGISTERED" -> "badge-registered";
                    case "PAYMENT_PENDING" -> "badge-pending";
                    case "DROPPED" -> "badge-dropped";
                    default -> "badge-failed";
                });
                setGraphic(lbl);
                setText(null);
            }
        });
        table.getColumns().addAll(studentCol, courseCol, dateCol, statusCol);

        try {
            table.setItems(FXCollections.observableArrayList(registrationService.getAllRegistrations()));
        } catch (SQLException e) {
        }
        root.getChildren().addAll(title, table);
        ScrollPane sp = new ScrollPane(root);
        sp.setFitToWidth(true);
        sp.setFitToHeight(true);
        sp.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
        return sp;
    }
}
