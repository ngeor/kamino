export function loadJson(url, callback) {
    const request = new XMLHttpRequest();
    request.open('GET', url, true);
    request.onload = function() {
        if (request.status >= 200 && request.status < 400) {
            const data = JSON.parse(request.responseText);
            callback(null, data);
        } else {
            callback(new Error(request.status));
        }
    };

    request.onerror = function() {
        callback(new Error(request.responseText));
    };

    request.send();
}
