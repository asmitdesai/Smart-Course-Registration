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

import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

public class FacultyDashboardController implements Initializable {
    @FXML
    private StackPane contentPane;
    @FXML
    private Label sidebarName;
    @FXML
    private Label sidebarEmail;

    private Faculty currentFaculty;
    private final ScheduleService scheduleService = ScheduleService.getInstance();
    private final AcademicService academicService = AcademicService.getInstance();
    private final CourseService courseService = CourseService.getInstance();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        User user = AuthService.getInstance().getCurrentUser();
        currentFaculty = (Faculty) user;
        sidebarName.setText(user.getName());
        sidebarEmail.setText(user.getEmail());
        showDashboard();
    }

    @FXML
    void showDashboard() {
        setContent(buildDashboardView());
    }

    @FXML
    void showMyCourses() {
        setContent(buildCoursesView());
    }

    @FXML
    void showAttendance() {
        setContent(buildAttendanceView());
    }

    @FXML
    void showEvaluate() {
        setContent(buildEvaluateView());
    }

    @FXML
    void showSchedule() {
        setContent(buildScheduleView());
    }

    @FXML
    void handleLogout() {
        AuthService.getInstance().logout();
        SceneManager.switchScene("/fxml/login.fxml", "Smart Course Registration");
    }

    private void setContent(Node node) {
        contentPane.getChildren().setAll(node);
    }

    private Node buildDashboardView() {
        VBox root = new VBox(24);
        Label title = new Label("Welcome, Prof. "
                + currentFaculty.getName().split(" ")[currentFaculty.getName().split(" ").length - 1] + "! 👨‍🏫");
        title.getStyleClass().add("page-title");
        Label sub = new Label("Department of " + currentFaculty.getDepartment());
        sub.getStyleClass().add("page-subtitle");

        HBox stats = new HBox(16);
        try {
            long courseCount = scheduleService.getSchedulesByFaculty(currentFaculty.getUserId()).size();
            stats.getChildren().addAll(
                    buildStatCard("📚 Assigned Courses", String.valueOf(courseCount), "#6366f1"),
                    buildStatCard("🏫 Department", currentFaculty.getDepartment(), "#8b5cf6"),
                    buildStatCard("👤 Faculty ID", String.valueOf(currentFaculty.getFacultyId()), "#06b6d4"));
        } catch (SQLException e) {
            stats.getChildren().add(new Label("Error"));
        }

        Label infoCard = new Label("Use the sidebar to manage attendance, grades, and view your course schedule.");
        infoCard.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 14px;");
        infoCard.setWrapText(true);

        root.getChildren().addAll(title, sub, stats, infoCard);
        ScrollPane sp = new ScrollPane(root);
        sp.setFitToWidth(true);
        sp.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
        return sp;
    }

    private HBox buildStatCard(String label, String value, String color) {
        VBox card = new VBox(6);
        card.getStyleClass().add("card");
        card.setPrefWidth(220);
        Label val = new Label(value);
        val.setStyle("-fx-font-size: 28px; -fx-font-weight: 700; -fx-text-fill: " + color + ";");
        Label lbl = new Label(label);
        lbl.getStyleClass().add("metric-label");
        card.getChildren().addAll(val, lbl);
        HBox box = new HBox(card);
        HBox.setHgrow(card, Priority.ALWAYS);
        return box;
    }

    private Node buildCoursesView() {
        VBox root = new VBox(20);
        Label title = new Label("📚 My Assigned Courses");
        title.getStyleClass().add("page-title");

        TableView<Schedule> table = new TableView<>();
        table.getStyleClass().add("table-view");
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        VBox.setVgrow(table, Priority.ALWAYS);

        TableColumn<Schedule, String> courseCol = new TableColumn<>("Course");
        courseCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getCourseName()));
        TableColumn<Schedule, String> slotCol = new TableColumn<>("Time Slot");
        slotCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getTimeSlot()));
        TableColumn<Schedule, String> roomCol = new TableColumn<>("Room");
        roomCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getRoom()));
        TableColumn<Schedule, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getStatus()));
        table.getColumns().addAll(courseCol, slotCol, roomCol, statusCol);

        try {
            table.setItems(FXCollections
                    .observableArrayList(scheduleService.getSchedulesByFaculty(currentFaculty.getUserId())));
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

    private Node buildAttendanceView() {
        VBox root = new VBox(24);
        Label title = new Label("📋 Manage Attendance");
        title.getStyleClass().add("page-title");

        // Course selector card
        VBox selectorCard = new VBox(12);
        selectorCard.getStyleClass().add("card");
        Label selectTitle = new Label("Select Course");
        selectTitle.getStyleClass().add("card-title");

        ComboBox<Schedule> courseCombo = new ComboBox<>();
        courseCombo.setPromptText("Select a course...");
        courseCombo.setMaxWidth(Double.MAX_VALUE);
        try {
            courseCombo.setItems(FXCollections
                    .observableArrayList(scheduleService.getSchedulesByFaculty(currentFaculty.getUserId())));
            courseCombo.setConverter(new javafx.util.StringConverter<>() {
                public String toString(Schedule s) {
                    return s == null ? "" : s.getCourseName();
                }

                public Schedule fromString(String str) {
                    return null;
                }
            });
        } catch (SQLException e) {
        }
        selectorCard.getChildren().addAll(selectTitle, courseCombo);

        TableView<Attendance> table = new TableView<>();
        table.getStyleClass().add("table-view");
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        VBox.setVgrow(table, Priority.ALWAYS);

        TableColumn<Attendance, String> studentCol = new TableColumn<>("Student");
        studentCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getStudentName()));
        TableColumn<Attendance, String> totalCol = new TableColumn<>("Total Classes");
        totalCol.setCellValueFactory(d -> new SimpleStringProperty(String.valueOf(d.getValue().getTotalClasses())));
        TableColumn<Attendance, String> attCol = new TableColumn<>("Attended");
        attCol.setCellValueFactory(d -> new SimpleStringProperty(String.valueOf(d.getValue().getAttendedClasses())));
        TableColumn<Attendance, String> pctCol = new TableColumn<>("Percentage");
        pctCol.setCellValueFactory(
                d -> new SimpleStringProperty(String.format("%.1f%%", d.getValue().getPercentage())));
        table.getColumns().addAll(studentCol, totalCol, attCol, pctCol);

        Label statusLabel = new Label("");

        courseCombo.setOnAction(e -> {
            Schedule selected = courseCombo.getValue();
            if (selected == null)
                return;
            try {
                table.setItems(
                        FXCollections.observableArrayList(academicService.getCourseAttendance(selected.getCourseId())));
            } catch (SQLException ex) {
                statusLabel.setText("Error: " + ex.getMessage());
            }
        });

        // Edit form
        VBox formCard = new VBox(16);
        formCard.getStyleClass().add("card");
        formCard.setMaxWidth(600);
        Label formTitle = new Label("Update Records");
        formTitle.getStyleClass().add("card-title");

        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(12);

        Label l1 = new Label("TOTAL CLASSES");
        l1.getStyleClass().add("label-form");
        TextField totalField = new TextField();
        totalField.setPromptText("e.g. 40");
        grid.add(l1, 0, 0);
        grid.add(totalField, 0, 1);

        Label l2 = new Label("ATTENDED CLASSES");
        l2.getStyleClass().add("label-form");
        TextField attendedField = new TextField();
        attendedField.setPromptText("e.g. 35");
        grid.add(l2, 1, 0);
        grid.add(attendedField, 1, 1);

        Button saveBtn = new Button("Save Attendance");
        saveBtn.getStyleClass().add("btn-primary");
        saveBtn.setMaxWidth(Double.MAX_VALUE);
        saveBtn.setOnAction(e -> {
            Attendance sel = table.getSelectionModel().getSelectedItem();
            if (sel == null || totalField.getText().isEmpty() || attendedField.getText().isEmpty()) {
                statusLabel.setText("⚠️ Select a student and enter values.");
                statusLabel.getStyleClass().setAll("warning-label");
                return;
            }
            try {
                sel.setTotalClasses(Integer.parseInt(totalField.getText()));
                sel.setAttendedClasses(Integer.parseInt(attendedField.getText()));
                academicService.saveAttendance(sel);
                statusLabel.setText("✅ Attendance saved successfully!");
                statusLabel.getStyleClass().setAll("success-label");
                Schedule cs = courseCombo.getValue();
                if (cs != null)
                    table.setItems(
                            FXCollections.observableArrayList(academicService.getCourseAttendance(cs.getCourseId())));
            } catch (Exception ex) {
                statusLabel.setText("❌ Error: " + ex.getMessage());
                statusLabel.getStyleClass().setAll("error-label");
            }
        });

        formCard.getChildren().addAll(formTitle, grid, saveBtn, statusLabel);

        root.getChildren().addAll(title, selectorCard, table, formCard);
        ScrollPane sp = new ScrollPane(root);
        sp.setFitToWidth(true);
        sp.setFitToHeight(true);
        sp.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
        return sp;
    }

    private Node buildEvaluateView() {
        VBox root = new VBox(24);
        Label title = new Label("🏆 Evaluate Students");
        title.getStyleClass().add("page-title");

        VBox selectorCard = new VBox(12);
        selectorCard.getStyleClass().add("card");
        Label selectTitle = new Label("Select Course");
        selectTitle.getStyleClass().add("card-title");

        ComboBox<Schedule> courseCombo = new ComboBox<>();
        courseCombo.setPromptText("Select a course...");
        courseCombo.setMaxWidth(Double.MAX_VALUE);
        try {
            courseCombo.setItems(FXCollections
                    .observableArrayList(scheduleService.getSchedulesByFaculty(currentFaculty.getUserId())));
            courseCombo.setConverter(new javafx.util.StringConverter<>() {
                public String toString(Schedule s) {
                    return s == null ? "" : s.getCourseName();
                }

                public Schedule fromString(String str) {
                    return null;
                }
            });
        } catch (SQLException e) {
        }
        selectorCard.getChildren().addAll(selectTitle, courseCombo);

        TableView<Grade> table = new TableView<>();
        table.getStyleClass().add("table-view");
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        VBox.setVgrow(table, Priority.ALWAYS);

        TableColumn<Grade, String> studentCol = new TableColumn<>("Student");
        studentCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getStudentName()));
        TableColumn<Grade, String> marksCol = new TableColumn<>("Marks");
        marksCol.setCellValueFactory(d -> new SimpleStringProperty(String.format("%.1f", d.getValue().getMarks())));
        TableColumn<Grade, String> gradeCol = new TableColumn<>("Grade");
        gradeCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getLetterGrade()));
        table.getColumns().addAll(studentCol, marksCol, gradeCol);

        Label statusLabel = new Label("");

        courseCombo.setOnAction(e -> {
            Schedule s = courseCombo.getValue();
            if (s == null)
                return;
            try {
                table.setItems(FXCollections.observableArrayList(academicService.getCourseGrades(s.getCourseId())));
            } catch (SQLException ex) {
                statusLabel.setText("Error: " + ex.getMessage());
            }
        });

        // Evaluation form
        VBox formCard = new VBox(16);
        formCard.getStyleClass().add("card");
        formCard.setMaxWidth(600);
        Label formTitle = new Label("Submit Marks");
        formTitle.getStyleClass().add("card-title");

        Label l1 = new Label("STUDENT MARKS (0-100)");
        l1.getStyleClass().add("label-form");
        TextField marksField = new TextField();
        marksField.setPromptText("e.g. 85.5");

        Button saveBtn = new Button("Save Grade");
        saveBtn.getStyleClass().add("btn-primary");
        saveBtn.setMaxWidth(Double.MAX_VALUE);
        saveBtn.setOnAction(e -> {
            Grade sel = table.getSelectionModel().getSelectedItem();
            if (sel == null || marksField.getText().isEmpty()) {
                statusLabel.setText("⚠️ Select student and enter marks.");
                statusLabel.getStyleClass().setAll("warning-label");
                return;
            }
            try {
                double marks = Double.parseDouble(marksField.getText());
                sel.setMarks(marks);
                academicService.saveGrade(sel);
                statusLabel.setText("✅ Grade saved successfully!");
                statusLabel.getStyleClass().setAll("success-label");
                Schedule cs = courseCombo.getValue();
                if (cs != null)
                    table.setItems(
                            FXCollections.observableArrayList(academicService.getCourseGrades(cs.getCourseId())));
            } catch (Exception ex) {
                statusLabel.setText("❌ Error: " + ex.getMessage());
                statusLabel.getStyleClass().setAll("error-label");
            }
        });

        formCard.getChildren().addAll(formTitle, l1, marksField, saveBtn, statusLabel);

        root.getChildren().addAll(title, selectorCard, table, formCard);

        ScrollPane sp = new ScrollPane(root);
        sp.setFitToWidth(true);
        sp.setFitToHeight(true);
        sp.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
        return sp;
    }

    private Node buildScheduleView() {
        VBox root = new VBox(20);
        Label title = new Label("📅 My Schedule");
        title.getStyleClass().add("page-title");

        TableView<Schedule> table = new TableView<>();
        table.getStyleClass().add("table-view");
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        VBox.setVgrow(table, Priority.ALWAYS);

        TableColumn<Schedule, String> courseCol = new TableColumn<>("Course");
        courseCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getCourseName()));
        TableColumn<Schedule, String> slotCol = new TableColumn<>("Day & Time");
        slotCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getTimeSlot()));
        TableColumn<Schedule, String> roomCol = new TableColumn<>("Room");
        roomCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getRoom()));
        table.getColumns().addAll(courseCol, slotCol, roomCol);

        try {
            table.setItems(FXCollections
                    .observableArrayList(scheduleService.getSchedulesByFaculty(currentFaculty.getUserId())));
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
