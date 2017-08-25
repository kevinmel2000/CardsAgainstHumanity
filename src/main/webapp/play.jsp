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
            <img src='images/logo-mobile.png' onclick="confirmRestartGame()" />
        </div>

        <div id='userInfo'>
            <div>
                <label>Your Name:</label><input class="login" id='name' type="text" />
            </div>
            <div>
                <label>Room Code:</label><input class="login" id='roomCode' type="text" />  
            </div>
            <div>
                <input id="joinRoomButton"     type="button" value="Join Room"       onClick='joinRoom()' />
                <input id="allPlayersInButton" type="button" value="All Players In!" onClick='allPlayersIn()' />
                <div id='status' style='margin-top:5px'></div> 
                <div id='instructions'>
                    <h4>Instructions</h4>
                    <p>Cards Against Humanity is a web based version of the popular crazy and politically incorrect card game.  Not for the easily offended!
                    <h4>How to Play</h4>
                    In the game, one person is chosen to be the judge.  All other players are dealt 7 cards.  The judge draws a black card 
                    and it is shown to all of the players. Players pick white cards to complete the black card.  Some black cards only 
                    need one pick, while others may need more. The judge will review the chosen cards, and select the most silly ones.  That player will win a point. 
                    The next player then becomes the judge and the play continues.
                    <h4>Winning</h4>
                    <p>There is no formal end to the game - play to 10 points or 100 points.  The real goal is to have fun!</p>
                    <p>**Disclaimer- this is a fan-made game and is not endorsed by Cards Against Humanity LLC.  This implementation was written by
                        <a href='mailto:scott.hutter@gmail.com'>Scott Hutter</a>.</p>
                </div>
            </div> 
        </div>

        <div id='playerDisplay' style='display:none'>
            <div class='smallblackcard'></div>
            <div id='playerActionButtons' class='actionButtons'>
               <input id="cardSelectionSubmit" type="Submit" onClick="submitSelection()" /> 
            </div>
        </div>
        
        <div id='judgeDisplay' style='display:none'>
            <div class='smallblackcard'></div>
            <div id='judgeActionButtons' class='actionButtons'>
                <input id="winningCardsSelectionSubmit" type="Submit" onClick="submitWinningCardsSelection()" />
                <div id='judgeStartNewRound'>
                    <table>
                        <tr>
                            <td><img src='images/arrow.gif' style='width:40px; height:40px'/></td>
                            <td><input type="button" value="Start New Round" onClick='startNewRound()' /></td>
                        </tr>
                    </table>
            </div>
        </div>
        
        <div id='restartGame'>
            <h3>Restart The Game</h3>
            Are you sure?<br />
            <input id='restartButton' type='radio' name='restart' value='yes'>Yes<br />
            <input id='restartButton' type='radio' name='restart' value='no'>No<br/>
            <br />
            <input type='button' value='Select' onClick='restartGame()'>
        </div>
        <input style='display:none' type='button' value='RESTART GAME' onClick='confirmRestartGame()'>
    </body>
</html>
