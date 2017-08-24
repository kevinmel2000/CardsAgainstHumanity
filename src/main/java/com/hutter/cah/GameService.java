/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hutter.cah;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Random;
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
import java.io.*;
import org.json.simple.JSONArray;
//import org.json.simple.JSONValue;


/**
 *
 * @author scott
 */
@Path("/v1/")
public class GameService {

        private static final ArrayList<Room> ROOMS = new ArrayList<Room>();
    
        //
        // Constructor 
        //
        
        public GameService()
        {
            if(GameService.ROOMS.isEmpty())
            {
                String whiteDeckString = getDeckFromFile("whiteDeck.json");
                ArrayList<Card> whiteDeck = getCardsFromJSONString(whiteDeckString);
                
                String blackDeckString = getDeckFromFile("blackDeck.json");
                ArrayList<Card> blackDeck = getCardsFromJSONString(blackDeckString);
                
                String roomCode = generateRoomCode();

                Room room = new Room();
                room.setRoomCode(roomCode);
                room.setWhiteDeck(whiteDeck);
                room.setBlackDeck(blackDeck);
                GameService.ROOMS.add(room);                
            }
        }
        
        //
        // Helper methods
        //
                
        private String generateRoomCode()
        {
            Random r = new Random();
            String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
            String roomCode = "";
            for (int i = 0; i < 4; i++) {
                roomCode += alphabet.charAt(r.nextInt(alphabet.length()));
            }

            return roomCode;
        }
        
        private int getRoomIndexByCode(String code)
        {          
            for(int x=0; x<GameService.ROOMS.size(); x++)
            {
                if (GameService.ROOMS.get(x).getRoomCode().equals(code))
                    return x;
            }
            
            return -1;
        }
        
        private String getDeckFromFile(String filename)
        {
            try {
               InputStream f = this.getClass().getClassLoader().getResourceAsStream(filename);
               InputStreamReader ir = new InputStreamReader(f);
               BufferedReader br = new BufferedReader(ir); 
               String text;
               String json = "";
               
               while ((text = br.readLine()) != null) {
                   json += text;
               }
               
               return json;
            }
            catch (IOException ex)
            {
                Logger.getLogger(GameService.class.getName()).log(Level.SEVERE, null, ex);
            }
       
            return null;
        }        
    
        private ArrayList<Card> getCardsFromJSONString(String data)
        {
            ArrayList<Card> cards = new ArrayList<>();
            
            JSONParser  parser = new JSONParser();
            try {
                
                JSONObject j = (JSONObject) parser.parse(data);
                JSONArray jarray = (JSONArray)j.get("cards");
                
                for (int x = 0; x < jarray.size(); x++)
                {
                    JSONObject jsonCard = (JSONObject)jarray.get(x);
                    
                    Card c = new Card();
                    c.setId((String)jsonCard.get("id"));  
                    c.setText((String)jsonCard.get("text"));
                    
                    cards.add(c);
                }
                
            } catch (ParseException ex) {
                Logger.getLogger(GameService.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            return cards;
        }
        
        //
        // Web service methods
        //
       
        
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
            
            if(type.equals("Get Message"))
            {
                int roomIndex = getRoomIndexByCode(request.getRoomCode());
  
                if(roomIndex >= 0)
                {   
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

            if (request.getType().equals("Get Room Code"))
            {
                response.setRoomCode(GameService.ROOMS.get(0).getRoomCode());
                response.setName(request.getName());
                response.setType("Get Room Code");
                response.setText(GameService.ROOMS.get(0).getRoomCode());
                response.setCards(null);    
            }
            
            if (request.getType().equals("Start New Game"))
            {
                int roomIndex = getRoomIndexByCode(request.getRoomCode());
                GameService.ROOMS.get(roomIndex).restartGame(); 
                GameService.ROOMS.remove(roomIndex);
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
                        // Create a player object and add it to the room
                        Player p = new Player();
                        p.setName(request.getName());
                        GameService.ROOMS.get(roomIndex).addPlayer(p);
                        GameService.ROOMS.get(roomIndex).getPlayer(request.getName()).setLastPing(LocalDateTime.now());
                        
                        // Create response object to confirm joining the room
                        response.setRoomCode(request.getText());
                        response.setName(request.getName());
                        response.setType(type);
                        response.setText("You have joined the room.");
                        response.setCards(null); 
                        
                        // Notify the room that a player has joined.
                        Message m = new Message();
                        m.setRoomCode(request.getRoomCode());
                        m.setName("server");
                        m.setType("Player Joined");
                        m.setText(request.getName());
                        GameService.ROOMS.get(roomIndex).pushNotification(m);
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
                
                GameService.ROOMS.get(roomIndex).setLock(true);
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
