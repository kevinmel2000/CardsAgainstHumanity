/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hutter.cah;

import java.util.ArrayList;

/**
 *
 * @author scott
 */
public class Message {
    
    private String roomCode;
    private String name;
    private String type;
    private String text;
    private ArrayList<Card> cards;
    
    public String getRoomCode()
    {
        return this.roomCode;
    }
    
    public void setRoomCode(String roomCode)
    {
        this.roomCode = roomCode;
    }
    
    public String getName()
    {
        return this.name;
    }
    
    public void setName(String name)
    {
        this.name = name;
    }
    
    public String getType()
    {
        return this.type;
    }
    
    public void setType(String type)
    {
        this.type = type;
    }
    
    public String getText()
    {
        return this.text;
    }
    
    public void setText(String text)
    {
        this.text = text;
    }
    
    public ArrayList<Card> getCards()
    {
        return this.cards;
    }
    
    public void setCards(ArrayList<Card> cards)
    {
        this.cards = cards;
    }
}
