
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
