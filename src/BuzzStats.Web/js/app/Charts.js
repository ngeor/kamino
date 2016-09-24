/* globals alert */
var $ = require('jquery');
var Highcharts = require('highcharts');
var chart;
var statsService = {
    pendingRequests: 0,

    call: function(methodName, request, responseDataType, onSuccess) {
        var _this = this;
        this.pendingRequests++;
        $.ajax({
            url: 'api/ConsoleService.asmx/' + methodName,
            data: request,
            type: 'POST',
            contentType: 'application/json',
            dataType: responseDataType,

            success: function(data) {
                _this.pendingRequests--;
                onSuccess(data);
                enableControls();
            },

            error: function(xhr) {
                // ugly hack to reactivate UI
                _this.pendingRequests--;
                enableControls();

                // show message
                if (xhr && xhr.responseText) {
                    alert(xhr.responseText);
                } else {
                    alert('Παρουσιάστηκε σφάλμα');
                }
            }
        });
    },

    callJson: function(methodName, request, onSuccess) {
        this.call(methodName, request, 'json', onSuccess);
    },

    callTimeRequest: function(methodName, from, to, intervalLength, onSuccess) {
        var request = {
            request: {
                Start: formatDate(from),
                Stop: formatDate(to),
                Interval: intervalLength
            }
        };
        this.callJson(methodName, JSON.stringify(request), onSuccess);
    },

    storyCount: function(from, to, intervalLength, onSuccess) {
        this.callTimeRequest('GetStoryCountStats', from, to, intervalLength, onSuccess);
    },

    commentCount: function(from, to, intervalLength, onSuccess) {
        this.callTimeRequest('GetCommentCountStats', from, to, intervalLength, onSuccess);
    }
};

/**
    * Converts a JSON date string into a date.
    */
function convertJsonDate(dt) {
    var strippedDate = dt.substr(6);

    // parseInt can handle the trailing non numeric characters
    var millisecondsSinceEpoch = parseInt(strippedDate);

    // convert to date
    var date = new Date(millisecondsSinceEpoch);

    return date;
}

/**
    * Draws a series.
    */
function drawSeries(response, seriesIndex) {
    var timePairResponse = response.d;
    var start = convertJsonDate(timePairResponse.Start);
    var data = timePairResponse.Data;
    var newData = [];
    var date = start;
    var intervalLength = $('select#selIntervalLength').val();
    var i;
    var millis;

    for (i = 0; i < data.length; i++) {
        millis = Date.UTC(date.getFullYear(), date.getMonth(), date.getDate());
        newData[i] = [millis, data[i]];

        if (intervalLength === 'Day') {
            date.setDate(date.getDate() + 1);
        } else if (intervalLength === 'Week') {
            date.setDate(date.getDate() + 7);
        } else if (intervalLength === 'Month') {
            date.setMonth(date.getMonth() + 1);
        } else if (intervalLength === 'Year') {
            date.setYear(date.getYear() + 1);
        }
    }

    chart.series[seriesIndex].setData(newData);
}

/**
    * Formats a date.
    */
function formatDate(dt) {
    return dt.getFullYear() + '/' + (dt.getMonth() + 1) + '/' + (dt.getDate());
}

/**
    * Draws the stories series.
    */
function drawStories(response) {
    drawSeries(response, 0);
}

/**
    * Draws the comments series.
    */
function drawComments(response) {
    drawSeries(response, 1);
}

/**
    * Disables user input controls.
    */
function disableControls() {
    var $btnRefreshCharts = $('input#btnRefreshCharts');
    var $imgProgress = $btnRefreshCharts.next();

    $btnRefreshCharts.attr('disabled', true);
    $imgProgress.show();
}

/**
    * Enables user input controls.
    */
function enableControls() {
    var $btnRefreshCharts;
    var $imgProgress;

    if (statsService.pendingRequests > 0) {
        return;
    }

    $btnRefreshCharts = $('input#btnRefreshCharts');
    $imgProgress = $btnRefreshCharts.next();
    $btnRefreshCharts.removeAttr('disabled');
    $imgProgress.hide();
}

/**
    * Initializes the charts UI.
    */
function start() {
    // TODO: Initialize form with current month values
    chart = new Highcharts.Chart({
        chart: {
            renderTo: 'chartContainer',
            zoomType: 'x',
            spacingRight: 20
        },
        title: {
            text: 'Γραφήματα'
        },
        subtitle: {
            text: document.ontouchstart === undefined ?
                'Click and drag in the plot area to zoom in' :
                'Drag your finger over the plot to zoom in'
        },
        xAxis: {
            type: 'datetime',
            title: {
                text: 'Ημερομηνία'
            }
        },
        yAxis: {
            title: {
                text: 'Άρθρα και σχόλια'
            },
            min: 0.0,
            startOnTick: false,
            showFirstLabel: false
        },
        tooltip: {
            shared: true
        },
        legend: {
            enabled: false
        },
        series: [
            {
                name: 'Άρθρα',
                data: []
            },
            {
                name: 'Σχόλια',
                data: []
            }
        ]
    });
}

$(document).ready(function() {
    var $chartForm;
    var startDate;
    var endDate;
    var selectedInterval;

    if (!document.getElementById('chartContainer')) {
        return;
    }

    start();
    $chartForm = $('div#chartForm');

    /**
        * Gets the selected interval.
        */
    function getSelectedInterval() {
        return $('select', $chartForm).val();
    }

    /**
        * Parses the date.
        */
    function parseDate(action, valueForEmptyString) {
        var inputDate = $('.date-range-picker').dateRangePicker(action) || valueForEmptyString;
        return new Date(inputDate);
    }

    /**
        * Checks whether the form is valid.
        */
    function isFormValid() {
        startDate = parseDate('getFromValue', '2000-01-01');
        endDate = parseDate('getToValue', '3000-01-01');
        selectedInterval = getSelectedInterval();

        return startDate && endDate && startDate <= endDate && selectedInterval;
    }

    /**
        * Sets the enabled state of the button based on the form.
        */
    function enableDisableButton() {
        if (isFormValid()) {
            $('input[type="submit"]', $chartForm).removeAttr('disabled');
        } else {
            $('input[type="submit"]', $chartForm).attr('disabled', 'disabled');
        }
    }

    $('select', $chartForm).change(function() {
        enableDisableButton();
    });

    $('input[type="date"]', $chartForm).change(function() {
        enableDisableButton();
    });

    $('#btnRefreshCharts', $chartForm).click(function() {
        if (isFormValid()) {
            disableControls();
            statsService.storyCount(startDate, endDate, selectedInterval, drawStories);
            statsService.commentCount(startDate, endDate, selectedInterval, drawComments);
        } else {
            alert('Παρακαλώ συμπληρώστε όλα τα πεδία με έγκυρες τιμές.');
        }

        return false;
    });
});
