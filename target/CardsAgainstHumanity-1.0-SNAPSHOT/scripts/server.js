var roomCode = "";
var hostUrl = "http://" + document.location.host + "/CAH";
var api = "/api/v1/com";
    
 $(document).ready(function() {

     // Get an available room code
     var message = {};
     message.roomCode = "";
     message.name = "server";
     message.type = "Get Room Code";
     message.text = "";
     message.cards = null;
     
     postMessage(message);
     
     $("#host").html("<a href='" + hostUrl + "/play.jsp'>" + hostUrl + "/play.jsp</a>");

     setInterval(getMessage, 1000);

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
            $('#status').html(message.type);
            
            if (message.type === "Get Room Code")
            {
                $('#roomCode').html(message.text);
                roomCode = message.text;
            }
            
            if (message.type === "Player Joined")
            {               
                $('#players').append(message.text + "<br />");
            }
            
            if (message.type === "Black Card Dealt")
            {
                displayBlackCard();
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
                $("#players tr:last").after("<tr><td>" + message.text + "</td><td id='score-" + message.text + "' align=center>0</td>");
            }

            if (message.type === "Picked Black Card")
            {
                $("#blackCard").html(message.text);
            }
            
            if (message.type === "Judge Selected")
            {
                $("#judgeName").html(message.text);
            }
            
            if (message.type === "Give Card To Judge")
            {
                var div = "<div id='answerCard' class='largewhitecard'";
                      div += " value='" + message.cards[0].id + "'";
                      div += " txt='" + message.cards[0].text + "'";
                      div += " player='" + message.text + "'";
                      div += ">" + message.cards[0].text;
                   div += "</div>";
                   
                $('#blackCard').after(div);
            }

        },
        error: function( jqXhr, textStatus, errorThrown ){
            $('#status').html(textStatus);
        }
    });   
    
}
