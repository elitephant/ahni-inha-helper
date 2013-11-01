package kr.ac.inha.itlab.daegikim.services;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.WriteConcern;
import de.svenjacobs.loremipsum.LoremIpsum;
import kr.ac.inha.itlab.daegikim.helpers.MongoDBHelper;
import org.apache.log4j.Logger;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class InsertRandomEvaluation implements Runnable {
    private static final Logger logger = Logger.getLogger(InsertRandomEvaluation.class.getName());
    private static final LoremIpsum loremIpsum = new LoremIpsum();

    private String users[];
    private String lectures[];
    private String ratings[];

    private DBCollection lecture_simple_coll;
    private DBCollection user_detail_coll;

    public InsertRandomEvaluation() {
        this.lecture_simple_coll = MongoDBHelper.getMongoDBCollection("lecture_simple");
        this.user_detail_coll = MongoDBHelper.getMongoDBCollection("user_detail");

        List<String> lectureList = new ArrayList();
        for(DBObject obj : this.lecture_simple_coll.find()) {
            lectureList.add(String.valueOf(obj.get("_id")));
        }
        this.lectures = lectureList.toArray(new String[lectureList.size()]);

        List<String> userList = new ArrayList();
        for(DBObject obj : this.user_detail_coll.find()) {
            userList.add(String.valueOf(obj.get("user")));
        }
        this.users = userList.toArray(new String[userList.size()]);

        this.ratings = new String[] {"1","2","3","4","5"};
    }

    private String getRandomValue(String[] values) {
        Random rand = new Random();
        int  n = rand.nextInt(values.length)+1;
        return values[n-1];
    }

    @Override
    public void run() {
        Random rand = new Random();

        for(int i=0; i<users.length*10; i++) {
            BasicDBObject findQuery = new BasicDBObject("_id", new ObjectId(getRandomValue(lectures)));

            DBObject pushObject = new BasicDBObject("evaluations",
                new BasicDBObject("_id", new ObjectId())
                    .append("user",new ObjectId(getRandomValue(users)))
                    .append("year", "2000")
                    .append("comment", loremIpsum.getWords(rand.nextInt(50)+1))
                    .append("rating", Integer.parseInt(getRandomValue(ratings)))
                    .append("semester", 1)
                    .append("dateTime", System.currentTimeMillis())
            );

            BasicDBObject pushQuery = new BasicDBObject("$push", pushObject);
            lecture_simple_coll.setWriteConcern(WriteConcern.NORMAL);
            lecture_simple_coll.update(findQuery, pushQuery);

            logger.debug(String.format("PROCESSING: %s", String.valueOf((float)i/(float)users.length*10)));
        }
    }
}
