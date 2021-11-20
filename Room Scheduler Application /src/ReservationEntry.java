
import java.sql.Date;
import java.sql.Timestamp;
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
public class ReservationEntry {
    private  String faculty;
    private  String room;
    private  Date date;
    private  int seats;
    private  Timestamp timestamp; 
    
    public ReservationEntry(String sfaculty, String sroom, Date sdate, int sseats){
        faculty = sfaculty;
        room = sroom;
        date = sdate;
        seats = sseats;
        timestamp = new Timestamp(Calendar.getInstance().getTime().getTime());; 
    }
    
    public String getFaculty(){
        return faculty;
    }
    
    public String getRoom(){
        return room;
    }
    
    public Date getDate(){
        return  date;
    }
    
    public int getSeats(){
        return seats;
    }
    
    public Timestamp getTimestamp(){
        return timestamp;
    }
    
    public void setFaculty(String sfaculty){
        faculty = sfaculty;
    }
    
    public void setRoom(String sroom){
        room = sroom;
    }
    
    public void setDate(Date sdate){
        date = sdate;
    }
    
    public void setSeats(int sseats){
        seats = sseats;
    }
    
    public void setTimestamp(Timestamp stimestamp){
        timestamp = stimestamp;
    }
}
