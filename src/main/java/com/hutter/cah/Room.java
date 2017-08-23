/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hutter.cah;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

/**
 *
 * @author scott
 */
public class Room {
    
    private String roomCode;
    private final int max = 8;
    private final ArrayList<Player> players = new ArrayList<>();
    private ArrayList<Card> whiteDeck = new ArrayList<>();
    private ArrayList<Card> blackDeck = new ArrayList<>();
    private final Queue<Message> messageQueue = new LinkedList<>();
    private boolean locked = false;
    private int judgeIndex = -1;
    private Card pickedBlackCard;
    
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
    
    public Room()
    {

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
        
        // Deal cards and notify players
        dealCards();
        
        // Draw a black card and display it on screen & to judge
        drawBlackCard();
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
        judgeIndex++;
        if (judgeIndex > this.players.size()-1)
            judgeIndex = 0;
        
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
               for (int counter=0; counter<7; counter++)
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
               m.setCards(this.players.get(playerIndex).getCards());
               this.players.get(playerIndex).pushNotification(m);               
            }
         }
    }
    
    private void drawBlackCard()
    {
        Random r = new Random();
        int blackCardIndex = r.nextInt(blackDeck.size());
        pickedBlackCard = blackDeck.get(blackCardIndex);
        blackDeck.remove(blackCardIndex);
        
        Message m = new Message();
        m.setRoomCode(roomCode);
        m.setType("Picked Black Card");
        m.setName("server");
        m.setText(pickedBlackCard.text);
        m.setCards(null);
        this.pushNotification(m);
        
        // Send the card to the judge player also
        m = new Message();
        m.setRoomCode(roomCode);
        m.setType("Picked Black Card");
        m.setName(this.players.get(judgeIndex).getName());
        m.setText(pickedBlackCard.text);
        m.setCards(null);
        this.players.get(judgeIndex).pushNotification(m);
    }
    
    public void giveJudgeAnswerCard(Message request)
    {
        //TODO: remove card from players hand for next draw
        
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
    }
    
    public void judgeSelectsWinningCard(Message request)
    {
        Message m = new Message();
        m.setRoomCode(roomCode);
        m.setType("Notify Winner");
        m.setName("server");
        m.setText(request.getText());
        m.setCards(request.getCards());
        this.pushNotification(m);
        
        // Add to score
        addToPlayerScore(request.getText());
        
        m = new Message();
        m.setRoomCode(roomCode);
        m.setType("Update Score");
        m.setName("server");
        
        String name = getPlayerByName(request.getText()).getName();
        String score = Integer.toString(getPlayerByName(request.getText()).getScore());
        
        m.setText(name + "," + score);
        m.setCards(null);
        this.pushNotification(m);
    }
}
