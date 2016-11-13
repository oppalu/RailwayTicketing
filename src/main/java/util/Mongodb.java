package util;

import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by phoebegl on 2016/11/8.
 */
public class Mongodb {

    private static MongoDatabase database;

    public static MongoDatabase connect() {
        try {
            MongoClient client = new MongoClient("localhost",27017);
            database = client.getDatabase("homework3");
            //System.out.println("Connect to database successfully");
            return database;
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    public static void initCarriage() {
        int location = 1;
        int snum = 1,ynum = 1,ernum = 1;
        MongoCollection<Document> collection = database.getCollection("carriage");
        ArrayList<String> temp = Helper.getTrainNames();

        List<Document> documents = new ArrayList<Document>();

        for(int i = 0;i<temp.size();i++) {
            int s = (int) Math.round(Math.random()*10+90);
            int y = (int)Math.round(Math.random()*10+60) ;
            int e = (int) Math.round(Math.random()*10+40);
            String name = "";
            for(int j=0;j<2;j++) {
                name = "S"+String.valueOf(snum);
                Document document = new Document("id", name).
                        append("train", temp.get(i)).
                        append("location", location).
                        append("type", "商务座").
                        append("row", 5).
                        append("column", 2).
                        append("price", s);
                documents.add(document);
                location++;
                snum++;
            }
            for(int j=0;j<2;j++) {
                name = "Y"+String.valueOf(ynum);
                Document document = new Document("id", name).
                        append("train", temp.get(i)).
                        append("location", location).
                        append("type", "一等座").
                        append("row", 15).
                        append("column", 4).
                        append("price", y);
                documents.add(document);
                location++;
                snum++;
            }
            for(int j=0;j<4;j++) {
                name = "E"+String.valueOf(ernum);
                Document document = new Document("id", name).
                        append("train", temp.get(i)).
                        append("location", location).
                        append("type", "二等座").
                        append("row", 20).
                        append("column", 5).
                        append("price", e);
                documents.add(document);
                location++;
                snum++;
            }
            location = 1;
        }
        collection.insertMany(documents);

    }

    public static void initRemain() {
        MongoCollection<Document> collection = database.getCollection("remain");
        ArrayList<String> temp = Helper.read();
        List<Document> documents = new ArrayList<Document>();

        for(int i=0;i<temp.size();i++) {
            String[] help = temp.get(i).split(" ");
            String train = help[0];
            String[] routes = help[1].replace("\n","").split("-");
            for(int j=0;j<routes.length;j++) {
                Document document = new Document("train", train).
                        append("station", routes[j]).
                        append("time", "2016-11-11").
                        append("remainnum", 20).
                        append("type", "商务座");
                documents.add(document);

                document = new Document("train", train).
                        append("station", routes[j]).
                        append("time", "2016-11-11").
                        append("remainnum", 120).
                        append("type", "一等座");
                documents.add(document);

                document = new Document("train", train).
                        append("station", routes[j]).
                        append("time", "2016-11-11").
                        append("remainnum", 400).
                        append("type", "二等座");
                documents.add(document);

                document = new Document("train", train).
                        append("station", routes[j]).
                        append("time", "2016-11-11").
                        append("remainnum", 60).
                        append("type", "无座");
                documents.add(document);
            }

        }

        collection.insertMany(documents);
    }

    public static void main(String args[]) {
        Mongodb.connect();
        Mongodb.initRemain();
    }
}
