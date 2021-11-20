
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
public class WaitlistEntry {
    private  String faculty;
    private  Date date;
    private  int seats;
    private  Timestamp timestamp;
    
    public WaitlistEntry(String sfaculty, Date sdate, int sseats){
        faculty = sfaculty;
        date = sdate;
        seats = sseats;
        timestamp = new Timestamp(Calendar.getInstance().getTime().getTime());
    }
    
    public String getFaculty(){
        return faculty;
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


