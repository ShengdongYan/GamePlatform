package Database;

import org.json.JSONObject;

import java.io.*;
import java.sql.*;

/**
 * Created by bxs863 on 27/02/19.
 */
public class Database {
    private String url = null;
    private String username = null;
    private String password = null;
    private static Database database = null;

    private Database(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }

    /**
     * Singleton for Database
     * @return
     */
    public static Database getInstance() {
        if (database == null) {
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(new File("setting.json"))));
                StringBuilder content = new StringBuilder();
                String line = "";
                while ((line = reader.readLine()) != null) {
                    content.append(line);
                }
                JSONObject setting = new JSONObject(content.toString());
                String username = setting.getString("sql_usr");
                String password = setting.getString("sql_password");
                String url = setting.getString("sql_url");
                database = new Database(url, username, password);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return database;
    }

    /**
     * Check if the table exists
     * @param table
     * @return
     */
    public boolean checkExist(String table){
        try(Connection connection = DriverManager.getConnection(url,username,password)){
            DatabaseMetaData dbm = connection.getMetaData();
            ResultSet tables = dbm.getTables(null,null,table,null);
            if(tables.next()) return true;
            else return false;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Check if the value exists
     * @param table
     * @param key
     * @param value
     * @return
     */
    public boolean checkExist(String table,String key, String value){
        try(Connection connection = DriverManager.getConnection(url,username,password);
                Statement statement = connection.createStatement()){
            if(checkExist(table)){
                String sql =String.format("select %s from %s",key,table);
                ResultSet rs =statement.executeQuery(sql);
                while(rs.next()){
                    if(rs.getString(key).equals(value)) return true;
                }
            }
            else return false;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean userMatch(String table,String usr,String pw){
        try(Connection conn = DriverManager.getConnection(url,username,password);Statement stmt= conn.createStatement()){
            String sql = String.format("SELECT %s,%s FROM %s","username","password",table);
            ResultSet rs =stmt.executeQuery(sql);
            while(rs.next()){
                if(usr.equals(rs.getString("username"))
                        &&pw.equals(rs.getString("password"))){
                    return true;
                }
            }
            return false;
        }
        catch(SQLException e){
            System.out.println("Table doesn't exist or user doesn't exist");
        }
        return false;
    }

    public void addUserInfo(String table,String username,String password){
        if(checkExist("user_info")) {
            try (Connection connection = DriverManager.getConnection(url, this.username, this.password); Statement statement = connection.createStatement()) {
                String sql = String.format("INSERT INTO %s (username,password) VALUES ('%s','%s');", table, username, password);
                statement.executeUpdate(sql);

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        else{
            System.out.println("Table doesn't exist.");
        }
    }


    /**
     * This method is only used to do some operation
     * @param sql
     * @param value
     */
    public void execute(String sql,Object... value){
        String s = String.format(sql,value);
        try(Connection conn = DriverManager.getConnection(url,username,password);Statement stmt= conn.createStatement()){
            ResultSet rs =stmt.executeQuery(sql);
            }
        catch(SQLException e){
            System.out.println("Cannot connect to Database");
        }
    }
}
