/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author apple
 */
public class RoomEntry {
    private  String roomName;
    private  int seats; 
    
    public RoomEntry(String room, int seat){
        roomName = room;
        seats = seat;
    }
    
    public String getRoomName(){
        return roomName;
    }
    
    public int getSeats(){
        return seats;
    }
    
    public void setRoomName(String roomname){
        roomName = roomname;
    }
    
    public void setSeats(int seat){
        seats = seat;
    }
    
    
}
