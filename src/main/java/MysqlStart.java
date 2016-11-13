import com.mysql.jdbc.*;
import util.Helper;
import util.JDBC;

import java.sql.*;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by phoebegl on 2016/11/9.
 */
public class MysqlStart {
    private Connection con;

    public MysqlStart() {
        con = JDBC.connect("homework3","root","2a617");
    }

    public void selectTicket(String start, String end,String type, java.util.Date date) {
        String sql = "SELECT trainname FROM route WHERE way LIKE '%"+start+"%"+end+"%';";
        try {
            long begin = System.currentTimeMillis();
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet set = ps.executeQuery();
            while (set.next()) {
                java.sql.Date d = new java.sql.Date(date.getTime());
                String sql2 = "SELECT * FROM remain WHERE trainname = ? AND station = ? AND time = ? AND TYPE = ? FOR UPDATE ;";
                ps = con.prepareStatement(sql2);
                ps.setString(1,set.getString(1));
                ps.setString(2,start);
                ps.setDate(3,d);
                ps.setString(4,type);
                ResultSet result = ps.executeQuery();
                while (result.next()) {
                    System.out.print("车号:"+result.getString(1)+"  ");
                    System.out.print("起点:"+result.getString(2)+"  ");
                    System.out.print("终点:"+end+"  ");
                    System.out.print("日期:"+result.getString(3)+"  ");
                    System.out.print("余票量:"+result.getInt(4)+"  ");
                    System.out.println("席别:"+result.getString(5)+"  ");
                }
            }
            long finish = System.currentTimeMillis();
            System.out.println("查询所用时间:"+(finish-begin)/1000.0+"s");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 订票的过程,涉及occupy&remain表的修改
     * @param username
     * @param trainname
     * @param start
     * @param end
     * @param type
     * @param date
     */
    public void orderTicket(String username,String trainname,String start, String end,String type, java.util.Date date) {
        //先随机选择一个车厢的一个座位,然后判断是否冲突
        long begin1 = System.currentTimeMillis();
        String sql = "select id,`row`,`column` FROM carriage WHERE trainname = ? AND type = ? ORDER BY RAND() limit 1;";
        PreparedStatement ps;
        ResultSet set;
        try {
            ps = con.prepareStatement(sql);
            ps.setString(1,trainname);
            ps.setString(2,type);
            set = ps.executeQuery();

            while(set.next()) {
                int rowNum = (int)Math.round(Math.random()*(set.getInt(2)-1)+1);
                int colNum = (int)Math.round(Math.random()*(set.getInt(3)-1)+1);
                boolean temp = isConflict(set.getString(1),rowNum,colNum,start,end,type,date);
                //如果冲突就重新找个座位
                while(temp) {
                    rowNum = (int)Math.round(Math.random()*(set.getInt(2)-1)+1);
                    colNum = (int)Math.round(Math.random()*(set.getInt(3)-1)+1);
                    temp = isConflict(set.getString(1),rowNum,colNum,start,end,type,date);
                }

                sql = "INSERT INTO occupy(carriageid,row,`column`,start,end,time) VALUES (?,?,?,?,?,?);";
                ps = con.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);
                ps.setString(1,set.getString(1));
                ps.setInt(2,rowNum);
                ps.setInt(3,colNum);
                ps.setString(4,start);
                ps.setString(5,end);
                ps.setDate(6,new java.sql.Date(date.getTime()));

                ps.executeUpdate();
                int autoNum = 0;
                ResultSet result = ps.getGeneratedKeys();
                if(result.next())
                    autoNum = result.getInt(1);
                //减少对应的remain数量
                removeTicket(trainname,start,end,type,date);
                long finish1 = System.currentTimeMillis();
                System.out.println("购票所用时间:"+(finish1-begin1)/1000.0+"s");

                printTicket(username,trainname,autoNum);
            }


        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private boolean isConflict(String carriage,int row,int column,String start, String end,String type, java.util.Date date) {
        String sql = "select way from route r,carriage c where r.trainname = c.trainname and id = ? and type = ?;";
        PreparedStatement ps;
        ResultSet set;
        try {
            ps = con.prepareStatement(sql);
            ps.setString(1,carriage);
            ps.setString(2,type);
            set = ps.executeQuery();
            while(set.next()) {
                List<String> stations = Arrays.asList(set.getString(1).split("-"));
                int start_index = stations.indexOf(start);
                int end_index = stations.indexOf(end);

                sql = "select start,end from occupy where carriageid = ? and row = ? and `column` = ? and time=?;";
                ps = con.prepareStatement(sql);
                ps.setString(1,carriage);
                ps.setInt(2,row);
                ps.setInt(3,row);
                ps.setDate(4,new java.sql.Date(date.getTime()));
                set = ps.executeQuery();

                while(set.next()) {
                    int s = stations.indexOf(set.getString(1));
                    int e = stations.indexOf(set.getString(2));
                    //如果存在想做的起点在存储的终点之后或者想做的终点在存储的起点前则可以考虑
                    if(!(start_index > e || end_index < s)) {
                        return true;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 从remain中删除涉及车站的余票数
     * 使用乐观锁
     * @param trainname
     * @param start
     * @param end
     * @param type
     * @param date
     */
    public void removeTicket(String trainname, String start, String end, String type, java.util.Date date) {
        String sql = "SELECT version FROM remain WHERE trainname=? and station=? and time=? and type=?;";
        PreparedStatement ps;
        ResultSet set;
        try {
            List<String> stations = Helper.getArray(trainname);
            int start_index = stations.indexOf(start);
            int end_index = stations.indexOf(end);

            for(int i = start_index;i<end_index;i++) {
                ps = con.prepareStatement(sql);
                ps.setString(1,trainname);
                ps.setString(2,stations.get(i));
                ps.setDate(3,new java.sql.Date(date.getTime()));
                ps.setString(4,type);
                set = ps.executeQuery();

                while (set.next()) {
                    String sql2 = "UPDATE `remain` SET remainnum=remainnum-1,version=version+1 WHERE trainname=? and station=? and time=? and type=? and `version`=?;";
                    ps = con.prepareStatement(sql2);
                    ps.setString(1,trainname);
                    ps.setString(2,stations.get(i));
                    ps.setDate(3,new java.sql.Date(date.getTime()));
                    ps.setString(4,type);
                    ps.setInt(5,set.getInt(1));
                    ps.executeUpdate();
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 打印车票即添加订单(向order中加数据)
     */
    public void printTicket(String username,String trainname,int occupy) {
        long begin2 = System.currentTimeMillis();
        String sql = "SELECT * FROM carriage c,occupy o WHERE o.carriageid=c.id and o.id = "+occupy+";";
        PreparedStatement ps;
        ResultSet set;
        try {
            ps = con.prepareStatement(sql);
            set = ps.executeQuery();
            while(set.next()) {
                int location = set.getInt(3);
                String type = set.getString(4);
                int singleprice = set.getInt(7);
                int row = set.getInt(10);
                int column = set.getInt(11);
                String start = set.getString(12);
                String end = set.getString(13);
                String time = set.getString(14);

                List<String> stations = Helper.getArray(trainname);
                int start_index = stations.indexOf(start);
                int end_index = stations.indexOf(end);
                int price = singleprice*(end_index-start_index);

                System.out.println("车票信息:");
                System.out.println("用户名:"+username+" 车次:"+trainname+" 车厢号:"+location+" "+ row +"排"+
                        column+"座 席别:"+type+" 起点:"+start+" 终点:"+end+" 价格:"+price+" 乘车时间:"+time);

                sql = "INSERT INTO `order`(username,trainname,occupyid) VALUES (?,?,?);";
                ps = con.prepareStatement(sql);
                ps.setString(1,username);
                ps.setString(2,trainname);
                ps.setInt(3,occupy);
                ps.executeUpdate();
            }
            long finish2 = System.currentTimeMillis();
            System.out.println("打印车票所用时间:"+(finish2-begin2)/1000.0+"s");
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

}
