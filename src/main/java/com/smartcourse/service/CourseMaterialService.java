package com.smartcourse.service;

import com.smartcourse.dao.CourseMaterialDAO;
import com.smartcourse.model.CourseMaterial;
import java.sql.SQLException;
import java.util.List;

public class CourseMaterialService {
    private static CourseMaterialService instance;
    private final CourseMaterialDAO materialDAO = new CourseMaterialDAO();

    private CourseMaterialService() {
    }

    public static CourseMaterialService getInstance() {
        if (instance == null)
            instance = new CourseMaterialService();
        return instance;
    }

    public List<CourseMaterial> getMaterialsByCourse(int courseId) throws SQLException {
        return materialDAO.findByCourse(courseId);
    }

    public List<CourseMaterial> getMaterialsByFaculty(int facultyId) throws SQLException {
        return materialDAO.findByFaculty(facultyId);
    }

    public List<CourseMaterial> getMaterialsForStudent(int studentId) throws SQLException {
        return materialDAO.findByStudentCourses(studentId);
    }

    public void addMaterial(CourseMaterial material) throws SQLException {
        materialDAO.insert(material);
    }

    public void updateMaterial(CourseMaterial material) throws SQLException {
        materialDAO.update(material);
    }

    public void deleteMaterial(int materialId) throws SQLException {
        materialDAO.delete(materialId);
    }
}
