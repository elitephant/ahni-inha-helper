package kr.ac.inha.itlab.daegikim.services;

import com.mongodb.*;
import kr.ac.inha.itlab.daegikim.helpers.MongoDBHelper;
import kr.ac.inha.itlab.daegikim.models.Lecture;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.util.*;
import java.util.concurrent.TimeUnit;

//인하대학교 정규학기 강의시간표 파서
public class InhaSugangSiteParser implements Runnable {
    private String year;
    private String semester;

    //전공고유번호:전공명
    private HashMap<String, String> depts;

    //기타고유번호:기타명
    private HashMap<String, String> kitas;

    //학수번호:강의클래스
    private HashMap<String, Lecture> lectures;

    public InhaSugangSiteParser(String year, String semester) {
        this.year = year;
        this.semester = semester;
        depts = new HashMap<String, String>();
        kitas = new HashMap<String, String>();
        lectures = new HashMap<String, Lecture>();
    }

    public void run() {
        try {
            //STEP0: 타이머 세팅
            long startTime = System.currentTimeMillis();

            //STEP1: 기본 index 페이지를 받아온다. data/index.html
            getIndexPage();

            //STEP2: 기본 index 페이지에서 [전공고유번호:전공명] 값을 추출하고 HashMap depts 에 넣는다.
            getDepts();
            getKitas();

            //STEP3: 전공고유번호를 이용해 전공별 강의목록 html 을 얻어온다. data/고유번호.html
            for(String deptNumber : depts.keySet()) {
                getHtml(deptNumber,"{ddlDept}","data/MAJOR-POSTRequestHeader.txt");
            }
            for(String deptNumber : kitas.keySet()) {
                getHtml(deptNumber,"{ddlKita}","data/KITA-POSTRequestHeader.txt");
            }

            //STEP4: 전공별 html 에서 강의목록을 추출하여 HashMap lectures 에 넣는다.
            getLectureList(depts);
            getLectureList(kitas);

            //STEP5: 데이터베이스에 insert 한다.
            putMongoDB();

            //STEP6: 결과 메시지
            printMessage(startTime);
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    private void getIndexPage() {
        try{
            URL url = new URL("http://sugang.inha.ac.kr/sugang/SU_51001/Lec_Time_Search.aspx");
            HttpURLConnection hConnection = (HttpURLConnection)url.openConnection();
            HttpURLConnection.setFollowRedirects( true );

            hConnection.setDoOutput(true);
            hConnection.setRequestMethod("GET");

            PrintStream ps = new PrintStream(hConnection.getOutputStream());

            ps.close();
            hConnection.connect();

            if( HttpURLConnection.HTTP_OK == hConnection.getResponseCode() )
            {
                InputStream is = hConnection.getInputStream();
                BufferedReader in = new BufferedReader(new InputStreamReader(is, Charset.forName("EUC-KR")));
                BufferedWriter out= new BufferedWriter( new FileWriter("data/index.html"));
                String readLine;

                while((readLine=in.readLine()) != null)
                {
                    out.write(readLine+"\n");
                    out.flush();
                }
                is.close();
                out.close();
                hConnection.disconnect();
            }
            else {
                System.out.println("Fail");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void getDepts() throws IOException {
        HtmlCleaner cleaner = new HtmlCleaner();
        TagNode node = cleaner.clean(new File("data/index.html"));

        TagNode select = node.getElementsByName("select", true)[0];
        for(TagNode option : select.getElementsByName("option", true)){
            depts.put(option.getAttributes().get("value"), option.getText().toString());
        }
    }

    private void getKitas() throws IOException {
        HtmlCleaner cleaner = new HtmlCleaner();
        TagNode node = cleaner.clean(new File("data/index.html"));

        TagNode select = node.getElementsByName("select", true)[1];
        for(TagNode option : select.getElementsByName("option", true)){
            kitas.put(option.getAttributes().get("value"), option.getText().toString());
        }
    }

    private void getHtml(String dept, String replaceKey, String requestHeaderPath) {
        try{
            URL url = new URL("http://sugang.inha.ac.kr/sugang/SU_51001/Lec_Time_Search.aspx");
            HttpURLConnection hConnection = (HttpURLConnection)url.openConnection();
            HttpURLConnection.setFollowRedirects( true );

            hConnection.setDoOutput(true);
            hConnection.setRequestMethod("POST");

            PrintStream ps = new PrintStream(hConnection.getOutputStream());

            BufferedReader br;
            String line;
            br = new BufferedReader(new InputStreamReader(new FileInputStream(requestHeaderPath)));
            while ((line = br.readLine()) != null) {
                ps.print(line.replace(replaceKey, dept));
            }

            ps.close();
            hConnection.connect();

            if( HttpURLConnection.HTTP_OK == hConnection.getResponseCode() )
            {
                InputStream is = hConnection.getInputStream();
                BufferedReader in = new BufferedReader(new InputStreamReader(is, Charset.forName("EUC-KR")));
                BufferedWriter out= new BufferedWriter( new FileWriter("data/"+dept+".html"));
                String readLine;

                while((readLine=in.readLine()) != null)
                {
                    out.write(readLine+"\n");
                    out.flush();
                }
                is.close();
                out.close();
                hConnection.disconnect();
            }
            else {
                System.out.println("Fail");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void getLectureList(HashMap<String, String> collection) throws IOException {
        HtmlCleaner cleaner = new HtmlCleaner();

        for(String dept : collection.keySet()) {
            TagNode node = cleaner.clean(new File("data/"+dept+".html"));

            //8번째 위치한 테이블이 강의목록 테이블이다
            TagNode table = node.getElementsByName("table", true)[8];

            //강의목록 테이블의 모든 row 에 대해서
            for(TagNode tr : table.getElementsByName("tr", true)){
                String professorName = tr.getElementsByName("td", true)[7].getText().toString().replace("&nbsp;", " ").trim();
                String lectureName = tr.getElementsByName("td", true)[2].getText().toString().replace("&nbsp;", " ").trim();

                if("".equals(professorName) || " ".equals(professorName) || "".equals(lectureName) || " ".equals(lectureName)) {
                    continue;
                }

                String haksoo = tr.getElementsByName("td", true)[0].getText().toString();

                Lecture lecture = new Lecture();
                lecture.setHaksoo(haksoo);
                lecture.setLectureName(lectureName);
                lecture.setProfessorName(professorName);
                lecture.setGrade(tr.getElementsByName("td", true)[3].getText().toString());
                lecture.setCredit(tr.getElementsByName("td", true)[4].getText().toString());
                lecture.setType(tr.getElementsByName("td", true)[5].getText().toString());
                lecture.setTimeClassroom(tr.getElementsByName("td", true)[6].getText().toString());
                lecture.setYear(year);
                lecture.setSemester(semester);

                lectures.put(haksoo, lecture);
            }
        }
    }

    private void putMongoDB() throws UnknownHostException {
        DBCollection coll = MongoDBHelper.getMongoDBCollection("lecture");

        for(Lecture lecture : lectures.values()) {
            BasicDBObject basicDBObject = new BasicDBObject("haksoo", lecture.getHaksoo())
                    .append("year",lecture.getYear())
                    .append("semester",lecture.getSemester())
                    .append("lectureName", lecture.getLectureName())
                    .append("grade", lecture.getGrade())
                    .append("credit",lecture.getCredit())
                    .append("type",lecture.getType())
                    .append("timeClassroom",lecture.getTimeClassroom())
                    .append("professorName",lecture.getProfessorName());

            coll.insert(basicDBObject, WriteConcern.NORMAL);
        }

        coll.getDB().getMongo().close();
    }

    private void printMessage(long startTime) {
        System.out.println("총 " + lectures.size() + "개의 강의목록을 추출하여 데이터베이스에 입력하였습니다.");
        long elapsedtime = System.currentTimeMillis() - startTime;
        String hms = String.format("%02d시간 %02d분 %02d초", TimeUnit.MILLISECONDS.toHours(elapsedtime),
                TimeUnit.MILLISECONDS.toMinutes(elapsedtime) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(elapsedtime)),
                TimeUnit.MILLISECONDS.toSeconds(elapsedtime) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(elapsedtime)));
        System.out.println("모든 작업을 완료하는데 "+ hms +"가 소요되었습니다.");
    }
}