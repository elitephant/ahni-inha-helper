package kr.ac.inha.itlab.daegikim;

import kr.ac.inha.itlab.daegikim.services.*;

import java.io.IOException;

public class Main {
    private static final String YEAR = "2013";
    private static final String SEMESTER = "fall";
    private static final String LOCATION = "backup/2013-fall";

    public static void main(String[] args) throws IOException {
/*
        //인하대 수강신청 사이트 파서
        InhaSugangSiteParser inhaSugangSiteParser = new InhaSugangSiteParser(YEAR, SEMESTER, LOCATION);
        inhaSugangSiteParser.run();

        //계절학기 파서
        InhaSugangSiteParser_S inhaSugangSiteParser_s = new InhaSugangSiteParser_S(YEAR, SEMESTER, LOCATION);
        inhaSugangSiteParser_s.run();

        //lecture_simple 넣는 작업
        InsertUniqueLectures insertUniqueLectures = new InsertUniqueLectures();
        insertUniqueLectures.run();

        //전공 목록 넣는 작업
        InsertMajorList insertMajorList= new InsertMajorList();
        insertMajorList.run();

        //랜덤 유저 생성
        InsertRandomUserDetail insertRandomUserDetail = new InsertRandomUserDetail();
        insertRandomUserDetail.run();

        //랜덤 강의평 생성
        InsertRandomEvaluation insertRandomEvaluation = new InsertRandomEvaluation();
        insertRandomEvaluation.run();

        long start = System.currentTimeMillis();
        InhaPortalAuthenticator inhaPortalAuthenticator = new InhaPortalAuthenticator();
        inhaPortalAuthenticator.run();
        long end = System.currentTimeMillis();
        System.out.println(end - start);
*/
    }
}
