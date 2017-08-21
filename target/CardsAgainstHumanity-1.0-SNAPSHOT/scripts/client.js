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
                $('#card0').html("[1] " + message.cards[0].text);
                $('#card1').html("[2] " + message.cards[1].text);
                $('#card2').html("[3] " + message.cards[2].text);
                $('#card3').html("[4] " + message.cards[3].text);
                $('#card4').html("[5] " + message.cards[4].text);
                $('#card5').html("[6] " + message.cards[5].text);
                $('#card6').html("[7] " + message.cards[6].text);
                $("#cards").show();
            }
            
            if (message.type === "All Players In")
            {
                $("#allInButton").hide();
            }
            
            if (message.type === "You Are Judge")
            {
                $("#status").html("You are the judge in this round. Please wait while all players select their choice card.");
                $("#cards").hide();
            }

        },
        error: function( jqXhr, textStatus, errorThrown ){
            $('#status').html(textStatus);
        }
    });   
       
    
}

function joinRoom()
{
    name = $('#name').val();
    
    var request = {};
    request.type = "Join Room";
    request.roomCode = "";
    request.name = name;
    request.text = $('#roomCode').val();
    request.card = null;
    
    postMessage(request);
    
    setInterval(getMessage, 3000);
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


