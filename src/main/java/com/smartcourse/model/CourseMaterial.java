package com.smartcourse.model;

public class CourseMaterial {
    private int materialId;
    private int courseId;
    private int facultyId;
    private String title;
    private String description;
    private String materialType; // NOTES, SLIDES, ASSIGNMENT, REFERENCE, VIDEO_LINK, OTHER
    private String content;      // text content or URL
    private String uploadDate;
    private String courseName;   // joined field for display
    private String facultyName;  // joined field for display

    public CourseMaterial() {
    }

    public CourseMaterial(int materialId, int courseId, int facultyId, String title,
                          String description, String materialType, String content, String uploadDate) {
        this.materialId = materialId;
        this.courseId = courseId;
        this.facultyId = facultyId;
        this.title = title;
        this.description = description;
        this.materialType = materialType;
        this.content = content;
        this.uploadDate = uploadDate;
    }

    // Getters & Setters
    public int getMaterialId() { return materialId; }
    public void setMaterialId(int materialId) { this.materialId = materialId; }

    public int getCourseId() { return courseId; }
    public void setCourseId(int courseId) { this.courseId = courseId; }

    public int getFacultyId() { return facultyId; }
    public void setFacultyId(int facultyId) { this.facultyId = facultyId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getMaterialType() { return materialType; }
    public void setMaterialType(String materialType) { this.materialType = materialType; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getUploadDate() { return uploadDate; }
    public void setUploadDate(String uploadDate) { this.uploadDate = uploadDate; }

    public String getCourseName() { return courseName; }
    public void setCourseName(String courseName) { this.courseName = courseName; }

    public String getFacultyName() { return facultyName; }
    public void setFacultyName(String facultyName) { this.facultyName = facultyName; }

    public String getTypeEmoji() {
        return switch (materialType) {
            case "NOTES" -> "📝";
            case "SLIDES" -> "📊";
            case "ASSIGNMENT" -> "📄";
            case "REFERENCE" -> "📚";
            case "VIDEO_LINK" -> "🎥";
            default -> "📎";
        };
    }

    @Override
    public String toString() {
        return title + " (" + materialType + ")";
    }
}
