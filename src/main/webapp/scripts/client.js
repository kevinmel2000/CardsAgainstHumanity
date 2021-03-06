// Global variables available to all functions
var roomCode = "";
var name = "";
var hostUrl = "http://" + document.location.host + "/CAH";
var api = "/api/v1/com";
var intervalId = 0;
var pollInterval = 1000;
var audio = new Audio('images/ding.wav');
var pickCount = 0;
var selectOrder = 0;

 $(document).ready(function() {

    resetPage();

 });

// This function is used to post messages to the server
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
             $("#joinRoomButton").hide();
             $("#allPlayersInButton").show();
             
             $("#name").attr("disabled", "disabled");
             $("#roomCode").attr("disabled", "disabled");
        }
    },
    error: function( jqXhr, textStatus, errorThrown ){
        $('#status').html(textStatus);
    }
});   
    
}

// This function is called on an interval to check for new messages
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
                pickCount = parseInt(message.text);
                $("#status").html("Please pick " + message.text + " card(s).");
                
                for (var x=0; x<7; x++)
                {
                    var div = "<div class='smallwhitecard'>";
                         div += "<span id='" + message.cards[x].id + "' class='cardOrder'></span>";
                         div += "<input ";
                         div += " id='selectionCheckBox-" + message.cards[x].id + "'";
                         div += " type='checkbox'";
                         div += " name='choice'";
                         div += " value='" + message.cards[x].id + "'";
                         div += " txt=\"" + message.cards[x].text + "\"";
                         div += " order=0";
                         div += " onClick='setSelectOrder(" + message.cards[x].id + ")'";
                         div += ">" + message.cards[x].text;
                         
                      div += "</div>";
                   $('#otherPlayerCards').before(div);                   
                }

                $("#judgeDisplay").hide();
                $("#playerDisplay").show();
            }
            
            if (message.type === "All Players In")
            {
                $("#allPlayersInButton").hide();
            }
            
            if (message.type === "You Are Judge")
            {
                $("#status").html("You are the judge in this round.");
                $("#judgeDisplay").show();
                $("#playerDisplay").hide();
                $("#winningCardsSelectionSubmit").prop("disabled", true);
            }
            
            if (message.type === "Picked Black Card")
            {
                $("#status").append(" Players pick " + message.cards[0].pick + " card(s).");
                $('.smallblackcard').html(message.cards[0].text);
            }
            
            if (message.type === "Give Card To Judge")
            {
                // build list of cards for judge to review
                var div = "<div id='answerCard' class='smallwhitecard' style='padding-top:0px;padding-bottom:0px'>";
                    div += "<table>";
                        div += "<tr>";
                            div += "<td style='width:1px'>";
                                div += "<input type='checkbox' name='choice'";
                                div += " value='" + message.cards[0].id + "'";
                                div += " txt=\"" + message.cards[0].text + "\"";
                                div += " player='" + message.text + "'";
                                div += "/>";
                            div += "</td>";
                            
                            div += "<td>";
                                div += "<ol style='margin-left:-25px'>";
                                for(var x=0; x<message.cards.length; x++)
                                {
                                    div += "<li>" + message.cards[x].text + "</li>";
                                }
                                div += "</ol>";
                            div += "</td>";
                        div += "</tr>";
                    div += "</table>";
                div += "</div>";
                $('#judgeActionButtons').before(div);
                
                $("#winningCardsSelectionSubmit").prop("disabled", false);
            }
            
            if (message.type === "Show Card To Other Players")
            {
                var div = "<div id='answerCard' class='smallwhitecard' style='padding-top:0px;padding-bottom:0px' player='" + message.text + "'>";
                    div += "<div class='answerCardPlayerName'>" + message.text + "</div>";
                    div += "<ol style='margin-left:-25px'>";
                    for(var x=0; x<message.cards.length; x++)
                    {
                        div += "<li>" + message.cards[x].text + "</li>";
                    }
                    div += "</ol>";
                div += "</div>";
                
                $('#otherPlayerCards').append(div);
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
                $('#otherPlayerCards').html("<div>Other Player's Cards</div>");
                $('#otherPlayerCards').hide();
            }
            
            if (message.type === "Notify Winner")
            {
                $('#status').html("Player: " + message.text + " wins!");
                $("div[class='answerCardPlayerName']").show();
                
                // Display checkmark on winning card
                var winningCard =  ".smallwhitecard[player='" + message.text + "']";
                $(winningCard).prepend("<div style='display:block; margin-top:0px;float:right;'><img src='images/checkmark.png' style='width:40px;height:40px'></div>");
                                
                $('#playerActionButtons').before(div); 
            }
            
            if (message.type === "Start New Game")
            {
                resetPage();
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
    
    intervalId = setInterval(getMessage, pollInterval);
    
    $("#instructions").hide();
}

function allPlayersIn() {
    
    // hide the button for this player
    $("#allInButton").hide();
    
    // notify all the other players to hide this button also
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
    // check if they picked the required card count
    var thisPickCount = 0;
    $('#playerDisplay input:checked').each(function() {
        thisPickCount++;
    });
    
    if (pickCount !== thisPickCount)
    {
        $('#status').html("<span style='color:red'>Can not submit. You must pick " + pickCount + " cards.<span>");
        return;
    }

    //Disable picking cards
    $("#playerDisplay input[type='checkbox']").each(function() {
        $(this).prop("disabled", true);
    });

    // Notify the server which cards have been picked
    var request = {};
    request.type = "Cards Selected";
    request.roomCode = roomCode;
    request.name = name;
    request.text = "";
    request.cards = [];
    
    var unsortedCards = [];
    // Loops through the judge picked winning cards and adds them to an unsorted array
    $('#playerDisplay input:checked').each(function() {
        var card = {};
        card.id = $(this).val();
        card.text = $(this).attr('txt');
        card.order = $(this).attr('order');
        unsortedCards.push(card);
    });
    
    // put the unsorted cards into the request in the proper order
    for(var x=1; x<=pickCount; x++)
    {
        for(z=0;z<unsortedCards.length;z++)
        {
            if(unsortedCards[z].order == x)
              request.cards.push(unsortedCards[z]);  
        }    
    }
    
    // Reformat card and display it at top.  Other player cards will display below
    //$('#playerDisplay input:checkbox:not(:checked)').parent().remove();
    $('#playerDisplay input:checkbox').parent().remove();

    var div = "<div id='answerCard' class='smallwhitecard' style='padding-top:0px;padding-bottom:0px' player='" + name + "'>";
        div += "<ol style='margin-left:-25px'>";
        for(var x=0; x<request.cards.length; x++)
        {
            div += "<li>" + request.cards[x].text + "</li>";
        }
        div += "</ol>";
    div += "</div>";

    $('#otherPlayerCards').before(div); 
    $('#otherPlayerCards').show();
    
    $('#cardSelectionSubmit').hide();
    $('#status').html("Selection made. Waiting for the judge selection.");
    
    postMessage(request);
    
    // Reset values for next round
    pickCount = 0;
    selectOrder = 0;
}

function setSelectOrder(cardId)
{
    var selectionCheckBoxId = "#selectionCheckBox-" + cardId;
    
    if($(selectionCheckBoxId).prop('checked') === true){
    
        selectOrder++;
        $(selectionCheckBoxId).attr("order", selectOrder);
    
        var selector = "#" + cardId;
        $(selector).html(selectOrder);
        $(selector).show();       
    }
    else
    {
        selectOrder=0;

        $(".smallwhitecard input:checked").prop('checked', false);
        
        $(".cardOrder").each(function() {
            $(this).html("");
            $(this).hide();
        });
    }
}

function submitWinningCardsSelection()
{
    // This message tells the server which cards the judge picked to win
    var request = {};
    request.type = "Winning Cards Selected";
    request.roomCode = roomCode;
    request.name = name;
    request.cards = [];

    // loop through the selected cards and add them to the message
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
    // Judge clicked the Start New Round button, so send notification to server
    var request = {};
    request.type = "Start New Round";
    request.roomCode = roomCode;
    request.name = name;
    request.text = "";
    request.cards = [];
    
    $('#judgeStartNewRound').hide();
    
    postMessage(request);
}

function confirmRestartGame() 
{
    $('#restartGame').show();
}

function restartGame()
{
    var answer = $('input[name=restart]:checked').val();
    $('#restartGame').hide();
    
    if(answer === "yes")
    {
        var request = {};
        request.type = "Start New Game";
        request.roomCode = roomCode;
        request.name = "server";
        request.text = "";
        request.cards = [];

        postMessage(request);
        
        resetPage();
    }
}

function resetPage()
{
        clearInterval(intervalId);
        roomCode = "";
        name = "";
        pickCount = 0;
        selectOrder = 0;
        
        $("#name").prop("disabled", false);
        $("#roomCode").prop("disabled", false);
        
        $("#name").val("");
        $("#roomCode").val("");
        $("#allPlayersInButton").hide();
        $("#joinRoomButton").show();
        $("#status").html("");
        $("#judgeDisplay").hide();
        $("#playerDisplay").hide();
        $("#instructions").show();
        $('#otherPlayerCards').html("<div>Other Player's Cards</div>");
        $('#otherPlayerCards').hide();
        $("div[class='answerCardPlayerName']").hide();
}
