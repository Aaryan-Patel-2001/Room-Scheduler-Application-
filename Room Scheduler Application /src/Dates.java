
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author apple
 */
public class Dates {
    
    private static Connection connection;
    private static PreparedStatement addDate;
    private static ArrayList<Date> date = new ArrayList<Date>();
    private static PreparedStatement getAllDates;
    private static ResultSet resultSet;
    // adds a date object to DATE database 
    public static void addDate(Date date){
        connection = DBConnection.getConnection();
        try
        {
          addDate = connection.prepareStatement("insert into DATE (DATE) values (?)");
          addDate.setObject(1, date);
          addDate.executeUpdate();
        }
        catch(SQLException sqlException)
                {
                    sqlException.printStackTrace();
                }
    }
    // returns an arrayList of all dates in DATE database, sorted by date
    public static ArrayList<Date> getAllDates(){
        connection = DBConnection.getConnection();
        ArrayList<Date> date = new ArrayList<Date>();
        try
        {
            getAllDates = connection.prepareStatement("select DATE from DATE order by DATE");
            resultSet = getAllDates.executeQuery();
            
            while(resultSet.next())
            {
                date.add(resultSet.getDate(1));
            }
        }
        catch(SQLException sqlException)
        {
            sqlException.printStackTrace();
        }
        return date;
    }
}
