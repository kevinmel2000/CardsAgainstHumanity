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
        <div style='float:left'>
            <img src='images/logo.png' />
        </div>

        <div style='float:left; margin-left:20px;'>
            <p>Please use your mobile device and go to the following url to join this room:</p>
            <span id='host'></span>            
            <h3>Room Code: <span id='roomId'></span></h3>
        </div>
        
        <div style='clear:both; padding-top: 20px'>
            <h3>Logged In Players<h3>
            <div id="players"></div>
        </div>

    </body>
</html>
