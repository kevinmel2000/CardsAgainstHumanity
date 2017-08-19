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
public class Player {
    
    private String name;
    private final ArrayList<Card> cards;
    
    public void setName(String name)
    {
        this.name = name;
    }
    
    public String getName()
    {
        return name;
    }
    
    public Player()
    {
        cards = new ArrayList<>();
    }
    
    public void AddCard(Card c)
    {
        cards.add(c);
    }
    
    public int getCardCount()
    {
        return cards.size();
    }
    
    
    
}
