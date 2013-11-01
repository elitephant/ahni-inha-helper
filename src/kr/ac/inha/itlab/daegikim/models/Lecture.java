package kr.ac.inha.itlab.daegikim.models;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

public class Lecture {
    private String haksoo;          //학수번호
    private String lectureName;     //강의명
    private String professorName;   //교수명
    private String credit;          //학점
    private String grade;           //학년
    private String type;            //구분(교양필수, 전공필수, 전공선택, 전공필수 등)
    private String timeClassroom;   //시간 & 강의실
    private String year;            //연도
    private String semester;        //학기

    public Lecture() {
    }

    public Lecture(String haksoo, String lectureName, String professorName, String credit, String grade, String type, String timeClassroom, String year, String semester) {
        this.haksoo = haksoo;
        this.lectureName = lectureName;
        this.professorName = professorName;
        this.credit = credit;
        this.grade = grade;
        this.type = type;
        this.timeClassroom = timeClassroom;
        this.year = year;
        this.semester = semester;
    }

    public String getHaksoo() {
        return haksoo;
    }

    public void setHaksoo(String haksoo) {
        this.haksoo = haksoo;
    }

    public String getLectureName() {
        return lectureName;
    }

    public void setLectureName(String lectureName) {
        this.lectureName = lectureName;
    }

    public String getProfessorName() {
        return professorName;
    }

    public void setProfessorName(String professorName) {
        this.professorName = professorName;
    }

    public String getCredit() {
        return credit;
    }

    public void setCredit(String credit) {
        this.credit = credit;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTimeClassroom() {
        return timeClassroom;
    }

    public void setTimeClassroom(String timeClassroom) {
        this.timeClassroom = timeClassroom;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    public static Lecture mongoObjectToLecture(DBObject dbObject) {
        Lecture lecture = new Lecture();

        lecture.setHaksoo(String.valueOf(dbObject.get("haksoo")));
        lecture.setLectureName(String.valueOf(dbObject.get("lectureName")));
        lecture.setProfessorName(String.valueOf(dbObject.get("professorName")));
        lecture.setCredit(String.valueOf(dbObject.get("credit")));
        lecture.setGrade(String.valueOf(dbObject.get("grade")));
        lecture.setType(String.valueOf(dbObject.get("type")));
        lecture.setTimeClassroom(String.valueOf(dbObject.get("timeClassroom")));
        lecture.setSemester(String.valueOf(dbObject.get("semester")));

        return lecture;
    }
}
