
// Get an API token using the existing session
function getToken() {
    return $.ajax({
        type: "GET",
        url: `http://localhost:8080/api/User/token`,
        async: false
    }).responseText;
}
