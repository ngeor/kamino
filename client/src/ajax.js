/**
 * Fills-in options with defaults.
 * @param {object} options - The options to use.
 * @returns {object} The options with defaults.
 */
function fillOptionsWithDefaults(options) {
    const defaults = {
        method: 'GET',
        contentType: '',
        data: null
    };

    return Object.assign({}, defaults, options);
}
/**
 * Creates an AJAX request.
 * @param {string} url - The url to fetch.
 * @param {object} [options] - The options to use.
 * @returns {Promise} A promise that resolves with the fetched data.
 */
export default function ajax(url, options) {
    const promise = new Promise(function(resolve, reject) {
        const request = new XMLHttpRequest();
        const _options = fillOptionsWithDefaults(options);
        const {
            method,
            contentType,
            data
        } = _options;
        request.open(method, url, true);
        if (contentType) {
            request.setRequestHeader('Content-Type', contentType);
        }
        request.onload = function() {
            const SUCCESS_MIN = 200;
            const SUCCESS_MAX = 400;
            if (this.status >= SUCCESS_MIN && this.status < SUCCESS_MAX) {
                // Success!
                try {
                    const response = JSON.parse(this.response);
                    resolve(response);
                } catch (err) {
                    reject(err);
                }
            } else {
                // We reached our target server, but it returned an error
                reject(new Error(
                    `We reached our target server, but it returned an error: ${this.response}`));
            }
        };

        request.onerror = function() {
            // There was a connection error of some sort
            reject(new Error('There was a connection error of some sort'));
        };

        request.send(data);
    });

    return promise;
}
