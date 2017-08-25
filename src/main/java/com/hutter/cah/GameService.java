/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hutter.cah;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.Consumes;
//import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
//import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


/**
 *
 * @author scott
 */
@Path("/v1/")
public class GameService {

        private static final ArrayList<Room> ROOMS = new ArrayList<Room>();
    
        // Constructor       
        public GameService()
        {

        }
        
        // Helper methods
        private int getRoomIndexByCode(String code)
        {          
            for(int x=0; x<GameService.ROOMS.size(); x++)
            {
                if (GameService.ROOMS.get(x).getRoomCode().equals(code))
                    return x;
            }
            
            return -1;
        }
        
        // Web service methods
        @POST
	@Path("/com")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response com(String data) {

            JSONParser  parser = new JSONParser();
            Message response = new Message();
            
            try {
                // Get the json data and parse it into useable objects
                JSONObject json = (JSONObject) parser.parse(data);
                
                // Convert the json data to a message object
                Message request = new Message();
                request.setRoomCode((String)json.get("roomCode"));
                request.setName((String)json.get("name"));
                request.setType((String)json.get("type"));
                request.setText((String)json.get("text"));
                request.setCards((ArrayList<Card>)json.get("cards"));
                
                if(request.getName().equals("server"))
                    response = HandleServerRequest(request);
                else
                    response = HandleClientRequest(request);

            } 
            catch (ParseException ex) 
            {
                Logger.getLogger(GameService.class.getName()).log(Level.SEVERE, null, ex);
                
                // Send an error response
                response.setRoomCode("");
                response.setName("");
                response.setType("Parse Exception");
                response.setText(ex.getMessage());
                response.setCards(null);  
            }
            
            return Response.status(201).entity(response).build();
	}
 
        private Message HandleServerRequest(Message request)
        { 
            Message response = new Message();
            String type = request.getType();
            
            if(type.equals("Start New Game"))
            {
                int roomIndex = getRoomIndexByCode(request.getRoomCode());
                GameService.ROOMS.get(roomIndex).restartGame();
            }
            
            if(type.equals("Get Message"))
            {
                int roomIndex = getRoomIndexByCode(request.getRoomCode());
  
                if(roomIndex >= 0)
                {   
                    // Remove any dropped players
                    GameService.ROOMS.get(roomIndex).removeDroppedPlayers();
                    
                    // Check for any pending messages
                    if (GameService.ROOMS.get(roomIndex).isNotificationPending() == true)
                    {
                       response = GameService.ROOMS.get(roomIndex).getNotification();
                    }
                    else
                    {
                       // Send a no message response
                       response.setRoomCode(request.getRoomCode());
                       response.setName(request.getName());
                       response.setType(type);
                       response.setText("No message");
                       response.setCards(null);                       
                    }
                }
                else
                {
                    response.setRoomCode("");
                    response.setName(request.getName());
                    response.setType(type);
                    response.setText("Invalid room code.");
                    response.setCards(null); 
                }
            }
    
            if (request.getType().equals("Create Room"))
            {
                Room room = new Room();
                GameService.ROOMS.add(room);
                
                response.setRoomCode(room.getRoomCode());
                response.setName(request.getName());
                response.setType(type);
                response.setText(room.getRoomCode());
                response.setCards(null); 
            }

            return response;
        }
        
        private Message HandleClientRequest(Message request)
        {
            Message response = new Message();
            String type = request.getType();
            
            if(type.equals("Join Room"))
            {
                int roomIndex = getRoomIndexByCode(request.getText());
                
                if(roomIndex >= 0)
                {
                    if(GameService.ROOMS.get(roomIndex).getLock() == true)
                    {
                        response.setRoomCode("");
                        response.setName(request.getName());
                        response.setType(type);
                        response.setText("This room is currently locked.");
                        response.setCards(null);                       
                    }
                    else
                    {
                        if(GameService.ROOMS.get(roomIndex).getPlayer(request.getName()) == null)
                        {
                            // Create a player object and add it to the room
                            Player p = new Player();
                            p.setName(request.getName());
                            GameService.ROOMS.get(roomIndex).addPlayer(p);
                            GameService.ROOMS.get(roomIndex).getPlayer(request.getName()).setLastPing(LocalDateTime.now());  
                           
                            // Notify the room that a player has joined.
                            Message m = new Message();
                            m.setRoomCode(request.getRoomCode());
                            m.setName("server");
                            m.setType("Player Joined");
                            m.setText(request.getName());
                            GameService.ROOMS.get(roomIndex).pushNotification(m);
                        }

                        // Create response object to confirm joining the room
                        response.setRoomCode(request.getText());
                        response.setName(request.getName());
                        response.setType(type);
                        response.setText("You have joined the room.");
                        response.setCards(null); 
                    }
                }
                else
                {
                    response.setRoomCode("");
                    response.setName(request.getName());
                    response.setType(type);
                    response.setText("Invalid room code.");
                    response.setCards(null); 
                }
                
            }
           
            if(type.equals("All Players In"))
            {
                int roomIndex = getRoomIndexByCode(request.getRoomCode());
                
                //GameService.ROOMS.get(roomIndex).setLock(true);
                GameService.ROOMS.get(roomIndex).playGame();                  

            }
            
            if(type.equals("Get Message"))
            {
                int roomIndex = getRoomIndexByCode(request.getRoomCode());
  
                if(roomIndex >= 0)
                {   
                    // Update the player's last ping time so we know he is still active
                    GameService.ROOMS.get(roomIndex).getPlayer(request.getName()).setLastPing(LocalDateTime.now());

                    // Check for any pending messages
                    if (GameService.ROOMS.get(roomIndex).getPlayer(request.getName()).isNotificationPending() == true)
                    {
                       response = GameService.ROOMS.get(roomIndex).getPlayer(request.getName()).getNotification();
                    }
                    else
                    {
                        // Send a no message response
                       response.setRoomCode(request.getRoomCode());
                       response.setName(request.getName());
                       response.setType(type);
                       response.setText("");
                       response.setCards(null);                       
                    }
                }
                else
                {
                    response.setRoomCode("");
                    response.setName(request.getName());
                    response.setType(type);
                    response.setText("Invalid room code.");
                    response.setCards(null); 
                }
            }
            
            if(type.equals("Cards Selected"))
            {
                int roomIndex = getRoomIndexByCode(request.getRoomCode());
 
                // Update the player's last ping time so we know he is still active
                GameService.ROOMS.get(roomIndex).getPlayer(request.getName()).setLastPing(LocalDateTime.now());
                GameService.ROOMS.get(roomIndex).giveJudgeAnswerCard(request); 

            }
            
            if(type.equals("Winning Cards Selected"))
            {
                int roomIndex = getRoomIndexByCode(request.getRoomCode());
 
                // Update the player's last ping time so we know he is still active
                GameService.ROOMS.get(roomIndex).getPlayer(request.getName()).setLastPing(LocalDateTime.now());
                GameService.ROOMS.get(roomIndex).judgeSelectsWinningCard(request); 

            }
            
            if (type.equals("Start New Round"))
            {
                int roomIndex = getRoomIndexByCode(request.getRoomCode());
                GameService.ROOMS.get(roomIndex).playGame();  
            }
            
            return response;
        }
        
}
