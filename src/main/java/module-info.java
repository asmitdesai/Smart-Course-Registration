module com.smartcourse {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.base;
    requires javafx.graphics;
    requires java.sql;
    requires org.xerial.sqlitejdbc;
    requires jbcrypt;

    opens com.smartcourse to javafx.fxml;
    opens com.smartcourse.controller to javafx.fxml;
    opens com.smartcourse.model to javafx.base;

    exports com.smartcourse;
    exports com.smartcourse.controller;
    exports com.smartcourse.model;
    exports com.smartcourse.dao;
    exports com.smartcourse.service;
    exports com.smartcourse.util;
}
