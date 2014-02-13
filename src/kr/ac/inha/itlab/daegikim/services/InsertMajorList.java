package kr.ac.inha.itlab.daegikim.services;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.WriteConcern;
import kr.ac.inha.itlab.daegikim.helpers.MongoDBHelper;
import kr.ac.inha.itlab.daegikim.models.Lecture;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class InsertMajorList implements Runnable {
    @Override
    public void run() {
        HashMap<String, String> depts = new HashMap();

        HtmlCleaner cleaner = new HtmlCleaner();
        TagNode node = null;
        try {
            node = cleaner.clean(new File("data/index.html"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        TagNode select = node.getElementsByName("select", true)[0];
        for(TagNode option : select.getElementsByName("option", true)){
            depts.put(option.getAttributes().get("value"), option.getText().toString().split("/")[1].trim());
        }

        DBCollection coll = MongoDBHelper.getMongoDBCollection("major");

        coll.ensureIndex(new BasicDBObject("major", 1), new BasicDBObject("unique", true));

        for(String major : depts.values()) {
            BasicDBObject basicDBObject = new BasicDBObject("major", major);
            coll.insert(basicDBObject, WriteConcern.NORMAL);
        }

        coll.getDB().getMongo().close();
    }
}
