package util;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;

/**
 * Created by phoebegl on 2016/11/8.
 */
public class JDBC {
    private static String driver = "com.mysql.jdbc.Driver";
    private static Connection con;
    private static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

    public static Connection connect(String db,String user,String password) {

        String url = "jdbc:mysql://127.0.0.1:3306/"+db+"?useUnicode=true&characterEncoding=utf8";
        try {
            Class.forName(driver);
            con = DriverManager.getConnection(url,user,password);
            return con;
        } catch (ClassNotFoundException e) {
            System.out.println("can't find the driver!");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("fail to connect to the database!");
            e.printStackTrace();
        }
        return null;

    }

    public static void initCarriage() {
        String sql = "SELECT trainname FROM route WHERE size = 8";
        String sql2 = "INSERT INTO carriage VALUES (?,?,?,?,?,?,?);";
        int location = 1;
        int snum = 1,ynum = 1,ernum = 1;

        PreparedStatement ps = null;
        ResultSet set = null;
        try {
            //8车厢的
            ps = con.prepareStatement(sql);
            set = ps.executeQuery();
            while (set.next()) {
                int s = (int) Math.round(Math.random()*10+90);
                int y = (int)Math.round(Math.random()*10+60) ;
                int e = (int) Math.round(Math.random()*10+40);
                String name = "";
                for(int i =0;i<2;i++) {
                    ps = con.prepareStatement(sql2);
                    name = "S"+String.valueOf(snum);
                    ps.setString(1,name);
                    ps.setString(2,set.getString(1));
                    ps.setInt(3,location);
                    ps.setString(4,"商务座");
                    ps.setInt(5,5);
                    ps.setInt(6,2);
                    ps.setInt(7,s);
                    ps.executeUpdate();
                    location++;
                    snum++;
                }
                for(int i =0;i<2;i++) {
                    ps = con.prepareStatement(sql2);
                    name = "Y"+String.valueOf(ynum);
                    ps.setString(1,name);
                    ps.setString(2,set.getString(1));
                    ps.setInt(3,location);
                    ps.setString(4,"一等座");
                    ps.setInt(5,15);
                    ps.setInt(6,4);
                    ps.setInt(7,y);
                    ps.executeUpdate();
                    location++;
                    ynum++;
                }
                for(int i =0;i<4;i++) {
                    ps = con.prepareStatement(sql2);
                    name = "E"+String.valueOf(ernum);
                    ps.setString(1,name);
                    ps.setString(2,set.getString(1));
                    ps.setInt(3,location);
                    ps.setString(4,"二等座");
                    ps.setInt(5,20);
                    ps.setInt(6,5);
                    ps.setInt(7,e);
                    ps.executeUpdate();
                    location++;
                    ernum++;
                }
                location = 1;
            }

            //16车厢的
            sql = "SELECT trainname FROM route WHERE size = 16";
            ps = con.prepareStatement(sql);
            set = ps.executeQuery();
            while (set.next()) {
                int s = (int) Math.round(Math.random()*10+90);
                int y = (int)Math.round(Math.random()*10+60) ;
                int e = (int) Math.round(Math.random()*10+40);
                String name = "";
                for(int i =0;i<2;i++) {
                    ps = con.prepareStatement(sql2);
                    name = "S"+String.valueOf(snum);
                    ps.setString(1,name);
                    ps.setString(2,set.getString(1));
                    ps.setInt(3,location);
                    ps.setString(4,"商务座");
                    ps.setInt(5,5);
                    ps.setInt(6,2);
                    ps.setInt(7,s);
                    ps.executeUpdate();
                    location++;
                    snum++;
                }
                for(int i =0;i<4;i++) {
                    ps = con.prepareStatement(sql2);
                    name = "Y"+String.valueOf(ynum);
                    ps.setString(1,name);
                    ps.setString(2,set.getString(1));
                    ps.setInt(3,location);
                    ps.setString(4,"一等座");
                    ps.setInt(5,15);
                    ps.setInt(6,4);
                    ps.setInt(7,y);
                    ps.executeUpdate();
                    location++;
                    ynum++;
                }
                for(int i =0;i<10;i++) {
                    ps = con.prepareStatement(sql2);
                    name = "E"+String.valueOf(ernum);
                    ps.setString(1,name);
                    ps.setString(2,set.getString(1));
                    ps.setInt(3,location);
                    ps.setString(4,"二等座");
                    ps.setInt(5,20);
                    ps.setInt(6,5);
                    ps.setInt(7,e);
                    ps.executeUpdate();
                    location++;
                    ernum++;
                }
                location = 1;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void initRemain() {
        String sql = "select * from route";
        PreparedStatement ps;
        ResultSet set;
        try {
            ps = con.prepareStatement(sql);
            set = ps.executeQuery();
            while (set.next()) {
                String temp = set.getString(2).replace("\n","");
                String[] stations = temp.split("-");
                String name = set.getString(1);
                if(set.getInt(3)==8) {
                    for(int i = 0;i<stations.length;i++) {
                        for(int j = 0;j<7;j++) {
                            Calendar c = Calendar.getInstance();
                            c.setTime(c.getTime());
                            c.add(Calendar.DATE,j);
                            java.sql.Date d = new java.sql.Date(c.getTime().getTime());

                            String sql2 = "INSERT INTO remain(trainname,station,time,remainnum,type) VALUES ('"+name+"','"+stations[i]+"','"+d+"',20,'商务座');";
                            ps = con.prepareStatement(sql2);
                            ps.executeUpdate();

                            sql2 = "INSERT INTO remain(trainname,station,time,remainnum,type) VALUES ('"+name+"','"+stations[i]+"','"+d+"',120,'一等座');";
                            ps = con.prepareStatement(sql2);
                            ps.executeUpdate();

                            sql2 = "INSERT INTO remain(trainname,station,time,remainnum,type) VALUES ('"+name+"','"+stations[i]+"','"+d+"',400,'二等座');";
                            ps = con.prepareStatement(sql2);
                            ps.executeUpdate();

                            sql2 = "INSERT INTO remain(trainname,station,time,remainnum,type) VALUES ('"+name+"','"+stations[i]+"','"+d+"',60,'无座');";
                            ps = con.prepareStatement(sql2);
                            ps.executeUpdate();
                        }
                    }
                } else {
                    for(int i = 0;i<stations.length;i++) {
                        for(int j = 0;j<7;j++) {
                            Calendar c = Calendar.getInstance();
                            c.setTime(c.getTime());
                            c.add(Calendar.DATE,j);
                            java.sql.Date d = new java.sql.Date(c.getTime().getTime());

                            String sql2 = "INSERT INTO remain(trainname,station,time,remainnum,type) VALUES ('"+name+"','"+stations[i]+"','"+d+"',20,'商务座');";
                            ps = con.prepareStatement(sql2);
                            ps.executeUpdate();

                            sql2 = "INSERT INTO remain(trainname,station,time,remainnum,type) VALUES ('"+name+"','"+stations[i]+"','"+d+"',240,'一等座');";
                            ps = con.prepareStatement(sql2);
                            ps.executeUpdate();

                            sql2 = "INSERT INTO remain(trainname,station,time,remainnum,type) VALUES ('"+name+"','"+stations[i]+"','"+d+"',1000,'二等座');";
                            ps = con.prepareStatement(sql2);
                            ps.executeUpdate();

                            sql2 = "INSERT INTO remain(trainname,station,time,remainnum,type) VALUES ('"+name+"','"+stations[i]+"','"+d+"',140,'无座');";
                            ps = con.prepareStatement(sql2);
                            ps.executeUpdate();
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void close() {
        try {
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        con = JDBC.connect("homework3","root","2a617");
        JDBC.initRemain();
    }

}
