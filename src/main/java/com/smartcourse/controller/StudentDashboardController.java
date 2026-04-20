package com.smartcourse.controller;

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
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.net.URL;
import java.sql.SQLException;
import java.util.*;

public class StudentDashboardController implements Initializable {
    @FXML
    private StackPane contentPane;
    @FXML
    private Label sidebarName;
    @FXML
    private Label sidebarEmail;

    private Student currentStudent;
    private final CourseService courseService = CourseService.getInstance();
    private final RegistrationService registrationService = RegistrationService.getInstance();
    private final ScheduleService scheduleService = ScheduleService.getInstance();
    private final AcademicService academicService = AcademicService.getInstance();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        User user = AuthService.getInstance().getCurrentUser();
        currentStudent = (Student) user;
        sidebarName.setText(user.getName());
        sidebarEmail.setText(user.getEmail());
        showDashboard();
    }

    @FXML
    void showDashboard() {
        setContent(buildDashboardView());
    }

    @FXML
    void showBrowse() {
        setContent(buildBrowseView());
    }

    @FXML
    void showRegistrations() {
        setContent(buildRegistrationsView());
    }

    @FXML
    void showSchedule() {
        setContent(buildScheduleView());
    }

    @FXML
    void showGrades() {
        setContent(buildGradesView());
    }

    @FXML
    void showAttendance() {
        setContent(buildAttendanceView());
    }

    @FXML
    void handleLogout() {
        AuthService.getInstance().logout();
        SceneManager.switchScene("/fxml/login.fxml", "Smart Course Registration");
    }

    private void setContent(Node node) {
        contentPane.getChildren().setAll(node);
    }

    // ===== DASHBOARD HOME =====
    private Node buildDashboardView() {
        VBox root = new VBox(24);
        root.setPadding(new Insets(0));

        // Header
        VBox header = new VBox(4);
        Label title = new Label("Welcome back, " + currentStudent.getName().split(" ")[0] + "! 👋");
        title.getStyleClass().add("page-title");
        Label subtitle = new Label("Here's your academic overview");
        subtitle.getStyleClass().add("page-subtitle");
        header.getChildren().addAll(title, subtitle);

        // Stats Cards
        HBox stats = new HBox(16);
        try {
            long regCount = registrationService.getStudentRegistrations(currentStudent.getUserId())
                    .stream().filter(r -> "REGISTERED".equals(r.getStatus())).count();
            long gradeCount = academicService.getStudentGrades(currentStudent.getUserId()).size();
            long attCount = academicService.getStudentAttendance(currentStudent.getUserId()).size();

            stats.getChildren().addAll(
                    buildStatCard("📚 Registered Courses", String.valueOf(regCount), "#6366f1"),
                    buildStatCard("🏆 Grades Available", String.valueOf(gradeCount), "#8b5cf6"),
                    buildStatCard("📋 Attendance Records", String.valueOf(attCount), "#06b6d4"),
                    buildStatCard("🎓 Semester", String.valueOf(currentStudent.getSemester()), "#10b981"));
        } catch (SQLException e) {
            stats.getChildren().add(new Label("Error loading stats"));
        }

        // Quick actions
        Label actLabel = new Label("Quick Actions");
        actLabel.getStyleClass().add("card-title");

        HBox actions = new HBox(12);
        Button browseBtn = createActionButton("📚 Browse & Register Courses", "#6366f1");
        browseBtn.setOnAction(e -> showBrowse());
        Button gradesBtn = createActionButton("🏆 View Grades", "#8b5cf6");
        gradesBtn.setOnAction(e -> showGrades());
        Button scheduleBtn = createActionButton("📅 View Schedule", "#06b6d4");
        scheduleBtn.setOnAction(e -> showSchedule());
        actions.getChildren().addAll(browseBtn, gradesBtn, scheduleBtn);

        // Recent registrations
        VBox recentBox = new VBox(12);
        recentBox.getStyleClass().add("card");
        Label recentLabel = new Label("Recent Registrations");
        recentLabel.getStyleClass().add("card-title");
        recentBox.getChildren().add(recentLabel);
        try {
            List<Registration> regs = registrationService.getStudentRegistrations(currentStudent.getUserId());
            regs.stream().limit(4).forEach(r -> {
                HBox row = new HBox(16);
                row.setAlignment(Pos.CENTER_LEFT);
                Label courseLbl = new Label("📘 " + r.getCourseName());
                courseLbl.setStyle("-fx-text-fill: #e2e8f0; -fx-font-size: 13px;");
                Region spacer = new Region();
                HBox.setHgrow(spacer, Priority.ALWAYS);
                Label badge = new Label(r.getStatus());
                badge.getStyleClass().add(getBadgeClass(r.getStatus()));
                row.getChildren().addAll(courseLbl, spacer, badge);
                recentBox.getChildren().add(row);
            });
            if (regs.isEmpty())
                recentBox.getChildren().add(new Label("No registrations yet. Browse courses to get started!") {
                    {
                        setStyle("-fx-text-fill:#64748b;");
                    }
                });
        } catch (SQLException e) {
            recentBox.getChildren().add(new Label("Error: " + e.getMessage()));
        }

        root.getChildren().addAll(header, stats, actLabel, actions, recentBox);
        ScrollPane sp = new ScrollPane(root);
        sp.setFitToWidth(true);
        sp.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
        return sp;
    }

    private HBox buildStatCard(String label, String value, String color) {
        VBox card = new VBox(6);
        card.getStyleClass().add("card");
        card.setPrefWidth(200);
        card.setAlignment(Pos.CENTER_LEFT);
        Label val = new Label(value);
        val.getStyleClass().add("metric-value");
        val.setStyle("-fx-text-fill: " + color + ";");
        Label lbl = new Label(label);
        lbl.getStyleClass().add("metric-label");
        card.getChildren().addAll(val, lbl);
        HBox box = new HBox(card);
        HBox.setHgrow(card, Priority.ALWAYS);
        return box;
    }

    private Button createActionButton(String text, String color) {
        Button btn = new Button(text);
        btn.setStyle("-fx-background-color: rgba(99,102,241,0.12); -fx-text-fill: " + color + "; " +
                "-fx-font-size: 13px; -fx-font-weight: 600; -fx-padding: 14 20; " +
                "-fx-background-radius: 12; -fx-border-color: " + color
                + "33; -fx-border-radius: 12; -fx-cursor: hand;");
        btn.setOnMouseEntered(e -> btn.setStyle(btn.getStyle() + " -fx-background-color: rgba(99,102,241,0.2);"));
        return btn;
    }

    // ===== BROWSE COURSES =====
    private Node buildBrowseView() {
        VBox root = new VBox(20);

        Label title = new Label("📚 Browse Courses");
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
        TableColumn<Course, String> seatsCol = new TableColumn<>("Seats Available");
        seatsCol.setCellValueFactory(
                d -> new SimpleStringProperty(d.getValue().getSeatsAvailable() + "/" + d.getValue().getTotalSeats()));
        TableColumn<Course, String> descCol = new TableColumn<>("Description");
        descCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getDescription()));

        label_status: {
            TableColumn<Course, String> statusCol = new TableColumn<>("Status");
            statusCol.setCellValueFactory(
                    d -> new SimpleStringProperty(d.getValue().getSeatsAvailable() > 0 ? "Available" : "Full"));
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
                    lbl.getStyleClass().add("Full".equals(item) ? "badge-failed" : "badge-registered");
                    setGraphic(lbl);
                    setText(null);
                }
            });
            table.getColumns().addAll(nameCol, creditsCol, seatsCol, descCol, statusCol);
        }

        Label statusLabel = new Label("");
        statusLabel.getStyleClass().add("info-label");

        Button registerBtn = new Button("Register Selected Course");
        registerBtn.getStyleClass().add("btn-primary");
        registerBtn.setOnAction(e -> {
            Course selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) {
                statusLabel.setText("⚠️ Please select a course first.");
                statusLabel.getStyleClass().setAll("error-label");
                return;
            }
            handleRegistration(selected, statusLabel);
        });

        try {
            table.setItems(FXCollections.observableArrayList(courseService.getAllCourses()));
        } catch (SQLException e) {
            statusLabel.setText("Error loading courses: " + e.getMessage());
        }

        HBox btnRow = new HBox(12, registerBtn, statusLabel);
        btnRow.setAlignment(Pos.CENTER_LEFT);
        root.getChildren().addAll(title, table, btnRow);

        ScrollPane sp = new ScrollPane(root);
        sp.setFitToWidth(true);
        sp.setFitToHeight(true);
        sp.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
        return sp;
    }

    private void handleRegistration(Course course, Label statusLabel) {
        try {
            RegistrationService.RegistrationResult result = registrationService.register(
                    currentStudent.getUserId(), course.getCourseId(), courseService);

            switch (result) {
                case ALREADY_REGISTERED -> {
                    statusLabel.setText("⚠️ You're already registered for " + course.getName());
                    statusLabel.getStyleClass().setAll("error-label");
                }
                case SEAT_FULL -> {
                    statusLabel.setText("❌ Course Full: No seats available for " + course.getName());
                    statusLabel.getStyleClass().setAll("error-label");
                }
                case PREREQUISITE_NOT_MET -> {
                    statusLabel.setText("❌ Prerequisites not met for " + course.getName());
                    statusLabel.getStyleClass().setAll("error-label");
                }
                case SUCCESS -> showPaymentDialog(course, statusLabel);
            }
        } catch (SQLException e) {
            statusLabel.setText("Error: " + e.getMessage());
        }
    }

    private void showPaymentDialog(Course course, Label statusLabel) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("💳 Fee Payment");
        dialog.setHeaderText("Complete Payment for " + course.getName());

        VBox content = new VBox(16);
        content.setPadding(new Insets(20));

        double amount = course.getCredits() * 150.0;
        content.getChildren().addAll(
                styledLabel("Course: " + course.getName()),
                styledLabel("Credits: " + course.getCredits()),
                styledLabel(String.format("Amount Due: $%.2f", amount)),
                new Separator(),
                styledLabel("Card Number:"),
                cardField("**** **** **** 1234"),
                styledLabel("Expiry:"), cardField("MM/YY"),
                styledLabel("CVV:"), cardField("•••"));

        ((DialogPane) dialog.getDialogPane()).setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        dialog.getDialogPane().getStyleClass().add("dialog-pane");

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                // Find the pending registration
                List<Registration> regs = registrationService.getStudentRegistrations(currentStudent.getUserId());
                Optional<Registration> pending = regs.stream()
                        .filter(r -> r.getCourseId() == course.getCourseId() && "PAYMENT_PENDING".equals(r.getStatus()))
                        .findFirst();
                if (pending.isPresent()) {
                    boolean success = registrationService.processPayment(currentStudent.getUserId(),
                            course.getCourseId(), pending.get().getRegId());
                    if (success) {
                        statusLabel.setText("✅ Successfully registered for " + course.getName() + "!");
                        statusLabel.getStyleClass().setAll("success-label");
                        showBrowse();
                    } else {
                        statusLabel.setText("❌ Payment failed. Please try again.");
                        statusLabel.getStyleClass().setAll("error-label");
                    }
                }
            } catch (SQLException e) {
                statusLabel.setText("Error: " + e.getMessage());
            }
        } else {
            statusLabel.setText("ℹ️ Registration cancelled.");
            statusLabel.getStyleClass().setAll("info-label");
        }
    }

    private Label styledLabel(String text) {
        Label l = new Label(text);
        l.setStyle("-fx-text-fill: #e2e8f0; -fx-font-size: 13px;");
        return l;
    }

    private TextField cardField(String prompt) {
        TextField tf = new TextField();
        tf.setPromptText(prompt);
        return tf;
    }

    // ===== MY REGISTRATIONS =====
    private Node buildRegistrationsView() {
        VBox root = new VBox(20);
        Label title = new Label("✅ My Registrations");
        title.getStyleClass().add("page-title");

        TableView<Registration> table = new TableView<>();
        table.getStyleClass().add("table-view");
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        VBox.setVgrow(table, Priority.ALWAYS);

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
                lbl.getStyleClass().add(getBadgeClass(item));
                setGraphic(lbl);
                setText(null);
            }
        });
        table.getColumns().addAll(courseCol, dateCol, statusCol);

        Label statusLabel = new Label("");

        Button dropBtn = new Button("Drop Selected Course");
        dropBtn.getStyleClass().add("btn-danger");
        dropBtn.setOnAction(e -> {
            Registration selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) {
                statusLabel.setText("Please select a course to drop.");
                return;
            }
            if (!"REGISTERED".equals(selected.getStatus())) {
                statusLabel.setText("Can only drop REGISTERED courses.");
                return;
            }
            try {
                registrationService.dropCourse(selected.getRegId(), selected.getCourseId());
                statusLabel.setText("✅ Dropped " + selected.getCourseName() + " successfully.");
                statusLabel.getStyleClass().setAll("success-label");
                showRegistrations();
            } catch (SQLException ex) {
                statusLabel.setText("Error: " + ex.getMessage());
            }
        });

        try {
            table.setItems(FXCollections
                    .observableArrayList(registrationService.getStudentRegistrations(currentStudent.getUserId())));
        } catch (SQLException e) {
            statusLabel.setText("Error: " + e.getMessage());
        }

        HBox btnRow = new HBox(12, dropBtn, statusLabel);
        btnRow.setAlignment(Pos.CENTER_LEFT);
        root.getChildren().addAll(title, table, btnRow);

        ScrollPane sp = new ScrollPane(root);
        sp.setFitToWidth(true);
        sp.setFitToHeight(true);
        sp.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
        return sp;
    }

    // ===== SCHEDULE =====
    private Node buildScheduleView() {
        VBox root = new VBox(20);
        Label title = new Label("📅 My Schedule");
        title.getStyleClass().add("page-title");

        TableView<com.smartcourse.model.Schedule> table = new TableView<>();
        table.getStyleClass().add("table-view");
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        VBox.setVgrow(table, Priority.ALWAYS);

        TableColumn<com.smartcourse.model.Schedule, String> courseCol = new TableColumn<>("Course");
        courseCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getCourseName()));
        TableColumn<com.smartcourse.model.Schedule, String> slotCol = new TableColumn<>("Time Slot");
        slotCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getTimeSlot()));
        TableColumn<com.smartcourse.model.Schedule, String> roomCol = new TableColumn<>("Room");
        roomCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getRoom()));
        TableColumn<com.smartcourse.model.Schedule, String> facultyCol = new TableColumn<>("Faculty");
        facultyCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getFacultyName()));
        table.getColumns().addAll(courseCol, slotCol, roomCol, facultyCol);

        try {
            table.setItems(FXCollections
                    .observableArrayList(scheduleService.getSchedulesByStudent(currentStudent.getUserId())));
        } catch (SQLException e) {
            root.getChildren().add(new Label("Error: " + e.getMessage()));
        }

        root.getChildren().addAll(title, table);
        ScrollPane sp = new ScrollPane(root);
        sp.setFitToWidth(true);
        sp.setFitToHeight(true);
        sp.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
        return sp;
    }

    // ===== GRADES =====
    private Node buildGradesView() {
        VBox root = new VBox(20);
        Label title = new Label("🏆 My Academic Grades");
        title.getStyleClass().add("page-title");

        TableView<Grade> table = new TableView<>();
        table.getStyleClass().add("table-view");
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        VBox.setVgrow(table, Priority.ALWAYS);

        TableColumn<Grade, String> courseCol = new TableColumn<>("Course");
        courseCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getCourseName()));
        TableColumn<Grade, String> marksCol = new TableColumn<>("Marks");
        marksCol.setCellValueFactory(d -> new SimpleStringProperty(String.format("%.1f", d.getValue().getMarks())));
        marksCol.setMaxWidth(100);
        TableColumn<Grade, String> gradeCol = new TableColumn<>("Grade");
        gradeCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getLetterGrade()));
        gradeCol.setMaxWidth(80);
        gradeCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                    return;
                }
                Label lbl = new Label(item);
                String color = switch (item) {
                    case "A+", "A" -> "#4ade80";
                    case "B" -> "#60a5fa";
                    case "C" -> "#fbbf24";
                    case "D" -> "#fb923c";
                    default -> "#f87171";
                };
                lbl.setStyle("-fx-text-fill: " + color + "; -fx-font-weight: 700; -fx-font-size: 14px;");
                setGraphic(lbl);
                setText(null);
            }
        });
        table.getColumns().addAll(courseCol, marksCol, gradeCol);

        try {
            table.setItems(
                    FXCollections.observableArrayList(academicService.getStudentGrades(currentStudent.getUserId())));
        } catch (SQLException e) {
            root.getChildren().add(new Label("Error: " + e.getMessage()));
        }

        root.getChildren().addAll(title, table);
        ScrollPane sp = new ScrollPane(root);
        sp.setFitToWidth(true);
        sp.setFitToHeight(true);
        sp.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
        return sp;
    }

    // ===== ATTENDANCE =====
    private Node buildAttendanceView() {
        VBox root = new VBox(20);
        Label title = new Label("📋 My Attendance");
        title.getStyleClass().add("page-title");

        VBox cards = new VBox(12);
        try {
            List<Attendance> attList = academicService.getStudentAttendance(currentStudent.getUserId());
            if (attList.isEmpty()) {
                cards.getChildren().add(new Label("No attendance records yet.") {
                    {
                        setStyle("-fx-text-fill:#64748b; -fx-font-size:14px;");
                    }
                });
            }
            for (Attendance att : attList) {
                VBox card = new VBox(10);
                card.getStyleClass().add("card");
                HBox header = new HBox(16);
                header.setAlignment(Pos.CENTER_LEFT);
                Label courseLbl = new Label("📘 " + att.getCourseName());
                courseLbl.setStyle("-fx-font-size:15px; -fx-font-weight:600; -fx-text-fill:white;");
                Region spacer = new Region();
                HBox.setHgrow(spacer, Priority.ALWAYS);
                double pct = att.getPercentage();
                String pctStr = String.format("%.1f%%", pct);
                Label pctLbl = new Label(pctStr);
                pctLbl.setStyle("-fx-font-size:18px; -fx-font-weight:700; -fx-text-fill:"
                        + (pct >= 75 ? "#4ade80" : "#f87171") + ";");
                header.getChildren().addAll(courseLbl, spacer, pctLbl);

                ProgressBar pb = new ProgressBar(pct / 100.0);
                pb.setMaxWidth(Double.MAX_VALUE);
                pb.getStyleClass().add("progress-bar");

                HBox detail = new HBox(24);
                detail.getChildren().addAll(
                        new Label("Classes attended: " + att.getAttendedClasses()) {
                            {
                                setStyle("-fx-text-fill:#94a3b8; -fx-font-size:12px;");
                            }
                        },
                        new Label("Total classes: " + att.getTotalClasses()) {
                            {
                                setStyle("-fx-text-fill:#94a3b8; -fx-font-size:12px;");
                            }
                        });

                card.getChildren().addAll(header, pb, detail);
                cards.getChildren().add(card);
            }
        } catch (SQLException e) {
            cards.getChildren().add(new Label("Error: " + e.getMessage()));
        }

        root.getChildren().addAll(title, cards);
        ScrollPane sp = new ScrollPane(root);
        sp.setFitToWidth(true);
        sp.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
        return sp;
    }

    private String getBadgeClass(String status) {
        return switch (status) {
            case "REGISTERED" -> "badge-registered";
            case "PAYMENT_PENDING" -> "badge-pending";
            case "DROPPED" -> "badge-dropped";
            default -> "badge-failed";
        };
    }
}
