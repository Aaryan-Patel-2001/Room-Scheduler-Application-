
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
public class RoomQueries {
    private static Connection connection;
    private static Connection connection2;
    private static Connection connection3;
    private static ArrayList<RoomEntry> roomList = new ArrayList<RoomEntry>();
    private static ArrayList<RoomEntry> occupiedRooms = new ArrayList<RoomEntry>();
    private static PreparedStatement getAllPossibleRooms;
    private static PreparedStatement addRoom;
    private static PreparedStatement updateWaitlist;
    private static PreparedStatement seatFinder;
    private static PreparedStatement addReservation;
    private static PreparedStatement deleteWaitlist;
    private static PreparedStatement listRes;
    private static PreparedStatement delRes;
    private static PreparedStatement dropRoom;
    private static PreparedStatement updateRes;
    private static ResultSet resultSet;
    private static ResultSet resultSet2;
    private static ResultSet resultSet3;
    private static ResultSet resultSet4;
    private static ArrayList<RoomEntry> allRooms;
    
    // returns an ArrayList of all RoomEntry objects of all rooms in ROOM database
    public static ArrayList<RoomEntry> getAllPossibleRooms(int seats){
        connection = DBConnection.getConnection();
        ArrayList<RoomEntry> roomList = new ArrayList<RoomEntry>();
        try
        {
            getAllPossibleRooms = connection.prepareStatement("select NAME, SIZE from ROOM where SIZE>=? order by SIZE ");
            getAllPossibleRooms.setInt(1, seats);
            resultSet = getAllPossibleRooms.executeQuery();
            
            while(resultSet.next())
            {   
                RoomEntry room;
                room = new RoomEntry(resultSet.getString(1), resultSet.getInt(2));
                roomList.add(room);
            }
        }
        catch(SQLException sqlException)
        {
            sqlException.printStackTrace();
        }
        return roomList;
    }
    
    // uses above methods to get all room that are in database by passing seats 
    // as zero. 
    public static ArrayList<String> getAllRooms(){

        ArrayList<RoomEntry> x = RoomQueries.getAllPossibleRooms(0);
        ArrayList<String> rooms = new ArrayList<String>();
        for(RoomEntry r:x){
            rooms.add(r.getRoomName());
        }
        return rooms;
    }
    
    public static int SeatsFinder(String roomName){
        connection = DBConnection.getConnection();
        int r = 0;
        try
        {
            seatFinder = connection.prepareStatement("select SIZE from ROOM where NAME=? ");
            seatFinder.setString(1, roomName);
            resultSet4 = seatFinder.executeQuery();
            while(resultSet4.next()){
                r = resultSet4.getInt(1);
                break;
            }
        }
        catch(SQLException sqlException)
        {
            sqlException.printStackTrace();
        }       
        return r;
    }
    
    
    public static ArrayList<String> addRoom(RoomEntry roomEntry){
        connection2 = DBConnection.getConnection();
        ArrayList<String> listProf = new ArrayList<>();
        try
        {
            addRoom = connection2.prepareStatement("insert into ROOM (NAME, SIZE) values (?,?) ");
            addRoom.setString(1, roomEntry.getRoomName());
            addRoom.setInt(2, roomEntry.getSeats());
            addRoom.executeUpdate();
            
            updateWaitlist = connection2.prepareStatement("select FACULTY, DATE, SEATS from WAITLIST where SEATS<=? order by DATE, TIMESTAMP ");
            updateWaitlist.setInt(1, roomEntry.getSeats());
            resultSet2 = updateWaitlist.executeQuery();
            Date date = null;
            while(resultSet2.next()){
                if((date==null) || (date!=null & date!=resultSet2.getDate(2))){
                ReservationEntry res = new ReservationEntry(resultSet2.getString(1), roomEntry.getRoomName(),resultSet2.getDate(2), roomEntry.getSeats());
                addReservation = connection2.prepareStatement("insert into RESERVATION (FACULTY, ROOM, DATE, SEATS, TIMESTAMP) values (?,?,?,?,?)");
                addReservation.setString(1, res.getFaculty());
                addReservation.setString(2, res.getRoom());
                addReservation.setDate(3, res.getDate());
                addReservation.setInt(4,res.getSeats());
                addReservation.setTimestamp(5, res.getTimestamp());
                String S = String.format("%s reserves room %s for %s", res.getFaculty(), res.getRoom(), res.getDate());
                listProf.add(S);
                addReservation.executeUpdate();
                
                deleteWaitlist = connection2.prepareStatement("delete from WAITLIST where FACULTY=? and DATE=? and SEATS=?");
                deleteWaitlist.setString(1, resultSet2.getString(1));
                deleteWaitlist.setDate(2, resultSet2.getDate(2));
                deleteWaitlist.setInt(3, resultSet2.getInt(3));
                deleteWaitlist.executeUpdate();
                
                date = resultSet2.getDate(2);
                }
            }
        }
        catch(SQLException sqlException)
        {
            sqlException.printStackTrace();
        }
        return listProf;
    }
    
    public static ArrayList<String> dropRoom(String roomName, int seats){
        connection3 = DBConnection.getConnection();
        ArrayList<String> dropProf = new ArrayList<String>();
        try
        {
            //delets the given room from ROOM database. 
            dropRoom = connection3.prepareStatement("DELETE FROM ROOM WHERE NAME=? AND SIZE=?");
            dropRoom.setString(1, roomName);
            dropRoom.setInt(2, seats);
            dropRoom.executeUpdate();
            
            // get list of all reservations of that room
            listRes = connection3.prepareStatement("select FACULTY, DATE, SEATS, TIMESTAMP from RESERVATION where ROOM=? order by DATE, TIMESTAMP ");
            listRes.setString(1, roomName);
            resultSet3 = listRes.executeQuery();
            // for all of those reservations, 
            while(resultSet3.next()){
                // delete them from RESERVATION database,
                delRes = connection3.prepareStatement("DELETE FROM RESERVATION WHERE FACULTY=? AND DATE=?");
                delRes.setString(1, resultSet3.getString(1));
                delRes.setDate(2, resultSet3.getDate(2));
                delRes.executeUpdate();
                
                // look for new room and if not add them to waitlist 
                // method addReservationEntry looks for new room, if there exits
                // such room then it assings it and return true. returns false 
                // otherwise 
                boolean x = ReservationQueries.addReservationEntry(resultSet3.getString(1), resultSet3.getDate(2), resultSet3.getInt(3));
                
                if(x){
                    // changes timestamp from current to the one in original reservation
                    updateRes = connection3.prepareStatement("UPDATE RESERVATION SET TIMESTAMP=? WHERE FACULTY=? AND DATE=?");
                    updateRes.setTimestamp(1, resultSet3.getTimestamp(4));
                    updateRes.setString(2, resultSet3.getString(1));
                    updateRes.setDate(3, resultSet3.getDate(2));
                    updateRes.executeUpdate();
                    String s = String.format("%s reserved room %s on %s", resultSet3.getString(1), ReservationQueries.roomFinder(resultSet3.getString(1), resultSet3.getDate(2)), resultSet3.getDate(2));
                    dropProf.add(s);
                }
                else{
                    // if a suitable room was not found then it is added to waitlist 
                    updateRes = connection3.prepareStatement("INSERT INTO WAITLIST (FACULTY, DATE, SEATS, TIMESTAMP) values (?,?,?,?)");
                    updateRes.setString(1, resultSet3.getString(1));
                    updateRes.setDate(2, resultSet3.getDate(2));
                    updateRes.setInt(3, resultSet3.getInt(3));
                    updateRes.setTimestamp(4, resultSet3.getTimestamp(4));
                    updateRes.executeUpdate();
                    String w = String.format("%s was moved to the waitlist for %s", resultSet3.getString(1), resultSet3.getDate(2));
                    dropProf.add(w);
                }
            }
        }
        catch(SQLException sqlException)
            {
                sqlException.printStackTrace();
            }
        return dropProf;
    }
    
}
