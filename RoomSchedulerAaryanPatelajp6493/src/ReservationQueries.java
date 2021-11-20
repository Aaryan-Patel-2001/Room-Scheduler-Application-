
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author apple
 */
public class ReservationQueries {

    private static Connection connection;
    private static ArrayList<String> facultyReserved = new ArrayList<String>();
    private static PreparedStatement getReservationByDate;
    private static PreparedStatement getReservationByFaculty;
    private static PreparedStatement addReservationEntry;
    private static PreparedStatement getReservationInfo;
    private static PreparedStatement roomFinder;
    private static PreparedStatement deleteReservation;
    private static PreparedStatement findRoomSeats;
    private static PreparedStatement addFromWaitlist;
    private static PreparedStatement addRess;
    private static ResultSet resultSet;
    private static ResultSet resultSet2;
    private static ResultSet resultSet3;
    private static ResultSet resultSet4;
    private static ResultSet resultSet5;
    private static ResultSet resultSet6;
    private static Connection connection2;

// method getReservationByDate displays all faculty that has a reservation on 
// that date. it takes a date object. 
    public static ArrayList<String> getReservationsByDate(Date date) {
        connection = DBConnection.getConnection();
        ArrayList<String> facultyReserved = new ArrayList<String>();
        try {
            getReservationByDate = connection.prepareStatement("select FACULTY, ROOM from RESERVATION where date=?");
            getReservationByDate.setDate(1, date);
            resultSet = getReservationByDate.executeQuery();

            while (resultSet.next()) {
                    facultyReserved.add(resultSet.getString(1));
            }
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
        return facultyReserved;
    }
    
    //return true if a given faculty on a given date has a reservation
    public static boolean isReservation(String faculty, Date date){
        ArrayList<String> facultybydate = getReservationsByDate(date);
        return facultybydate.contains(faculty);
    }
    
    public static ArrayList<ReservationEntry> getReservationsByFaculty(String faculty){
        connection = DBConnection.getConnection();
        ArrayList<ReservationEntry> resbyFac = new ArrayList<ReservationEntry>();
        try
        {
            getReservationByFaculty = connection.prepareStatement("select FACULTY, ROOM, DATE, SEATS, TIMESTAMP from RESERVATION where FACULTY=? ");
            getReservationByFaculty.setString(1, faculty);
            resultSet4 = getReservationByFaculty.executeQuery();
            while (resultSet4.next()){
                ReservationEntry res = new ReservationEntry(resultSet4.getString(1), resultSet4.getString(2), resultSet4.getDate(3), resultSet4.getInt(4));
                resbyFac.add(res);
            }
            
        }
        catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
        return resbyFac;
    }
    // takes a reservation entry object and adds it to RESERVATION database
    public static boolean addReservationEntry(String faculty, Date date, int seatsRequested) {
        connection = DBConnection.getConnection();
        try {
            ArrayList<RoomEntry> availableRooms = new ArrayList<RoomEntry>();
            availableRooms = RoomQueries.getAllPossibleRooms(seatsRequested);
            getReservationInfo = connection.prepareStatement("select ROOM, DATE from RESERVATION where date = ? ");
            getReservationInfo.setDate(1, date);
            resultSet2 = getReservationInfo.executeQuery();
            ArrayList<String> occupiedRooms = new ArrayList<String>();
            while (resultSet2.next()) {

                occupiedRooms.add(resultSet2.getString(1));

            }
            for (RoomEntry allRooms : availableRooms) {
                if ((occupiedRooms.isEmpty()) || !(occupiedRooms.contains(allRooms.getRoomName()))) {
                    addReservationEntry = connection.prepareStatement("insert into RESERVATION (FACULTY, ROOM, DATE, SEATS, TIMESTAMP) values (?, ?, ?, ?, ?)");
                    ReservationEntry res = new ReservationEntry(faculty, allRooms.getRoomName(), date, allRooms.getSeats());
                    addReservationEntry.setString(1, res.getFaculty());
                    addReservationEntry.setString(2, res.getRoom());
                    addReservationEntry.setDate(3, res.getDate());
                    addReservationEntry.setInt(4, res.getSeats());
                    addReservationEntry.setTimestamp(5, res.getTimestamp());
                    addReservationEntry.executeUpdate();
                    return true;
                }
            }

        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
        return false;
    }
    
    //finds which room Name was assigned to professor based on faculty name and 
    // date. 
    public static String roomFinder(String facultyName, Date date){
        connection2 = DBConnection.getConnection();
        String room = null;
        try
        {
            roomFinder = connection2.prepareStatement("select ROOM from RESERVATION where FACULTY=? AND DATE=? ");
            roomFinder.setString(1, facultyName);
            roomFinder.setDate(2, date);
            resultSet3 = roomFinder.executeQuery();
            while(resultSet3.next()){
            room = resultSet3.getString(1);
            }
     
        }
        catch(SQLException sqlException){
            sqlException.printStackTrace();
        }
        return room;
    }
    
    public static ArrayList<String> deleteReservation(String faculty, Date date){
        connection = DBConnection.getConnection();
        ArrayList<String> facgotwai = new ArrayList<String>();
        RoomEntry re = null;
        String fac = null;
        Date dt = null;
        int seats = 0;
        Timestamp tsp = null;
        try
        {
            // finds the room faculty wants to cancel and stores this information
            // as roomEntry object.
            findRoomSeats = connection.prepareStatement("select ROOM, SEATS from RESERVATION where FACULTY=? AND DATE=? ");
            findRoomSeats.setString(1, faculty);
            findRoomSeats.setDate(2, date);
            resultSet5 = findRoomSeats.executeQuery();
            while(resultSet5.next()){
                re = new RoomEntry(resultSet5.getString(1), resultSet5.getInt(2));
                break;
            }
            
            // deletes the said faculty's record from RESERVATION table. 
            deleteReservation = connection.prepareStatement("DELETE FROM RESERVATION where FACULTY=? AND DATE=?");
            deleteReservation.setString(1, faculty);
            deleteReservation.setDate(2, date);
            deleteReservation.executeUpdate(); 
            
            //finds suitable candidate from waitlist to replace the said professor
            addFromWaitlist = connection.prepareStatement("select FACULTY, DATE, SEATS, TIMESTAMP from WAITLIST where seats<=? AND DATE=? ORDER BY DATE, TIMESTAMP ");
            addFromWaitlist.setInt(1, re.getSeats());
            addFromWaitlist.setDate(2, date);
            resultSet6 = addFromWaitlist.executeQuery();
            String nullchecker = null;
            while(resultSet6.next()){
                nullchecker = "not";
                fac = resultSet6.getString(1);
                dt = resultSet6.getDate(2);
                seats = resultSet6.getInt(3);
                tsp = resultSet6.getTimestamp(4);
                break;
            }
            
            //only if we find candidate who can take the spot, following is executed.
            if(nullchecker!=null){
                //adds the candidate faculty into RESERVATION table. 
                addRess = connection.prepareStatement("insert into RESERVATION (FACULTY, ROOM, DATE, SEATS, TIMESTAMP) values (?, ?, ?, ?, ?)");
                addRess.setString(1, fac);
                addRess.setString(2, re.getRoomName());
                addRess.setDate(3, dt);
                addRess.setInt(4, seats);
                addRess.setTimestamp(5, tsp);
                addRess.executeUpdate();
                String zxc = String.format("%s got of the waitlist for date %s", fac, dt);
                facgotwai.add(zxc);
                //Removes the candidate from WAITLIST
                WaitlistQueries.deleteWaitlistEntry(fac, dt);
            }
                            
        }
        catch(SQLException sqlException){
            sqlException.printStackTrace();
        }       
        return facgotwai;
    }

}
