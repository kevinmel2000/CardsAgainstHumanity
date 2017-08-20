var hostUrl = "http://" + document.location.host;

function joinRoom() {

    var host = hostUrl + "/CAH/api/status/joinRoom";

    var request = {};
    request.roomCode = $('#roomCode').val();
    request.name = $('#playerName').val();

    $.ajax({
        url: host,
        dataType: 'json',
        type: 'post',
        contentType: 'application/json',
        data: JSON.stringify(request),
        processData: false,
        success: function( data, textStatus, jQxhr ){

            if(data === undefined)
            {
                $('#status').html("Invalid room code.");
            }
            else
            {
                $('#status').html("Joined room. Waiting for game start");
                
                setInterval(pingServer, 3000);
            }

        },
        error: function( jqXhr, textStatus, errorThrown ){
            $('#status').html(textStatus);
        }
    });    

}

function pingServer() {
    
        var host = hostUrl + "/CAH/api/status/getStatus";
        var request = {};
        request.roomCode = $('#roomCode').val();
        request.name = $('#playerName').val();
    
        var jsonRequest = JSON.stringify(request);
    
        $.ajax({
        url: host,
        dataType: 'json',
        type: 'post',
        contentType: 'application/json',
        data: jsonRequest,
        processData: false,
        success: function( data, textStatus, jQxhr ){
            
            $('#status').html(data.text);
            
            if (data.text === "Cards dealt")
            {
                displayCards();
            }
            
        },
        error: function( jqXhr, textStatus, errorThrown ){
            $('#status').html(textStatus);
        }
    });   
    
}

function allPlayersIn() {
    
        var host = hostUrl + "/CAH/api/status/roomLock";
        var request = {};
        request.roomCode = $('#roomCode').val();
        request.name = $('#playerName').val();
    
        $.ajax({
        url: host,
        dataType: 'json',
        type: 'post',
        contentType: 'application/json',
        data: JSON.stringify(request),
        processData: false,
        success: function( data, textStatus, jQxhr ){
            
            $("#allInButton").hide();
            
        },
        error: function( jqXhr, textStatus, errorThrown ){
            $('#status').html(textStatus);
        }
    });  
    
}

function displayCards()
{
        var host = hostUrl + "/CAH/api/status/getPlayerCards";
        var request = {};
        request.roomCode = $('#roomCode').val();
        request.name = $('#playerName').val();
    
        var jsonRequest = JSON.stringify(request);
    
        $.ajax({
        url: host,
        dataType: 'json',
        type: 'post',
        contentType: 'application/json',
        data: jsonRequest,
        processData: false,
        success: function( data, textStatus, jQxhr ){
            
            $('#card0').html(data[0].text);
            $('#card1').html(data[1].text);
            $('#card2').html(data[2].text);
            $('#card3').html(data[3].text);
            $('#card4').html(data[4].text);
            $('#card5').html(data[5].text);
            $('#card6').html(data[6].text);
                        
        },
        error: function( jqXhr, textStatus, errorThrown ){
            $('#status').html(textStatus);
        }
    });   
}
