package com.example.yogajava;

public class ClassInstance {
    public long id;
    public long courseId;
    public String date;
    public String teacher;
    public String comments;

    public ClassInstance(long id, long courseId, String date, String teacher, String comments) {
        this.id = id;
        this.courseId = courseId;
        this.date = date;
        this.teacher = teacher;
        this.comments = comments;
    }

    @Override
    public String toString() {
        return  date + " - " + teacher;
    }
}