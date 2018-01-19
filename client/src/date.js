/**
 * Parses the given string and returns the amount of milliseconds since epoch.
 * @param {string} s - The string to parse.
 * @returns {number} The amount of milliseconds since epoch.
 */
export function parseDate(s) {
    return Date.parse(s);
}

/**
 * Converts a numeric quantity into a string, taking into account singular and plural words.
 * @param {number} value - The value to pluralize.
 * @param {string} singular - The value to use for single quantities.
 * @param {string} plural - The value to use when more than one quantity is used.
 * @returns {string} The text representation.
 */
function pluralize(value, singular, plural) {
    return value === 1 ? singular : plural;
}

/**
 * Converts a numeric age into a string, taking into account singular and plural words.
 * @param {number} value - The value to pluralize.
 * @param {string} singular - The value to use for single quantities.
 * @param {string} plural - The value to use when more than one quantity is used.
 * @returns {string} The text representation.
 */
function pluralizeAgo(value, singular, plural) {
    const truncatedValue = parseInt(value, 10);
    return truncatedValue + ' ' + pluralize(truncatedValue, singular, plural) + ' ago';
}

/**
 * Formats a number of days as text.
 * @param {number} days - The number of days.
 * @returns {string} The text representation.
 * @private
 */
function agoDays(days) {
    const daysInWeek = 7;
    const daysInMonth = 30;

    const weeks = days / daysInWeek;
    if (weeks < 1) {
        return pluralizeAgo(days, 'day', 'days');
    }

    const months = days / daysInMonth;
    if (months < 1) {
        return pluralizeAgo(weeks, 'week', 'weeks');
    }

    return pluralizeAgo(months, 'month', 'months');
}

/**
 * Formats a number of hours as text.
 * @param {number} hours - The number of hours.
 * @returns {string} The text representation.
 * @private
 */
function agoHours(hours) {
    const hoursInDay = 24;
    const days = hours / hoursInDay;
    if (days < 1) {
        return pluralizeAgo(hours, 'hour', 'hours');
    }

    return agoDays(days);
}

/**
 * Converts the given amount of milliseconds to a string that states
 *     how much time has passed since that date.
 * @param {number} milliseconds - The amount of milliseconds.
 * @returns {string} A human friendly string.
 */
export function agoString(milliseconds) {
    const millisInMinute = 60000;
    const minutes = milliseconds / millisInMinute;
    if (minutes < 1) {
        return 'a few seconds ago';
    }

    const minutesInHours = 60;
    const hours = minutes / minutesInHours;
    if (hours < 1) {
        return pluralizeAgo(minutes, 'minute', 'minutes');
    }

    return agoHours(hours);
}

/**
 * Utility function that converts a serialized datetime into a
 *     human friendly string that specifies how old ago that datetime is.
 * @param {string} dateString - The datetime string to parse.
 * @returns {string} A human friendly expression of how old ago the given date is.
 */
export function toAgo(dateString) {
    return agoString(new Date().getTime() - parseDate(dateString));
}
