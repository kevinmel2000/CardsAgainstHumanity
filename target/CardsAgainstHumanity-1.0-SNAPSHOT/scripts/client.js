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
                $('#card0').html("<input type='checkbox' name='choice' value='" + message.cards[0].id + "' txt='" + message.cards[0].text + "'>" + message.cards[0].text);
                $('#card1').html("<input type='checkbox' name='choice' value='" + message.cards[1].id + "' txt='" + message.cards[1].text + "'>" + message.cards[1].text);
                $('#card2').html("<input type='checkbox' name='choice' value='" + message.cards[2].id + "' txt='" + message.cards[2].text + "'>" + message.cards[2].text);
                $('#card3').html("<input type='checkbox' name='choice' value='" + message.cards[3].id + "' txt='" + message.cards[3].text + "'>" + message.cards[3].text);
                $('#card4').html("<input type='checkbox' name='choice' value='" + message.cards[4].id + "' txt='" + message.cards[4].text + "'>" + message.cards[4].text);
                $('#card5').html("<input type='checkbox' name='choice' value='" + message.cards[5].id + "' txt='" + message.cards[5].text + "'>" + message.cards[5].text);
                $('#card6').html("<input type='checkbox' name='choice' value='" + message.cards[6].id + "' txt='" + message.cards[6].text + "'>" + message.cards[6].text);
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
                $('#answerCardSelectionSubmit').before(div);
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
    $('#status').html("Selection made. Waiting for other players.")
    
    postMessage(request);
}

