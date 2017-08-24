var roomCode = "";
var hostUrl = "http://" + document.location.host + "/CAH";
var api = "/api/v1/com";
    
 $(document).ready(function() {

    startNewGame(roomCode);

 });
 
 function startNewGame(roomCode) 
 {
     // Get an available room code
     var message = {};
     message.roomCode = roomCode;
     message.name = "server";
     message.type = "Get Room Code";
     message.text = "";
     message.cards = null;
     
     postMessage(message);
     
     $("#host").html("<a href='" + hostUrl + "/play.jsp'>" + hostUrl + "/play.jsp</a>");

     setInterval(getMessage, 1000);
 }
 
 
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
            
            if (message.type === "Get Room Code")
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
            if (message.type === "Get Message")
            {
                $('#status').html(message.text);
            }

            if (message.type === "Player Joined")
            {
                var judgeCell = "<td class='judgeScoreClass' id='judge-" + message.text + "' align=center></td>";
                var nameCell  = "<td>" + message.text + "</td>";
                var scoreCell = "<td id='score-" + message.text + "' align=center>0</td>";
                
                $("#playersTable tbody").append("<tr>" + judgeCell + nameCell + scoreCell + "</tr>");
            }

            if (message.type === "Picked Black Card")
            {
                var blackCard = "<div id='blackCard' class='largeblackcard'>" + message.text + "</div>";
                $("#cards").html(blackCard);
            }
            
            if (message.type === "Judge Selected")
            {
                var judgeSelector = "#judge-" + message.text;
                $(judgeSelector).html("*");
            }
            
            if (message.type === "Give Card To Judge")
            {
                var div = "<div id='answerCard' class='largewhitecard' style='position:relative'";
                        div += " value='" + message.cards[0].id + "'";
                        div += " txt='" + message.cards[0].text + "'";
                        div += " player='" + message.text + "'";
                        div += ">" + message.cards[0].text;
                   
                        // player name
                        div += "<div class='hiddenPlayerName' style='margin-top:200px; text-align:center'>" + message.text + "</div>";
                   div += "</div>";
                   
                $('#blackCard').after(div);
            }
            
            if (message.type === "Notify Winner")
            {
                // Display checkmark on winning card
                var winningCard =  ".largewhitecard[player='" + message.text + "']";
                $(winningCard).append("<div style='position:absolute; bottom:0; display:block; margin:auto;'><img src='images/checkmark.png'></div>");
                
                // Show player names
                $(".hiddenPlayerName").show();
                
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
            
            if (message.type === "Start New Game")
            {
                startNewGame(roomCode);
            }
            

        },
        error: function( jqXhr, textStatus, errorThrown ){
            $('#status').html(textStatus);
        }
    });   
    
}
