/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hutter.cah;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

/**
 *
 * @author scott
 */
public class Player {
    
    private String name;
    private boolean isJudge; 
    private final ArrayList<Card> cards;
    private LocalDateTime lastPing;
    private boolean away;
    private Queue<Message> messageQueue = new LinkedList<Message>();
    private int score;
    
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
    
    public void setAway(boolean away)
    {
        this.away = away;
    }
    
    public boolean getAway()
    {
        return this.away;
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
    
    public void clearNotifications()
    {
        this.messageQueue.clear();
    }
    
    public boolean isNotificationPending()
    {
        return this.messageQueue.size() > 0;
    }
    
    public void setScore(int score)
    {
        this.score = score;
    }
    
    public int getScore()
    {
        return this.score;
    }
    
    public ArrayList<Card> getCards()
    {
        return this.cards;
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
