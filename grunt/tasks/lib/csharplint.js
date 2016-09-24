exports.LinterResult = require('./csharplint/LinterResult');

exports.namespacePriority = function(namespace, atStart) {
    var result = atStart ? -1 : 1;
    return function(namespaceA, namespaceB) {
        if (namespaceA.indexOf(namespace) === 0) {
            return namespaceB.indexOf(namespace) === 0 ? 0 : result;
        }

        return namespaceB.indexOf(namespace) === 0 ? -result : 0;
    };
};

// using aliases should come last (using A = Namespace.A;)
exports.aliasCompare = function(namespaceA, namespaceB) {
    if (namespaceA.indexOf('=') > 0) {
        return namespaceB.indexOf('=') > 0 ? 0 : 1;
    }

    return namespaceB.indexOf('=') > 0 ? -1 : 0;
};

// alphabetic comparison
exports.alphaCompare = function(namespaceA, namespaceB) {
    return namespaceA.toUpperCase().localeCompare(namespaceB.toUpperCase());
};

exports.getUsingLineIndices = function(lines) {
    var i;
    var firstUsingLine = -1;
    var lastUsingLine;
    var line;

    for (i = 0; i < lines.length; i++) {
        line = (lines[i] || '').trim();
        if (!line) {
            continue;
        }

        // detect first using statement
        if (line.indexOf('using ') === 0) {
            if (firstUsingLine === -1) {
                firstUsingLine = i;
            }

            // potentially last using statement
            lastUsingLine = i;
        }

        // exit if we've found the first using statement
        // and we've encountered the first non empty statement
        if (line.indexOf('using ') !== 0 && firstUsingLine >= 0) {
            break;
        }
    }

    return {
        first: firstUsingLine,
        last: lastUsingLine
    };
};
