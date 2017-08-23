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
            <img src='images/logo-mobile.png' />
        </div>

        <div id='userInfo'>
            <div>
                <label>Your Name:</label><input class="login" id='name' type="text" />
            </div>
            <div>
                <label>Room Code:</label><input class="login" id='roomCode' type="text" />  
            </div>
            <div>
                <input id="joinRoom" type="Submit" onClick='joinRoom()' />
            </div> 
            <div id='status'></div>
            <div id='waitingOnOthers'>
                <input id='allInButton' type="button" value="All Players In!" onClick='allPlayersIn()' />
            </div>   
        </div>

        <div id='playerDisplay' style='display:none'>  
            <input id="cardSelectionSubmit" type="Submit" onClick="submitSelection()" />
        </div>
        
        <div id='judgeDisplay' style='display:none'>
            <div id='blackCard' class='smallblackcard'></div>
            <input id="winningCardsSelectionSubmit" type="Submit" onClick="submitWinningCardsSelection()" />
            <input id='judgeStartNewRound' type="button" value="Start New Round" onClick='startNewRound()' />
        </div>
        
    </body>
</html>
