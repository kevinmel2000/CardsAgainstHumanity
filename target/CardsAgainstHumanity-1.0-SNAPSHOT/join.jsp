<%-- 
    Document   : room
    Created on : Aug 18, 2017, 8:08:24 AM
    Author     : scott
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <title>Cards Against Humanity</title>
        <%@ page import="java.util.ArrayList, java.net.InetAddress, com.hutter.cah.Room" %>
        <link href="styles/standard.css" rel="stylesheet" type="text/css"/>
        <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.2.1/jquery.min.js"></script>
        <script src="scripts/client.js"></script>
    </head>
    <body>       
        <div>
            <img src='images/logo.png' />
        </div>

        <div>
            Your Name: <input id='playerName' type="text" />
        </div>
        <div>
            Room Code: <input id='roomCode' type="text" />  
        </div>
        <div>
            <input type="Submit" onClick='joinRoom()' />
        </div>    

        <div id='status'></div>
        
        <br />
        <br />
        <input id='allInButton' type="button" value="All Players In!" onClick='allPlayersIn()' />
        
        <div id='card0' style='border:1px solid black; padding: 5px 5px 5px 5px'></div>
        <div id='card1' style='border:1px solid black; padding: 5px 5px 5px 5px'></div>
        <div id='card2' style='border:1px solid black; padding: 5px 5px 5px 5px'></div>
        <div id='card3' style='border:1px solid black; padding: 5px 5px 5px 5px'></div>
        <div id='card4' style='border:1px solid black; padding: 5px 5px 5px 5px'></div>
        <div id='card5' style='border:1px solid black; padding: 5px 5px 5px 5px'></div>
        <div id='card6' style='border:1px solid black; padding: 5px 5px 5px 5px'></div>
        
    </body>
</html>
