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
    
        var host = hostUrl + "/CAH/api/status/ping";
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
        },
        error: function( jqXhr, textStatus, errorThrown ){
            $('#status').html(textStatus);
        }
    });   
    
}

