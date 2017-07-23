function hello() {
    console.log('hello, world!');
}

function loadJson(url, callback) {
    var request = new XMLHttpRequest();
    request.open('GET', url, true);
    request.onload = function() {
        if (request.status >= 200 && request.status < 400) {
            var data = JSON.parse(request.responseText);
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

import React from 'react';
import ReactDOM from 'react-dom';

document.addEventListener('DOMContentLoaded', function() {
    hello();
    ReactDOM.render(
        <p>hello,  world!</p>,
        document.getElementById('root')
    );
});
