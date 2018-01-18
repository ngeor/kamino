/**
 * Parses the given string and returns the amount of milliseconds since epoch.
 * @param {string} s - The string to parse.
 * @returns {number} The amount of milliseconds since epoch.
 */
export function parseDate(s) {
    return Date.parse(s);
}

function pluralize(value, singular, plural) {
    return value === 1 ? singular : plural;
}

function pluralizeAgo(value, singular, plural) {
    const truncatedValue = parseInt(value, 10);
    return truncatedValue + ' ' + pluralize(truncatedValue, singular, plural) + ' ago';
}

/**
 * Converts the given amount of milliseconds to a string that states
 *     how much time has passed since that date.
 * @param {number} milliseconds - The amount of milliseconds.
 * @returns {string} A human friendly string.
 */
export function agoString(milliseconds) {
    const minutes = milliseconds / 1000 / 60;
    if (minutes < 1) {
        return 'a few seconds ago';
    }

    const hours = minutes / 60;
    if (hours < 1) {
        return pluralizeAgo(minutes, 'minute', 'minutes');
    }

    const days = hours / 24;
    if (days < 1) {
        return pluralizeAgo(hours, 'hour', 'hours');
    }

    const weeks = days / 7;
    if (weeks < 1) {
        return pluralizeAgo(days, 'day', 'days');
    }

    const months = days / 30;
    if (months < 1) {
        return pluralizeAgo(weeks, 'week', 'weeks');
    }

    return pluralizeAgo(months, 'month', 'months');
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
