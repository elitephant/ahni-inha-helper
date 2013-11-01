package kr.ac.inha.itlab.daegikim.models;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

public class LectureSimple {
    private String lectureName;
    private String professorName;

    public LectureSimple() {

    }

    public LectureSimple(String lectureName, String professorName) {
        this.lectureName = lectureName;
        this.professorName = professorName;
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

    public static DBObject lectureUniqueToMongoObject(LectureSimple lectureSimple) {
        BasicDBObject basicDBObject = new BasicDBObject("lectureName", lectureSimple.getLectureName())
                .append("professorName", lectureSimple.getProfessorName());
        return basicDBObject;
    }
}
