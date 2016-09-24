var $ = require('jquery');

/**
 * Clones an object. This is a shallow clone.
 */
function clone(obj) {
    var result = {};
    var x;
    for (x in obj) {
        if (obj.hasOwnProperty(x)) {
            result[x] = obj[x];
        }
    }

    return result;
}

/**
 * Supplements the first object with properties of the second one.
 */
function fallback(obj, fallbackObj) {
    var x;
    for (x in fallbackObj) {
        if (fallbackObj.hasOwnProperty(x)) {
            obj[x] = obj[x] || fallbackObj[x];
        }
    }

    return obj;
}

/**
 * Merges the second object into the first one.
 */
function merge(obj, mergeObj) {
    var x;
    for (x in mergeObj) {
        if (mergeObj.hasOwnProperty(x)) {
            obj[x] = mergeObj[x];
        }
    }

    return obj;
}

var readService2 = {
    call: function(methodName, options) {
        var urlPrefix = window.location.href.indexOf('/BuzzStats/') > 0 ? '/BuzzStats' : '';
        var newOptions = clone(options || {});

        newOptions = merge(newOptions, {
            url: urlPrefix + '/api/ConsoleService.asmx/' + methodName
        });
        newOptions = fallback(newOptions, {
            type: 'POST',
            contentType: 'application/json',
            dataType: 'json',
            timeout: 5000
        });

        if (typeof newOptions.data === 'object') {
            newOptions.data = JSON.stringify(newOptions.data);
        }

        return $.ajax(newOptions);
    },

    getStorySummaries: function(rowIndex, maxRows, sortField, options) {
        var data = {
            request: {
                rowIndex: rowIndex,
                maxRows: maxRows,
                sortBy: [
                    {
                        field: sortField,
                        direction: 'Descending'
                    }
                ]
            }
        };

        var newOptions = clone(options);

        newOptions = merge(newOptions, {
            data: data
        });

        return this.call(
            'GetStorySummaries',
            newOptions
        );
    }
};

var readService = {
    defaultError: function() {
    },

    call: function(methodName, request, onSuccess, onError, options) {
        var urlPrefix = window.location.href.indexOf('/BuzzStats/') > 0 ? '/BuzzStats' : '';
        options = options || {};

        if (typeof request === 'object') {
            request = JSON.stringify(request);
        }

        $.ajax({
            url: urlPrefix + '/api/ConsoleService.asmx/' + methodName,
            data: request,
            type: 'POST',
            contentType: 'application/json',
            dataType: options.dataType || 'json',
            success: onSuccess,
            error: onError,
            timeout: options.timeout || 5000
        });
    },

    loadStory: function(storyId, onSuccess) {
        this.call('LoadStory', storyId, onSuccess, this.defaultError, {
            dataType: 'xml'
        });
    },

    getHostStats: function(startIndex, maxResults, minStoryCount, sortExpression, onSuccess, onError) {
        this.call(
            'GetHostStats',
            JSON.stringify({
                options: {
                    StartIndex: startIndex,
                    MaxResults: maxResults,
                    MinStoryCount: minStoryCount,
                    SortExpression: sortExpression
                }
            }),
            onSuccess,
            onError);
    },

    getRecentCommentsPerStory: function(onSuccess, onError, options) {
        this.call('GetRecentCommentsPerStory', '', onSuccess, onError, options);
    },

    getRecentActivity: function(maxResults, onSuccess, onError, options) {
        this.call(
            'GetRecentActivity',
            JSON.stringify({
                maxResults: maxResults
            }),
            onSuccess,
            onError,
            options);
    },

    getStorySummaries: function(rowIndex, maxRows, sortField, onSuccess, onError) {
        this.call('GetStorySummaries', {
            request: {
                rowIndex: rowIndex,
                maxRows: maxRows,
                sortBy: [
                    {
                        field: sortField,
                        direction: 'Descending'
                    }
                ]

            }
        }, onSuccess, onError);
    }
};

module.exports = {
    readService: readService,
    readService2: readService2
};
