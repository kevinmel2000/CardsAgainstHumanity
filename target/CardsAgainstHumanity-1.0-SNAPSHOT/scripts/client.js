var roomCode = "";
var name = "";
var hostUrl = "http://" + document.location.host + "/CAH";
var api = "/api/v1/com";

 $(document).ready(function() {

 });

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
        if (message.type === "Join Room")
        {
             roomCode = message.roomCode;
             roomCode = roomCode.toUpperCase();
             $('#status').html(message.text);
             $("#joinRoom").hide();
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
    request.name = name;
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
            if (message.type === "Get Message" && message.text !== "")
            {
                 $('#status').html(message.text);
            }

            if (message.type === "Cards Dealt")
            {
                for (var x=0; x<7; x++)
                {
                    var div = "<div class='smallwhitecard'>";
                         div += "<input type='checkbox' name='choice' value='" + message.cards[0].id + "'";
                         div += " txt='" + message.cards[x].text + "'";
                         div += ">" + message.cards[x].text;
                      div += "</div>";
                   $('#cardSelectionSubmit').before(div);                   
                }

                $("#judgeDisplay").hide();
                $("#playerDisplay").show();
            }
            
            if (message.type === "All Players In")
            {
                $("#allInButton").hide();
            }
            
            if (message.type === "You Are Judge")
            {
                $("#status").html("You are the judge in this round. Please wait while all players select their choice card.");
                $("#judgeDisplay").show();
                $("#playerDisplay").hide();
            }
            
            if (message.type === "Picked Black Card")
            {
                $('#blackCard').html(message.text);
            }
            
            if (message.type === "Give Card To Judge")
            {
                var div = "<div id='answerCard' class='smallwhitecard'>";
                      div += "<input type='checkbox' name='choice' value='" + message.cards[0].id + "'";
                      div += " txt='" + message.cards[0].text + "'";
                      div += " player='" + message.text + "'";
                      div += ">" + message.cards[0].text;
                   div += "</div>";
                $('#winningCardsSelectionSubmit').before(div);
            }
            
            if (message.type === "Reset Device")
            {
                $('#status').html("New round");
                $('#judgeStartNewRound').hide();
                $("#playerDisplay").hide();
                $("#judgeDisplay").hide();
                $('#cardSelectionSubmit').show();
                $('#winningCardsSelectionSubmit').show();
                $("div[class='smallwhitecard']").remove();
            }

        },
        error: function( jqXhr, textStatus, errorThrown ){
            $('#status').html(textStatus);
        }
    });   
       
    
}

function joinRoom()
{
    // Converts input to uppercase
    name = $('#name').val();
    name = name.toUpperCase();
    $('#name').val(name);
    
    var attemptedRoomCode = $('#roomCode').val();
    attemptedRoomCode = attemptedRoomCode.toUpperCase();
    $('#roomCode').val(attemptedRoomCode);
        
    var request = {};
    request.type = "Join Room";
    request.roomCode = "";
    request.name = name;
    request.text = attemptedRoomCode;
    request.card = null;
    
    postMessage(request);
    
    setInterval(getMessage, 1000);
}

function allPlayersIn() {
    
    var request = {};
    request.type = "All Players In";
    request.roomCode = roomCode;
    request.name = name;
    request.text = "";
    request.card = null;
    
    postMessage(request);
}

function submitSelection()
{
    var request = {};
    request.type = "Cards Selected";
    request.roomCode = roomCode;
    request.name = name;
    request.text = "";
    request.cards = [];
    
    $('#playerDisplay input:checked').each(function() {
        
        var card = {};
        card.id = $(this).val();
        card.text = $(this).attr('txt');
        request.cards.push(card);
    });
    
    $('#cardSelectionSubmit').hide();
    $('#status').html("Selection made. Waiting for other players.");
    
    postMessage(request);
}

function submitWinningCardsSelection()
{
    var request = {};
    request.type = "Winning Cards Selected";
    request.roomCode = roomCode;
    request.name = name;
    request.cards = [];
    
    $('#judgeDisplay input:checked').each(function() {
        
        var card = {};
        card.id = $(this).val();
        card.text = $(this).attr('txt');
        request.cards.push(card);
        request.text = $(this).attr('player');
    });
    
    $('#winningCardsSelectionSubmit').hide();
    $('#status').html("Player: " + request.text + " wins!");
    $('#judgeStartNewRound').show();
    
    postMessage(request);
}

function startNewRound()
{
    var request = {};
    request.type = "Start New Round";
    request.roomCode = roomCode;
    request.name = name;
    request.text = "";
    request.cards = [];
    
    $('#judgeStartNewRound').hide();
    
    postMessage(request);
}
