import com.mongodb.*;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import util.Helper;
import util.Mongodb;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by phoebegl on 2016/11/11.
 */
public class MongodbStart {

    private MongoDatabase database;
    private SimpleDateFormat format;

    public MongodbStart() {
        database = Mongodb.connect();
        format = new SimpleDateFormat("yyyy-MM-dd");
    }

    public void selectTicket(String start, String end,String type, java.util.Date date) {
        MongoCollection<Document> collection = database.getCollection("route");
        //模糊查询
        Pattern p = Pattern.compile("^.*"+start+".*"+end+".*$",Pattern.CASE_INSENSITIVE);
        BasicDBObject dbo = new BasicDBObject("way",p);
        FindIterable<Document> iter = collection.find(dbo);
        MongoCursor<Document> cursor = iter.iterator();
        long begin = System.currentTimeMillis();
        while (cursor.hasNext()) {
            String trainname = (String)cursor.next().get("train");
            collection = database.getCollection("remain");
            dbo = new BasicDBObject("train",trainname);
            dbo.put("station",start);
            dbo.put("time",format.format(date));
            dbo.put("type",type);
            iter = collection.find(dbo);
            MongoCursor<Document> cursor2 = iter.iterator();

            while (cursor2.hasNext()) {
                System.out.print("车号:"+trainname+"  ");
                System.out.print("起点:"+start+"  ");
                System.out.print("终点:"+end+"  ");
                System.out.print("日期:"+format.format(date)+"  ");
                System.out.print("余票量:"+cursor2.next().get("remainnum")+"  ");
                System.out.println("席别:"+type+"  ");
            }
        }
        long finish = System.currentTimeMillis();
        System.out.println("查询所用时间:"+(finish-begin)/1000.0+"s");
    }

    public void orderTicket(String username,String trainname,String start, String end,String type, java.util.Date date) {
        long begin = System.currentTimeMillis();

        MongoCollection<Document> collection = database.getCollection("carriage");
        BasicDBObject dbo = new BasicDBObject("train",trainname).append("type",type);
        FindIterable<Document> iter = collection.find(dbo);
        MongoCursor<Document> cursor = iter.iterator();
        Document o = cursor.next();
        int rowNum = (int)Math.round(Math.random()*(o.getInteger("row")-1)+1);
        int colNum = (int)Math.round(Math.random()*(o.getInteger("column")-1)+1);
        boolean temp = isConflict(o.getString("id"),rowNum,colNum,start,end,type,date);
        //如果冲突就重新找个座位
        while(temp) {
            rowNum = (int)Math.round(Math.random()*(o.getInteger("row")-1)+1);
            colNum = (int)Math.round(Math.random()*(o.getInteger("column")-1)+1);
            temp = isConflict(o.getString("id"),rowNum,colNum,start,end,type,date);
        }

        collection = database.getCollection("occupy");
        Document doc = new Document();
        doc.put("carriageid",o.getString("id"));
        doc.put("row",rowNum);
        doc.put("column",colNum);
        doc.put("start",start);
        doc.put("end",end);
        doc.put("time",format.format(date));
        collection.insertOne(doc);

        removeTicket(trainname,start,type,format.format(date));

        collection = database.getCollection("carriage");
        dbo = new BasicDBObject("id",o.getString("id"));
        iter = collection.find(dbo);
        cursor = iter.iterator();
        Document o1 = cursor.next();


        List<String> stations = Helper.getArray(trainname);
        int start_index = stations.indexOf(start);
        int end_index = stations.indexOf(end);
        int price = o1.getInteger("price")*(end_index-start_index);

        System.out.println("车票信息:");
        System.out.println("用户名:"+username+" 车次:"+trainname+" 车厢号:"+o1.get("location")+" "+ rowNum +"排"+
                colNum+"座 席别:"+type+" 起点:"+start+" 终点:"+end+" 价格:"+price+" 乘车时间:"+format.format(date));

        long finish = System.currentTimeMillis();
        System.out.println("购票所用时间:"+(finish-begin)/1000.0+"s");
    }

    private boolean isConflict(String carriage,int row,int column,String start, String end,String type, java.util.Date date) {
        MongoCollection<Document> collection = database.getCollection("carriage");
        BasicDBObject dbo = new BasicDBObject("id",carriage);
        FindIterable<Document> iter = collection.find(dbo);
        MongoCursor<Document> cursor = iter.iterator();
        while (cursor.hasNext()) {
            String name = (String)cursor.next().get("train");
            collection = database.getCollection("route");
            dbo = new BasicDBObject("train",name);
            iter = collection.find(dbo);
            MongoCursor<Document> cursor1 = iter.iterator();
            while (cursor1.hasNext()) {
                List<String> stations = Arrays.asList(cursor1.next().toString().split("-"));
                int start_index = stations.indexOf(start);
                int end_index = stations.indexOf(end);

                collection = database.getCollection("occupy");
                dbo = new BasicDBObject("carriageid",carriage).append("row",row).append("column",column).append("time",format.format(date));
                iter = collection.find(dbo);
                MongoCursor<Document> cursor2 = iter.iterator();
                while(cursor2.hasNext()) {
                    int s = stations.indexOf(cursor2.next().get("start"));
                    int e = stations.indexOf(cursor2.next().get("end"));
                    //如果存在想做的起点在存储的终点之后或者想做的终点在存储的起点前则可以考虑
                    if(!(start_index > e || end_index < s)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * 从remain中删除涉及车站的余票数
     * @param trainname
     * @param start
     * @param type
     * @param date
     */
    public void removeTicket(String trainname, String start, String type, String date) {
        MongoCollection<Document> collection = database.getCollection("remain");
        BasicDBObject dbo = new BasicDBObject("train",trainname).append("station",start).append("time",date).append("type",type);
        FindIterable<Document> iter = collection.find(dbo);
        MongoCursor<Document> cursor = iter.iterator();
        while (cursor.hasNext()) {
            int remain = cursor.next().getInteger("remainnum");
            int current = remain-1;
            collection.updateOne(Filters.eq("remainnum",remain),new Document("$set",new Document("remainnum",current)));
        }
    }

}
