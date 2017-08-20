/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hutter.cah;

import java.util.ArrayList;
import java.util.Random;

/**
 *
 * @author scott
 */
public class Room {
    
    private String id;
    private final int max = 8;
    private final ArrayList<Player> players = new ArrayList<>();
    private ArrayList<Card> deck = new ArrayList<>();
    private boolean locked = false;
    private int judgeIndex = 0;
    private int gameState = 0;
    
    public String getId()
    {
        return id;
    }
    
    public void setId(String code)
    {
        id = code;
    }
    
    public void setDeck(ArrayList<Card> deck)
    {
        this.deck = deck;
    }
    
    public ArrayList<Card> getDeck()
    {
        return deck;
    }
    
    public void setLock(boolean locked)
    {
        this.locked = locked;
        
        if (this.locked)
            playGame();
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
        deck.add(c);
    }

    public void playGame()
    {
        Random r = new Random();
        
        // Give each player 7 cards
        // Loop through each player
        for(int playerIndex=0; playerIndex< this.players.size(); playerIndex++)
        {
            // Draw 7 cards for the player
            for (int counter=0; counter<7; counter++)
            {
               int randomDeckCardIndex = r.nextInt(deck.size());

               // take a random card from the deck
               Card drawnCard = deck.get(randomDeckCardIndex);
               deck.remove(drawnCard);

               // give it to the player
               this.players.get(playerIndex).AddCard(drawnCard);   
            }
            
            // inform the player of his cards
            Message m = new Message();
            m.setId("123");
            m.setText("Cards dealt");
            this.players.get(playerIndex).pushNotification(m);
        }
        
        // Notify each player of their cards
        
        // Draw a black card and display it on screen
        
        // Notify players to select a card
        
        // Display selected cards on the screen
        
        // Notify judge to pick a winning card
        
        // Give points to winner
    }
    
}
