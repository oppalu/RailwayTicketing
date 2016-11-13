package util;

import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by phoebegl on 2016/11/9.
 */
public class Helper {

    private static Connection con;
    private static ArrayList<String> routeTemp;

    public static List<String> getArray (String trainname) {
        con = JDBC.connect("homework3","root","2a617");
        String sql = "select way from route where trainname =?;";
        PreparedStatement ps;
        ResultSet set;
        try {
            ps = con.prepareStatement(sql);
            ps.setString(1,trainname);
            set = ps.executeQuery();
            List<String> stations = new ArrayList<String>();
            while (set.next()) {
                stations = Arrays.asList(set.getString(1).split("-"));
            }
            return stations;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 转成json存储
     * 然后用:mongoimport -d homework3 -c route /Users/phoebegl/ticket/route.json --jsonArray;
     */
    public static void txt2Json() {
        read();
        File file = new File("route.json");
        BufferedWriter writer;
        try {
            writer = new BufferedWriter(new FileWriter(file));
            writer.write("[");
            writer.newLine();

            for(int i = 0;i<routeTemp.size();i++) {
                String[] temp = routeTemp.get(i).split(" ");
                writer.write("{");

                writer.write("\"train\":\"");
                writer.write(temp[0]);
                writer.write("\",\"way\":\"");
                writer.write(temp[1]);
                writer.write("\"");

                writer.write("}");
                writer.newLine();
                if(i != routeTemp.size()-1)
                    writer.write(",");
            }
            writer.write("]");
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static ArrayList<String> read() {
        routeTemp = new ArrayList<String>();
        File file = new File("routes.txt");
        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(file));
            String temp;
            while ((temp = reader.readLine()) != null) {
                routeTemp.add(temp);
            }
            reader.close();
            return routeTemp;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static ArrayList<String> getTrainNames() {
        Helper.read();
        ArrayList<String> result = new ArrayList<String>();
        for(int i = 0;i<routeTemp.size();i++) {
            String[] temp = routeTemp.get(i).split(" ");
            result.add(temp[0]);
        }
        return result;
    }

    public static void main(String[] args) {
        Helper.getTrainNames();
    }

}
