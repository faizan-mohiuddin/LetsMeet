let player = null;


$(document).ready(function () {
    $('.lm-egg-dangerZone').click(function () {
        $(this).parent().animate({left: '20px'}, "fast");
        $(this).parent().animate({left: '-20px'}, "fast");
        $(this).parent().animate({left: '0px'}, "fast");
        player == null ? playAudio("https://ia802904.us.archive.org/24/items/TopGunThemeFilmVersionDangerZone/TopGunAnthemDangerzoneFilmIntro.mp3") : player.pause();
    });

    $('.lm-api-event-delete').click(function () {
        let id = $(this).parent().find(".uuid").val();
        let root = this;

        $.ajax({
            type: "DELETE",
            url: `/api/Event/${id}?Token=${sessionID}`,
            async: true,
            success: function(){
                $(root).closest(".lm-entity").fadeOut();
            }
        });
    });

    $(document).ajaxError(function(){
        alert("An error occurred!");
    });

});


async function playAudio(source) {
    var audio = new Audio(source);
    audio.type = 'audio/mp3';
    audio.currentTime = 182;

    try {
        await audio.play();
        console.log('Playing...');
        player = audio;
    } catch (err) {
        console.log('Failed to play...' + err);
    }
}


// Get an API token using the existing session
function getToken() {
    return $.ajax({
        type: "GET",
        url: `http://localhost:8080/api/User/token`,
        async: false
    }).responseText;
}

function setEventImage(event, imageURL) {
    alert("hello!")
    let token = getToken();
    let url = `http://localhost:8080/api/Event/${event}/Properties?key=event_image&value=${imageURL}`;
    $.ajax({
        url: url,
        type: 'put',
        contentType: false,
        processData: false,
        success: alert("updated")   
    })
}

function deleteEvent(token,eventUUID, onSuccess, onFail) {
    $.ajax({
        type: "DELETE",
        url: `/api/Event/${eventUUID}?Token=${token}`,
        async: false,
        success: onSuccess,
        error: onFail,
    });
}
