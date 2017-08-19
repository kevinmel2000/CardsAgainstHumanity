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
public class Room {
    
    private String id;
    private final int max = 8;
    private final ArrayList<Player> players;
    
    public String getId()
    {
        return id;
    }
    
    public void setId(String code)
    {
        id = code;
    }
    
    public Room()
    {
        players = new ArrayList<>();
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
    
}
