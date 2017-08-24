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
        <title>Cards Against Humanity</title>
        <%@ page import="java.util.ArrayList, java.net.InetAddress, com.hutter.cah.Room" %>
        <link href="styles/standard.css" rel="stylesheet" type="text/css"/>
        <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.2.1/jquery.min.js"></script>
        <script src="scripts/server.js"></script>
    </head>
    <body>


        <div id='header'>
            <div id='logoSection'>
                <img src='images/logo.png' />
            </div>
            <div id='statusSection'>
                <p>Please use your mobile device and go to the following url to join this room:</p>
                <span id='host'></span>            
                <h4>Room Code: <span id='roomCode'></span></h4>
                <h4>Server status: <span id='status'></span></h4>
            </div>
        </div>
        
        <div>
            <div id='scoreSection'>
                <div class='tableHeader'>Logged In Players</div>
                <div >
                    <table id="playersTable">
                        <thead>
                            <tr><th>Judge</th><th>Player</th><th>Score</th></tr>
                        </thead>
                        <tbody></tbody>
                    </table>
                </div>
            </div>

            <div id='cards'></div>
       </div>
        
    </body>
</html>
