/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hutter.cah;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author scott
 */
public final class Room {
    
    private String roomCode;
    private final int max = 8;
    private final ArrayList<Player> players = new ArrayList<>();
    private ArrayList<Card> whiteDeck = new ArrayList<>();
    private ArrayList<Card> blackDeck = new ArrayList<>();
    private final Queue<Message> messageQueue = new LinkedList<>();
    private boolean locked = false;
    private int judgeIndex = -1;
    private Card pickedBlackCard;
    
    // Constructor
    public Room()
    {
        String whiteDeckString = getDeckFromFile("whiteDeck.json");
        this.setWhiteDeck(getCardsFromJSONString(whiteDeckString));

        String blackDeckString = getDeckFromFile("blackDeck.json");
        this.setBlackDeck(getCardsFromJSONString(blackDeckString));

        this.setRoomCode(generateRoomCode());
    }
    
    
    // Getters / Setters
    public String getRoomCode()
    {
        return roomCode;
    }
    
    public void setRoomCode(String roomCode)
    {
        this.roomCode = roomCode;
    }
    
    public void setWhiteDeck(ArrayList<Card> whiteDeck)
    {
        this.whiteDeck = whiteDeck;
    }
    
    public ArrayList<Card> getWhiteDeck()
    {
        return whiteDeck;
    } 
    
    public ArrayList<Card> getBlackDeck()
    {
        return blackDeck;
    }
    
    public void setBlackDeck(ArrayList<Card> blackDeck)
    {
        this.blackDeck = blackDeck;
    }
    
    public void pushNotification(Message message)
    {
        this.messageQueue.add(message);
    }
    
    public Message getNotification()
    {
        if (this.messageQueue.size() > 0)
            return this.messageQueue.remove();
        else
            return null;
    }
    
    public boolean isNotificationPending()
    {
        return this.messageQueue.size() > 0;
    }
    
    public void setLock(boolean locked)
    {
        this.locked = locked;
    }
    
    public boolean getLock()
    {
        return this.locked;
    }
    

    // Methods
    
    private String generateRoomCode()
    {
        Random r = new Random();
        String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String code = "";
        for (int i = 0; i < 4; i++) {
            code += alphabet.charAt(r.nextInt(alphabet.length()));
        }

        return code;
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
                c.setPick((String)jsonCard.get("pick"));
                c.setText((String)jsonCard.get("text"));

                cards.add(c);
            }

        } catch (ParseException ex) {
            Logger.getLogger(GameService.class.getName()).log(Level.SEVERE, null, ex);
        }

        return cards;
    }

    public boolean addPlayer(Player p)
    {
        if (players.size() == max)
            return false;
        else
        {
            players.add(p);
            return true;
        } 
    }
    
    public Player getPlayer(String name)
    {
        for(int z=0; z<players.size(); z++)
        {
            if(players.get(z).getName().equals(name))
                return players.get(z);
        }
        
        return null;
    }
    
    public String[] getPlayerNames()
    {
        int x = players.size();
        
        String[] names = new String[x];
        
        for(int z=0; z<x; z++)
        {
            names[z] = players.get(z).getName();
        }
        
        return names;
    }
    
    public void addCardToDeck(Card c)
    {
        whiteDeck.add(c);
    }

    public void addToPlayerScore(String name)
    {
        for(int x=0; x<players.size(); x++)
        {
            if (this.players.get(x).getName().equals(name))
                this.players.get(x).setScore(this.players.get(x).getScore() + 1);
        }
    }
    
    public Player getPlayerByName(String name)
    {
        Player p = null;
        
        for(int x=0; x<players.size(); x++)
        {
            if (this.players.get(x).getName().equals(name))
                p = this.players.get(x);
        }
        
        return p;
    }
    
    public void playGame()
    {
        // Reset all devices
        resetDevices();
        
        // Select a judge
        selectJudge();
        
         // Draw a black card and display it on screen & to judge
        drawBlackCard(); 
        
        // Deal cards and notify players
        dealCards();
    }
    
    private void resetDevices()
    {
        // Tell devices to clean up for next round
        for(int playerIndex=0; playerIndex< this.players.size(); playerIndex++)
        {
            Message m = new Message();
            m.setRoomCode(roomCode);
            m.setType("Reset Device");
            m.setName(this.players.get(playerIndex).getName());
            m.setCards(null);
            this.players.get(playerIndex).pushNotification(m);
         }
        
        // Server too
        Message m = new Message();
        m.setRoomCode(roomCode);
        m.setType("Reset Device");
        m.setName("server");
        m.setCards(null);
        this.pushNotification(m);
    }
    
    private void selectJudge()
    {
        boolean judgeSelected = false;
        for (int x=0;x<this.players.size()-1; x++)
            this.players.get(x).setIsJudge(false);     

        // Todo:  its possible that all players could be away.  if that happens, 
        // this will go into an infinite loop
        
        while(judgeSelected == false)
        {
            judgeIndex++;
            if (judgeIndex > this.players.size()-1)
                judgeIndex = 0; 
            
            if (this.players.get(judgeIndex).getAway() == false)
            {
                this.players.get(judgeIndex).setIsJudge(true);
                judgeSelected = true;
            }       
        }
       
        Message m = new Message();
        m.setRoomCode(roomCode);
        m.setType("You Are Judge");
        m.setName(this.players.get(judgeIndex).getName());
        m.setText("");
        m.setCards(null);
        this.players.get(judgeIndex).pushNotification(m);
        
        m = new Message();
        m.setRoomCode(roomCode);
        m.setType("Judge Selected");
        m.setName("server");
        m.setText(this.players.get(judgeIndex).getName());
        m.setCards(null);
        this.pushNotification(m);
    }
    
    private void drawBlackCard()
    {
        Random r = new Random();
        
        //do {
        int blackCardIndex = r.nextInt(blackDeck.size());
        pickedBlackCard = blackDeck.get(blackCardIndex);
        blackDeck.remove(blackCardIndex);
        //} while(pickedBlackCard.getPick().equals("1"));
        
        // add card to format needed for message
        ArrayList<Card> cards = new ArrayList<>();
        cards.add(pickedBlackCard);
        
        Message m = new Message();
        m.setRoomCode(roomCode);
        m.setType("Picked Black Card");
        m.setName("server");
        m.setText("");
        m.setCards(cards);
        this.pushNotification(m);
        
        // Send the card to the players
        for (int x=0;x<this.players.size(); x++)
        {
            m = new Message();
            m.setRoomCode(roomCode);
            m.setType("Picked Black Card");
            m.setName(this.players.get(x).getName());
            m.setText("");
            m.setCards(cards);
            this.players.get(x).pushNotification(m);
        }

    }   
    
    private void dealCards()
    {
        Random r = new Random();
        
        // Give each player 7 cards
        // Loop through each player
        for(int playerIndex=0; playerIndex< this.players.size(); playerIndex++)
        {
            // Disable the All Players In button
            Message m = new Message();
            m.setRoomCode(roomCode);
            m.setType("All Players In");
            m.setName(this.players.get(playerIndex).getName());
            m.setCards(null);
            this.players.get(playerIndex).pushNotification(m);
            
            // Only draw show hands to players
            if (playerIndex != judgeIndex)
            {
                // Draw 7 cards for the player
               for (int counter=this.players.get(playerIndex).getCardCount(); counter<7; counter++)
               {
                  int randomDeckCardIndex = r.nextInt(whiteDeck.size());

                  // take a random card from the deck
                  Card drawnCard = whiteDeck.get(randomDeckCardIndex);
                  whiteDeck.remove(drawnCard);

                  // give it to the player
                  this.players.get(playerIndex).AddCard(drawnCard);   
               }

               m = new Message();
               m.setRoomCode(roomCode);
               m.setType("Cards Dealt");
               m.setName(this.players.get(playerIndex).getName());
               m.setText(pickedBlackCard.getPick());
               m.setCards(this.players.get(playerIndex).getCards());
               this.players.get(playerIndex).pushNotification(m);               
            }
         }
    }

    public void giveJudgeAnswerCard(Message request)
    {       
        Message m = new Message();
        m.setRoomCode(roomCode);
        m.setType("Give Card To Judge");
        m.setName(this.players.get(judgeIndex).getName());
        m.setText(request.getName());
        m.setCards(request.getCards());
        this.players.get(judgeIndex).pushNotification(m);
        
        // Also send this card to the room for display to all players
        m = new Message();
        m.setRoomCode(roomCode);
        m.setType("Give Card To Judge");
        m.setName("server");
        m.setText(request.getName());
        m.setCards(request.getCards());
        this.pushNotification(m);
        
        // remove the chosen cards from player's hand
        Player p = getPlayerByName(request.getName());
        for(int z=0; z< p.getCards().size(); z++)
        {
            Card cardInHand = p.getCards().get(z);
            
            for(int v=0; v< request.getCards().size(); v++)
            {
                JSONArray turnedInCards = (JSONArray)request.getCards();
                JSONObject turnedInCard = (JSONObject) turnedInCards.get(v);
                String turnedInCardId = (String)turnedInCard.get("id");
                
                if (cardInHand.id.equals(turnedInCardId))
                    p.getCards().remove(z);
            }           
        }
        
        
    }
    
    public void judgeSelectsWinningCard(Message request)
    {
        String playerName = request.getText();
        
        // Notify player
        Message m = new Message();
        m.setRoomCode(roomCode);
        m.setType("Notify Winner");
        m.setName(playerName);
        m.setText("");
        getPlayerByName(playerName).pushNotification(m);
        
        // Notify server of winner
        m = new Message();
        m.setRoomCode(roomCode);
        m.setType("Notify Winner");
        m.setName("server");
        m.setText(playerName);
        m.setCards(request.getCards());
        this.pushNotification(m);
        
        // Add to player score
        addToPlayerScore(playerName);
        String score = Integer.toString(getPlayerByName(playerName).getScore());
        
        // Notify server to update score
        m = new Message();
        m.setRoomCode(roomCode);
        m.setType("Update Score");
        m.setName("server");
        m.setText(playerName + "," + score);
        m.setCards(null);
        this.pushNotification(m);
    }
    
    public void restartGame()
    {
        // Tell server to restart
        Message m = new Message();
        m.setRoomCode(this.roomCode);
        m.setName("server");
        m.setType("Start New Game");
        m.setText("");
        m.setCards(null); 
        this.pushNotification(m);
        
        for(int x=0; x< this.players.size(); x++)
        {
            m.setRoomCode(this.roomCode);
            m.setName(this.players.get(x).getName());
            m.setType("Start New Game");
            m.setText("");
            m.setCards(null); 
            this.players.get(x).pushNotification(m);
        }
                
    }
    
    public void setPlayerStatus()
    {
        LocalDateTime currentTime = LocalDateTime.now();
        
        for(int x=0; x< this.players.size(); x++)
        {
            LocalDateTime playerPingTime = this.players.get(x).getLastPing();
            
            long timeDif = ChronoUnit.SECONDS.between(playerPingTime,currentTime);
            
            if(timeDif > 5)
            {
                Message m = new Message();
                m.setRoomCode(roomCode);
                m.setType("Dropped Player");
                m.setName("server");
                m.setText(this.players.get(x).getName());
                m.setCards(null);
                this.pushNotification(m);
                
                this.players.get(x).setAway(true);
            }
        }
    }
}
