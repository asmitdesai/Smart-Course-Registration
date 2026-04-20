package com.smartcourse.model;

import java.util.List;

public class Course {
    private int courseId;
    private String name;
    private int credits;
    private int seatsAvailable;
    private int totalSeats;
    private String description;
    private List<Integer> prerequisiteIds; // list of courseIds

    public Course() {
    }

    public Course(int courseId, String name, int credits, int seatsAvailable, int totalSeats, String description) {
        this.courseId = courseId;
        this.name = name;
        this.credits = credits;
        this.seatsAvailable = seatsAvailable;
        this.totalSeats = totalSeats;
        this.description = description;
    }

    public boolean hasSeatAvailable() {
        return seatsAvailable > 0;
    }

    // Getters & Setters
    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCredits() {
        return credits;
    }

    public void setCredits(int credits) {
        this.credits = credits;
    }

    public int getSeatsAvailable() {
        return seatsAvailable;
    }

    public void setSeatsAvailable(int seatsAvailable) {
        this.seatsAvailable = seatsAvailable;
    }

    public int getTotalSeats() {
        return totalSeats;
    }

    public void setTotalSeats(int totalSeats) {
        this.totalSeats = totalSeats;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Integer> getPrerequisiteIds() {
        return prerequisiteIds;
    }

    public void setPrerequisiteIds(List<Integer> prerequisiteIds) {
        this.prerequisiteIds = prerequisiteIds;
    }

    @Override
    public String toString() {
        return name + " (" + credits + " credits)";
    }
}
