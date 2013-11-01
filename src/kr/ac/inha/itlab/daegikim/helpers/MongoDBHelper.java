package kr.ac.inha.itlab.daegikim.helpers;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Properties;

public class MongoDBHelper {
//    private final static String MODE = "prod";
    private final static String MODE = "dev";


    /**
     * 파라미터로 받은 collection 이름에 해당하는 몽고 컬렉션 객체를 반환함.
     * @param collection
     * @return
     */
    public static DBCollection getMongoDBCollection(String collection) {
        Properties prop = new Properties();
        MongoClient mongoClient = null;
        try {
            prop.load(new FileInputStream("config.properties"));
            String propStr = "";

            if(MODE=="dev") {
                propStr = "ahni-dev";
            }
            else if(MODE=="prod") {
                propStr = "ahni-prod";
            }

            mongoClient = new MongoClient(
                    prop.getProperty(String.format("%s-mongodb-host", propStr)),
                    Integer.parseInt(prop.getProperty(String.format("%s-mongodb-port", propStr)))
            );

            DB db = mongoClient.getDB(prop.getProperty(String.format("%s-mongodb-name", propStr)));

            db.authenticate(
                    prop.getProperty(String.format("%s-mongodb-user", propStr)),
                    prop.getProperty(String.format("%s-mongodb-pass", propStr)).toCharArray());

            DBCollection coll = db.getCollection(collection);

            return coll;

        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        return null;
    }
}
