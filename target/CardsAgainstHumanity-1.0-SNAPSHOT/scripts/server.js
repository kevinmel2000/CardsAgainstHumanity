// Global variables available to all functions
var roomCode = "";
var hostUrl = "http://" + document.location.host + "/CAH";
var api = "/api/v1/com";
var intervalId = 0;
var pollInterval = 1000;
var audio = new Audio('images/fanfare.wav');


// The ready function is always called as soon as the page is loaded
$(document).ready(function() {

    $('#generateRoomSection').show();

});
 
function createRoom()
{
    $('#generateRoomSection').hide();
    $('#statusSection').show();
     
    startNewGame("");
}
 
 function startNewGame() 
 {
    // Asks the server to create a new room and send us a room code
    var message = {};
    message.roomCode = "";
    message.name = "server";
    message.type = "Create Room";
    message.text = "";
    message.cards = null;

    postMessage(message);

    $("#host").html("<a href='" + hostUrl + "/play.jsp'>" + hostUrl + "/play.jsp</a>");

    // Enable the timer which will check for new server messages every second
    intervalId = setInterval(getMessage, pollInterval);
 }
 
// Function to send a request to the server.  Responses are pulled
// from the getMessage() function every interval
function postMessage(message) {
    
        var host = hostUrl + api;
        var jsonRequest = JSON.stringify(message);
    
        $.ajax({
        url: host,
        dataType: 'json',
        type: 'post',
        contentType: 'application/json',
        data: jsonRequest,
        processData: false,
        success: function(message, textStatus, jQxhr)
        {
            $('#status').html(message.type);
            
            if (message.type === "Create Room")
            {
                roomCode = message.text;
                $('#roomCode').html(roomCode);
                $('#cards').html("");
                $('#playersTable tbody').empty();
            }
        },
        error: function( jqXhr, textStatus, errorThrown ){
            $('#status').html(textStatus);
        }
    });   
    
}

// Once a room has been created, this function is
// polled by the server page for new updates
function getMessage() {

    var host = hostUrl + api;
    
    var request = {};
    request.roomCode = roomCode;
    request.name = "server";
    request.type = "Get Message";
    request.text = "";
    request.card = null;
    var jsonRequest = JSON.stringify(request);
    
    $.ajax({
        url: host,
        dataType: 'json',
        type: 'post',
        contentType: 'application/json',
        data: jsonRequest,
        processData: false,
        success: function(message, textStatus, jQxhr)
        {
            // this section handles the actual incoming messages
            
            if (message.type === "Get Message")
            {
                $('#status').html(message.text);
            }
            
            if (message.type === "Start New Game")
            {
                clearInterval(intervalId);
                $('#generateRoomSection').show();
                $('#statusSection').hide();
                $('#playersTable tbody').empty();
                $('#cards').empty();
            }

            if (message.type === "Player Joined")
            {
                // Try to find if this player dropped and returned
                var selector = "#playersTable td:contains('" + message.text + "')";
                var foundReturningPlayer = $(selector);
                
                if (foundReturningPlayer.length !== 0)
                {
                    // If player is returning, set name back to normal (it was italic)
                    $(selector).html(message.text);
                }
                else
                {
                    // This is a new player. Add them to the list
                    var judgeCell = "<td class='judgeScoreClass' id='judge-" + message.text + "' align=center></td>";
                    var nameCell  = "<td>" + message.text + "</td>";
                    var scoreCell = "<td id='score-" + message.text + "' align=center>0</td>";
                
                    $("#playersTable tbody").append("<tr>" + judgeCell + nameCell + scoreCell + "</tr>");
                } 
            }
            
            if (message.type === "Dropped Player")
            {
                var selector = "#playersTable td:contains('" + message.text + "')";
                $(selector).html("<i>" + message.text + "</i>");
            }

            if (message.type === "Picked Black Card")
            {
                var blackCard = "<div id='blackCard' class='largeblackcard'>" + message.cards[0].text + "</div>";
                $("#cards").html(blackCard);
            }
            
            if (message.type === "Judge Selected")
            {
                var judgeSelector = "#judge-" + message.text;
                $(judgeSelector).html("*");
            }
            
            if (message.type === "Give Card To Judge")
            {
                var topMargin = 0;
                var div = "<div style='position:relative; float:left'>";
                
                for(var x=0;x<message.cards.length;x++)
                {
                    var cardDiv = "<div id='answerCard' class='largewhitecard' style='margin-top:" + topMargin + "px'";
                            cardDiv += " value='" + message.cards[x].id + "'";
                            cardDiv += " txt='" + message.cards[x].text + "'";
                            cardDiv += " player='" + message.text + "'";
                            cardDiv += ">" + message.cards[x].text;
                            cardDiv += "</div>";
                    
                    div += cardDiv;
                    topMargin = -120;
                }
                
                // player name
                div += "<div class='hiddenPlayerName' style='margin-top:0px; text-align:center'>" + message.text + "</div>";

                $('#blackCard').after(div);
            }
            
            if (message.type === "Notify Winner")
            {
                // Display checkmark on winning card
                var winningCard =  ".largewhitecard[player='" + message.text + "']";
                $(winningCard).append("<div style='position:absolute; bottom:0; display:block; margin:auto;'><img src='images/checkmark.png'></div>");
                
                // Show player names
                $(".hiddenPlayerName").show();
                
                // play sound
                audio.play();
                
            }
            
            if (message.type === "Update Score")
            {
                var vals = message.text.split(",");
                var selector = "#score-" + vals[0];
                $(selector).html(vals[1]);
            }
            
            if (message.type === "Reset Device")
            {
                $('largewhitecard').remove();
                $('#blackCard').remove();
                $("#playersTable td[class='judgeScoreClass']").html(""); 
            }

        },
        error: function( jqXhr, textStatus, errorThrown ){
            $('#status').html(textStatus);
        }
    });   
    
}
