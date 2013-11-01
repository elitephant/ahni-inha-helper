package kr.ac.inha.itlab.daegikim.services;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.WriteConcern;
import kr.ac.inha.itlab.daegikim.helpers.MongoDBHelper;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class InsertRandomUserDetail implements Runnable {
    private String genders[];
    private String majors[];
    private String years[];
    private DBCollection major_coll;
    private DBCollection user_detail_coll;

    public InsertRandomUserDetail() {
        this.user_detail_coll = MongoDBHelper.getMongoDBCollection("user_detail");
        this.major_coll = MongoDBHelper.getMongoDBCollection("major");
        this.genders = new String[] {"남자","여자"};

        List<String> majorList = new ArrayList();
        for(DBObject obj : this.major_coll.find()) {
            majorList.add(String.valueOf(obj.get("major")));
        }
        this.majors = majorList.toArray(new String[majorList.size()]);

        List<String> yearList = new ArrayList();
        for(int i=2000; i<=2013; i++) {
            yearList.add(String.valueOf(i));
        }
        this.years = yearList.toArray(new String[yearList.size()]);
    }

    private String getRandomValue(String[] values) {
        Random rand = new Random();
        int  n = rand.nextInt(values.length)+1;
        return values[n-1];
    }

    public void run() {
        int count = 20000;

        for(int i=0; i<count; i++) {
            DBObject doc = new BasicDBObject("user", new ObjectId())
                    .append("validatedTime", new Date())
                    .append("isValidated", true)
                    .append("gender", getRandomValue(genders))
                    .append("major", getRandomValue(majors))
                    .append("entrance", getRandomValue(years));

            user_detail_coll.insert(doc, WriteConcern.NORMAL);
        }
    }
}