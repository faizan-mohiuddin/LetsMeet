let player = null;


$(document).ready(function () {
    $('.lm-egg-dangerZone').click(function () {

        player == null ? playAudio("https://ia802904.us.archive.org/24/items/TopGunThemeFilmVersionDangerZone/TopGunAnthemDangerzoneFilmIntro.mp3") : player.pause();
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
