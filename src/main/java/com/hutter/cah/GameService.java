/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hutter.cah;

import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;



/**
 *
 * @author scott
 */
@Path("/status/")
public class GameService {

        private static final ArrayList<Room> rooms = new ArrayList<>();
    
        public GameService()
        {
            if(rooms.isEmpty())
            {
                String roomCode = generateRoomCode();

                Room room = new Room();
                room.setId(roomCode);
                rooms.add(room);                
            }
        }
        
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
            for(int x=0; x<rooms.size(); x++)
            {
                if (rooms.get(x).getId().equals(code))
                    return x;
            }
            
            return -1;
        }
        
                
    
    	@GET
	@Path("/getRoom")
	@Produces(MediaType.APPLICATION_JSON)
	public Room getRoom() {

            return rooms.get(0);
	}
        
        @POST
	@Path("/joinRoom")
	@Consumes(MediaType.APPLICATION_JSON)
	public String[] joinRoom(String data) {

            String result = "";
            JSONParser  parser = new JSONParser();
            try {
                
                JSONObject json = (JSONObject) parser.parse(data);
                String name = (String)json.get("name");
                String roomCode = (String)json.get("roomCode");

                int roomIndex = getRoomIndexByCode(roomCode);
                
                if(roomIndex >= 0)
                {
                    Player p = new Player();
                    p.setName(name);
                    
                    rooms.get(roomIndex).addPlayer(p);
                    String names[] = rooms.get(roomIndex).getPlayerNames();
                    
                    return names;
                }
                else
                {
                    return null;
                }
                
                
            } catch (ParseException ex) {
                Logger.getLogger(GameService.class.getName()).log(Level.SEVERE, null, ex);
            }


            
            //return Response.status(201).entity(result).build();
            return null;
	}
    
                @POST
	@Path("/getPlayersList")
	@Consumes(MediaType.APPLICATION_JSON)
	public String[] getPlayersList(String data) {

            JSONParser  parser = new JSONParser();
            try {
                
                JSONObject json = (JSONObject) parser.parse(data);
                String roomCode = (String)json.get("roomCode");

                int roomIndex = getRoomIndexByCode(roomCode);
                
                if(roomIndex >= 0)
                {                  
                    String names[] = rooms.get(roomIndex).getPlayerNames();
                    return names;
                }
                else
                {
                    return null;
                }
            } catch (ParseException ex) {
                Logger.getLogger(GameService.class.getName()).log(Level.SEVERE, null, ex);
            }
            //return Response.status(201).entity(result).build();
            return null;
	}
        
        
	@GET
	//@Path("/get")
	@Produces(MediaType.APPLICATION_JSON)
	public Card getCardInJSON() {

		Card card = new Card();
		card.setId("123");
		card.setText("___ loves everyone.");

		return card;

	}

	@POST
	//@Path("/post")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response createCardInJSON(Card card) {

		String result = "Card saved : " + card;
		return Response.status(201).entity(result).build();

	}
}
