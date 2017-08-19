/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hutter.cah;

import java.time.LocalDateTime;
import java.util.ArrayList;

/**
 *
 * @author scott
 */
public class Player {
    
    private String name;
    private boolean isJudge; 
    private final ArrayList<Card> cards;
    private LocalDateTime lastPing;
    
    // Getters/Setters (Properies)
    public void setName(String name)
    {
        this.name = name;
    }
    
    public String getName()
    {
        return name;
    }
    
    public void setLastPing(LocalDateTime now)
    {
        lastPing = now;
    }
    
    public LocalDateTime getLastPing()
    {
        return this.lastPing;
    }
    
    public void setIsJudge(boolean isjudge)
    {
        this.isJudge = isjudge;
    }
    
    public boolean getIsJudge()
    {
        return this.isJudge;
    }
    
    // Constructor
    public Player()
    {
        cards = new ArrayList<>();
    }
    
    
    // Methods
    public void AddCard(Card c)
    {
        cards.add(c);
    }
    
    public int getCardCount()
    {
        return cards.size();
    }
    
    
    
}
