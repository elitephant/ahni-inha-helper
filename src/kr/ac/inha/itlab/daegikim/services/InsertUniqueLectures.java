package kr.ac.inha.itlab.daegikim.services;

import com.mongodb.*;
import kr.ac.inha.itlab.daegikim.helpers.MongoDBHelper;
import kr.ac.inha.itlab.daegikim.models.Lecture;

import kr.ac.inha.itlab.daegikim.models.LectureSimple;
import org.apache.log4j.Logger;

import java.util.HashMap;

public class InsertUniqueLectures implements Runnable {
    private static final Logger logger = Logger.getLogger(InsertUniqueLectures.class.getName());
    private HashMap<String, LectureSimple> uniqueHashMap;

    @Override
    public void run() {
        DBCollection toFindColl = MongoDBHelper.getMongoDBCollection("lecture");
        DBCollection toInsertColl = MongoDBHelper.getMongoDBCollection("lecture_simple");

        uniqueHashMap = new HashMap<String, LectureSimple>();
        for(DBObject object : toFindColl.find()) {
            Lecture lecture = Lecture.mongoObjectToLecture(object);

            LectureSimple lectureSimple = new LectureSimple(lecture.getLectureName().trim(), lecture.getProfessorName().trim());
            uniqueHashMap.put(lectureSimple.getLectureName()+"::"+lectureSimple.getProfessorName(), lectureSimple);
        }
        logger.debug(uniqueHashMap.size());

        //유니크 인덱스를 설정한다. 중복방지
        toInsertColl.ensureIndex(new BasicDBObject("lectureName", 1).append("professorName", 1), new BasicDBObject("unique", true));
        for(LectureSimple lectureSimple : uniqueHashMap.values()) {
            toInsertColl.insert(LectureSimple.lectureUniqueToMongoObject(lectureSimple), WriteConcern.NORMAL);
        }
    }
}
