
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
public class WaitlistQueries {
  private static Connection connection;
  private static ArrayList<String> WaitlistByDate;
  private static PreparedStatement getWaitlistByDate;
  private static PreparedStatement getWaitlistByFaculty;
  private static PreparedStatement addWaitlistEntry;
  private static PreparedStatement deleteWaitlistEntry;
  private static ResultSet resultSet;
  private static ResultSet resultSet2;
  
  
  //returns an arraylist of all faculty members in waiting list 
  // arranges by date first, than by timestamp. 
  public static ArrayList<WaitlistEntry> getWaitlistByDate(){
      connection = DBConnection.getConnection();
      ArrayList<WaitlistEntry> WaitlistByDate = new ArrayList<WaitlistEntry>();
      
      try
      {
          getWaitlistByDate = connection.prepareStatement("select FACULTY, DATE, SEATS from WAITLIST order by DATE, TIMESTAMP ");
          resultSet = getWaitlistByDate.executeQuery();
          
          while(resultSet.next())
          {
              WaitlistEntry waitlistEntry;
              waitlistEntry = new WaitlistEntry(resultSet.getString(1), resultSet.getDate(2), resultSet.getInt(3));
              WaitlistByDate.add(waitlistEntry);
          }
              
       }
      catch(SQLException sqlException)
      {
          sqlException.printStackTrace();
      }
      return WaitlistByDate;
  }
  
  public static ArrayList<WaitlistEntry> getWaitlistByFaculty(String faculty){
      connection = DBConnection.getConnection();
      ArrayList<WaitlistEntry> WaitlistByFaculty = new ArrayList<WaitlistEntry>();
      try
      {
          getWaitlistByFaculty = connection.prepareStatement("select FACULTY, DATE, SEATS from WAITLIST where FACULTY=? order by DATE, TIMESTAMP");
          getWaitlistByFaculty.setString(1, faculty);
          resultSet2 = getWaitlistByFaculty.executeQuery();
          
          while(resultSet2.next())
          {
              WaitlistEntry waitlistEntry;
              waitlistEntry = new WaitlistEntry(resultSet2.getString(1), resultSet2.getDate(2), resultSet2.getInt(3));
              WaitlistByFaculty.add(waitlistEntry);
          }          
      }
      catch(SQLException sqlException)
      {
          sqlException.printStackTrace();
      }
      return WaitlistByFaculty;      
  }
  
  
  //takes a waitlistEntry object and adds it to waitlist database 
  public static void addWaitlistEntry(WaitlistEntry waitlistEntry){
      connection = DBConnection.getConnection();
      try
      {
        addWaitlistEntry = connection.prepareStatement("insert into WAITLIST (FACULTY, DATE, SEATS, TIMESTAMP) values (?,?,?,?)");
        addWaitlistEntry.setString(1, waitlistEntry.getFaculty());
        addWaitlistEntry.setDate(2, waitlistEntry.getDate());
        addWaitlistEntry.setInt(3, waitlistEntry.getSeats());
        addWaitlistEntry.setTimestamp(4, waitlistEntry.getTimestamp());
        addWaitlistEntry.executeUpdate();
      }
      catch(SQLException sqlException)
              {
                  sqlException.printStackTrace();
              }
  }
  
  public static void deleteWaitlistEntry(String faculty, Date date){
      connection = DBConnection.getConnection();
      try
      {
          deleteWaitlistEntry = connection.prepareStatement("DELETE FROM WAITLIST where FACULTY=? AND DATE=?");
          deleteWaitlistEntry.setString(1, faculty);
          deleteWaitlistEntry.setDate(2, date);
          deleteWaitlistEntry.executeUpdate();
      }
      catch(SQLException sqlException)
              {
                  sqlException.printStackTrace();
              }
  }
}
      

