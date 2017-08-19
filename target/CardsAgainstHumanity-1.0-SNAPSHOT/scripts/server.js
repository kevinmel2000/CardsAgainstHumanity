var roomCode = "error";
var hostUrl = "http://" + document.location.host;
    
 $(document).ready(function() {

     // Get an available room code
     var host = hostUrl + "/CAH/api/status/getRoom";
     $.getJSON(host, function( room ) {
         roomCode = room.id;
         $("#roomId").text(roomCode);
     }); 

     $("#host").text(hostUrl + "/CAH/join.asp");

     setInterval(getPlayersList, 1000);

 });

 function getPlayersList() {

     var host = hostUrl + "/CAH/api/status/getPlayersList";
     var request = {};
     request.roomCode = roomCode;

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
                 $('#players').html("Invalid room code.");
             }
             else
             {
                 var htmlNames = "<ul>";
                 for(var p=0; p<data.length; p++)
                 {
                     htmlNames += "<li>" + data[p] + "</li>"; 
                 }
                 htmlNames += "</ul>";
                 
                 $('#players').html(htmlNames);

             }

         },
         error: function( jqXhr, textStatus, errorThrown ){
             $('#status').html(textStatus);
         }
     }); 

 }

