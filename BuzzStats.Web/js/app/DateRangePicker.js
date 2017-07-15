var $ = require('jquery');
require('bootstrap-datepicker');

/**
    * Cache the selectors.
    */
function cacheSelectors($obj) {
    // store the two top-level DIVs
    $obj.$display = $obj.$display || $obj.children('.date-range-picker-display');
    $obj.$dropdown = $obj.$dropdown || $obj.children('.date-range-picker-dropdown');
    return $obj;
}

/**
    * Close the drop down.
    */
function close($obj) {
    $obj.$dropdown.slideUp('fast');
    return $obj;
}

/**
    * Get the starting date.
    */
function getFromValue($obj) {
    return $obj.$dropdown.find('input[id$="txtFrom"]').val();
}

/**
    * Get the end date.
    */
function getToValue($obj) {
    return $obj.$dropdown.find('input[id$="txtTo"]').val();
}

/**
    * Formats a date as an ISO date string.
    */
function toISODate(dt) {
    return dt.toISOString().substr(0, 10);
}

/**
    * Updates the text display based on user input.
    */
function updateDisplayFromControls($obj) {
    var txtFrom = getFromValue($obj);
    var txtTo = getToValue($obj);
    var value;

    if (!txtFrom) {
        if (!txtTo) {
            value = '(χωρίς περιορισμό)';
        } else {
            value = 'έως ' + txtTo;
        }
    } else {
        if (!txtTo) {
            value = 'από ' + txtFrom;
        } else {
            value = txtFrom + ' - ' + txtTo;
        }
    }

    $obj.$display.children('span').text(value);

    // fix min-max
    // $obj.$dropdown.find('input[id$="txtTo"]').datepicker('option', 'minDate', txtFrom);
    // $obj.$dropdown.find('input[id$="txtFrom"]').datepicker('option', 'maxDate', txtTo);
}

/**
    * Sets data from the string parameters.
    */
function applyStrings($obj, start, stop) {
    $obj.$dropdown.find('input[id$="txtFrom"]').val(start);
    $obj.$dropdown.find('input[id$="txtTo"]').val(stop);
    updateDisplayFromControls($obj);
    close($obj);
}

/**
    * Sets data from the date parameters.
    */
function applyDates($obj, start, stop) {
    applyStrings($obj, toISODate(start), toISODate(stop));
}

/**
    * Initializes the date range picker UI.
    */
function initialize($obj) {
    // get the current HTML
    var innerHtml = $obj.html();

    // replace it with this one
    $obj.html('<div class="date-range-picker-display"><span></span><em>▼</em></div>' +
        '<div class="date-range-picker-dropdown" style="display:none">Περιορισμός σε χρονικό διάστημα' +
        '<ul>' +
        '<li><a href="#" class="limitWeek">εβδομάδα</a></li>' +
        '<li><a href="#" class="limitMonth">μήνα</a></li>' +
        '<li><a href="#" class="limitAllTime">χωρίς περιορισμό</a></li>' +
        '<li>' + innerHtml + '</li>' +
        '</ul></div>');

    cacheSelectors($obj);

    // bind the click event on the display
    $obj.$display.click(function() {
        $obj.$dropdown.slideToggle('fast');
        return false;
    });

    // bind the datepicker controls
    $obj.$dropdown.find('input.js-date-range-picker').each(function() {
        $(this).change(function() {
            updateDisplayFromControls($obj);
        });

        $(this).datepicker({
            format: 'yyyy-mm-dd',
            autoclose: true
        });
    });

    // refresh the label of the control
    updateDisplayFromControls($obj);

    $obj.$dropdown.find('a[class="limitWeek"]').click(function() {
        var startDate = new Date();
        startDate.setDate(startDate.getDate() - 7);
        applyDates($obj, startDate, new Date());
        return false;
    });

    $obj.$dropdown.find('a[class="limitMonth"]').click(function() {
        var startDate = new Date();
        startDate.setDate(startDate.getDate() - 30);
        applyDates($obj, startDate, new Date());
        return false;
    });

    $obj.$dropdown.find('a[class="limitAllTime"]').click(function() {
        applyStrings($obj, '', '');
        return false;
    });

    return $obj;
}

$.fn.dateRangePicker = function(action) {
    if (!this || this.length === 0) {
        return this;
    }

    if (action === 'getFromValue') {
        return getFromValue(cacheSelectors(this));
    }

    if (action === 'getToValue') {
        return getToValue(cacheSelectors(this));
    }

    if (action === 'close') {
        return close(cacheSelectors(this));
    }

    this.each(function() {
        initialize($(this));
    });

    $(document).mouseup(function(e) {
        var $container = $('.date-range-picker');

        if (!e) {
            return;
        }

        if (!e.target) {
            return;
        }

        // don't hide anything if this is the ui-datepicker...
        if (typeof e.target.className === 'string' && (e.target.className || '').indexOf('ui-') === 0) {
            return;
        }

        // if the target of the click isn't the container...
        // ... nor a descendant of the container
        if (!$container.is(e.target) && $container.has(e.target).length === 0) {
            $container.dateRangePicker('close');
        }
    });

    return this;
};

$(document).ready(function() {
    $('.date-range-picker').dateRangePicker();
});
