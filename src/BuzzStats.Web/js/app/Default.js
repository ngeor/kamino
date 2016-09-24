var $ = require('jquery');

/**
 * Pluralizes a message.
 */
function pluralize(number, singular, plural) {
    'use strict';
    if (number === 1) {
        return number + ' ' + singular;
    } else {
        return number + ' ' + plural;
    }
}

/**
 * Formats a timespan of hours as a human readable string.
 */
function formatDays(nHours) {
    'use strict';
    var nDays = nHours / 24;
    var strDays = pluralize(parseInt(nDays, 10), 'ημέρα', 'ημέρες');
    var strHours;

    nHours = parseInt(nHours % 24, 10);
    if (nHours >= 1) {
        strHours = pluralize(nHours, 'ώρα', 'ώρες');
        return strDays + ' ' + strHours;
    } else {
        return strDays;
    }
}

/**
 * Formats a timespan of minutes as a human readable string with a resolution
 * of hours or days.
 */
function formatHoursOrDays(nMinutes) {
    'use strict';
    var nHours = nMinutes / 60;
    var strHours;
    var strMinutes;

    if (nHours < 24) {
        strHours = pluralize(parseInt(nHours, 10), 'ώρα', 'ώρες');
        nMinutes = parseInt(nMinutes % 60, 10);
        if (nMinutes >= 1) {
            strMinutes = pluralize(nMinutes, 'λεπτό', 'λεπτά');
            return strHours + ' ' + strMinutes;
        } else {
            return strHours;
        }
    } else {
        return formatDays(nHours);
    }
}

/**
 * Formats a timespan that represents an age.
 */
function formatAge(ageInMinutes) {
    'use strict';
    var postfix;
    if (ageInMinutes <= 1) {
        postfix = 'δευτερόλεπτα';
    } else {
        if (ageInMinutes <= 60) {
            postfix = pluralize(parseInt(ageInMinutes, 10), 'λεπτό', 'λεπτά');
        } else {
            postfix = formatHoursOrDays(ageInMinutes);
        }
    }

    return 'πριν ' + postfix;
}

/**
 * Formats a timespan in a seconds resolution.
 */
function formatAgeInSeconds(ageInSeconds) {
    'use strict';
    if (ageInSeconds > 60) {
        return formatAge(ageInSeconds / 60);
    }

    return 'πριν ' + ageInSeconds + ' δευτερόλεπτα';
}

/**
 * Formats a json date.
 */
function formatJsonDateAsAge(jsonDate) { // jshint ignore:line
    'use strict';
    var dt = new Date(parseInt(jsonDate.substr(6), 10));
    var now = new Date();
    var timezoneOffset = now.getTimezoneOffset();
    var diff = (now.getTime() - dt.getTime() + timezoneOffset * 60 * 1000) / 1000;
    return formatAgeInSeconds(parseInt(diff, 10));
}

module.exports = {
    formatJsonDateAsAge: formatJsonDateAsAge
};

$(document).ready(function() {
    'use strict';
    $('header button').click(function() {
        $('nav').toggleClass('visible');
    });
});
