/* eslint-disable max-lines, max-statements */

var csharplintLib = require('./lib/csharplint');
var LinterResult = csharplintLib.LinterResult;
var namespacePriority = csharplintLib.namespacePriority;
var aliasCompare = csharplintLib.aliasCompare;
var alphaCompare = csharplintLib.alphaCompare;
var getUsingLineIndices = csharplintLib.getUsingLineIndices;
var csharplintLinters = {};

/**
 * Checks if a file is entirely commented out.
 */
csharplintLinters.isCommentedOut = function(grunt, file, contents, lines, options, reporter) {
    var i;
    var line;
    for (i = 0; i < lines.length; i++) {
        line = lines[i].trim();
        if (line && line.indexOf('//') !== 0) {
            return LinterResult.success();
        }
    }

    reporter(file, 'File is entirely commented out', 'warning');
    return LinterResult.failure();
};

/**
 * Checks if a file has trailing whitespace.
 */
csharplintLinters.hasTrailingWhitespace = function(grunt, file, contents, lines, options, reporter) {
    var failure = false;
    var newContents = '';
    var i;
    var line;

    for (i = 0; i < lines.length; i++) {
        line = lines[i];
        if (line && (line.charAt(line.length - 1) === ' ' || line.charAt(line.length - 1) === '\t')) {
            reporter(file, 'Trailing whitespace', 'warning', i + 1);
            failure = true;
        }

        if (options.fix) {
            newContents += line.trimRight() + grunt.util.linefeed;
        }
    }

    if (failure) {
        grunt.log.writeln('Trailing whitespace in ' + file);
        return LinterResult.failure(newContents);
    }

    return LinterResult.success();
};

csharplintLinters.exceedsMaximumLineLength = function(grunt, file, contents, lines, options, reporter) {
    var i;
    var line;
    var failure = false;

    if (!options || !options.maximumLineLength) {
        return LinterResult.success();
    }

    for (i = 0; i < lines.length; i++) {
        line = lines[i];
        if (line.length > options.maximumLineLength) {
            reporter(file, 'Line length exceeds maximum (' + line.length + ')', 'warning', i + 1);
            failure = true;
        }
    }

    if (failure) {
        grunt.log.writeln('Maximum line length exceeded in ' + file);
        return LinterResult.failure();
    }

    return LinterResult.success();
};

/**
 * Checks if a file has empty lines at the end.
 * Note the file should still be terminated by an EOL.
 */
csharplintLinters.hasEmptyLinesAtTheEnd = function(grunt, file, contents, lines, options, reporter) {
    var lastLine;
    var failure = false;
    var newContents = '';
    var i;
    var line;
    var nonEmptyIndex;

    if (!lines || lines.length <= 2) {
        // don't bother
        return LinterResult.success();
    }

    lastLine = lines[lines.length - 1];

    if (lastLine.trim()) {
        // last line should have been EOL, otherwise I don't bother
        return LinterResult.success();
    }

    for (i = lines.length - 2; i >= 0; i--) {
        line = lines[i].trim();
        if (!line) {
            reporter(file, 'Empty line at end of file', 'warning', i + 1);
            failure = true;
        } else {
            break;
        }
    }

    if (failure && options.fix) {
        nonEmptyIndex = i;
        for (i = 0; i <= nonEmptyIndex; i++) {
            newContents += lines[i] + grunt.util.linefeed;
        }
    }

    if (failure) {
        reporter(file, 'Multiple empty lines at EOF', 'warning', i);
        return LinterResult.failure(newContents);
    }

    return LinterResult.success();
};

/**
 * Checks that using statements are sorted.
 */
csharplintLinters.areUsingStatementsSorted = function(grunt, file, contents, lines, options, reporter) {
    var failure = false;
    var i;
    var line;
    var usingStatements;
    var sortedUsingStatements;
    var newLines;
    var newContents;
    var usingLineIndices = getUsingLineIndices(lines);
    var comparators = [

        // aliases are last
        aliasCompare,

        // system namespaces are first
        namespacePriority('System', true),

        // project namespaces are last
        namespacePriority('BuzzStats', false),

        // project namespaces are last
        namespacePriority('NGSoftware', false),

        // alphabetic comparison is the final comparison
        alphaCompare
    ];

    if (usingLineIndices.first === -1 || usingLineIndices.last <= usingLineIndices.first) {
        // did not find using statements
        // or only one statement
        grunt.log.debug('Did not find using statements');
        return LinterResult.success();
    }

    usingStatements = [];
    for (i = usingLineIndices.first; i <= usingLineIndices.last; i++) {
        line = (lines[i] || '').trim();
        if (line) {
            usingStatements.push(line);
        }
    }

    if (usingStatements.length <= 1) {
        // only one using statement after removing empty lines
        grunt.log.debug('usingStatements.length', usingStatements.length);
        grunt.log.debug('usingLineIndices', usingLineIndices);
        return LinterResult.success();
    }

    sortedUsingStatements = usingStatements.slice(0, usingStatements.length);
    sortedUsingStatements.sort(function(a, b) {
        var namespaceA = a.substr(0, a.indexOf(';')).substr('using '.length);
        var namespaceB = b.substr(0, b.indexOf(';')).substr('using '.length);
        var i;
        var comparator;
        var result;

        for (i = 0; i < comparators.length; i++) {
            comparator = comparators[i];
            result = comparator(namespaceA, namespaceB);
            if (result !== 0) {
                return result;
            }
        }

        return 0;
    });

    for (i = 0; i < usingStatements.length; i++) {
        if (usingStatements[i] !== sortedUsingStatements[i]) {
            failure = true;
            reporter(file, 'Using statements are not sorted', 'warning', usingLineIndices.first);
            break;
        }
    }

    grunt.log.debug('Actual order', usingStatements);
    grunt.log.debug('Expected order', sortedUsingStatements);

    if (failure) {
        grunt.log.debug('Actual order:');
        for (i = 0; i < usingStatements.length; i++) {
            grunt.log.debug(usingStatements[i]);
        }

        grunt.log.debug('Expected order:');
        for (i = 0; i < sortedUsingStatements.length; i++) {
            grunt.log.debug(sortedUsingStatements[i]);
        }

        if (options.fix) {
            newLines = lines.slice(0, usingLineIndices.first);
            newLines = newLines.concat(sortedUsingStatements);
            newLines = newLines.concat(lines.slice(usingLineIndices.last + 1));
            newContents = newLines.join(grunt.util.linefeed);
        }

        return LinterResult.failure(newContents);
    }

    return LinterResult.success();
};

/**
 * Checks if a single C# file is valid.
 */
function checkSingleFile(grunt, file, options, reporter) {
    var failure = false;
    var linterName;
    var linter;
    var contents;
    var lines;
    var linterResult;

    contents = grunt.file.read(file);
    lines = contents.split(grunt.util.linefeed);
    if (lines.length <= 1) {
        reporter(file, 'File is empty', 'warning');
        return true;
    }

    for (linterName in csharplintLinters) {
        if (!csharplintLinters.hasOwnProperty(linterName)) {
            continue;
        }

        if (options.excludeLinters && options.excludeLinters.indexOf(linterName) >= 0) {
            continue;
        }

        grunt.log.debug('Linting ' + file + ' with ' + linterName);
        linter = csharplintLinters[linterName];
        linterResult = linter(grunt, file, contents, lines, options, reporter);
        if (linterResult.isFailure()) {
            failure = true;
            if (linterResult.hasNewContents()) {
                contents = linterResult.getNewContents();
                grunt.file.write(file, contents);
                lines = contents.split(grunt.util.linefeed);
            }
        }
    }

    return failure;
}

/**
 * Validates the options of a task.
 */
function validateOptions(grunt, options) {
    var i;
    var linterName;
    if (options.excludeLinters) {
        for (i = options.excludeLinters.length - 1; i >= 0; i--) {
            linterName = options.excludeLinters[i];
            if (!csharplintLinters[linterName]) {
                grunt.log.error('Unknown linter: ' + linterName);
                return false;
            }
        }
    }

    return true;
}

/**
 * Runs C# linting against a collection of files.
 */
function csharplint(grunt, files, options) {
    'use strict';

    var failures = 0;
    var checkstyleReport = '<checkstyle version="5.0">';
    var i;
    var file;
    var reporter = function(file, message, severity, lineNumber) {
        if (lineNumber) {
            grunt.log.warn(file + ' - ' + message + ' at line ' + lineNumber);
        } else {
            grunt.log.warn(file + ' - ' + message);
        }

        checkstyleReport += '<error ' +
        'line="' + (lineNumber || 1) + '" ' +
        'message="' + message + '" ' +
        'severity="' + severity + '" ' +
        'source="csharplint" />' + grunt.util.linefeed;
    };

    for (i = files.length - 1; i >= 0; i--) {
        file = files[i];
        checkstyleReport += grunt.util.linefeed;
        checkstyleReport += '<file name="' + file + '">';
        if (checkSingleFile(grunt, file, options, reporter)) {
            failures++;
        }

        checkstyleReport += grunt.util.linefeed;
        checkstyleReport += '</file>';
    }

    checkstyleReport += grunt.util.linefeed + '</checkstyle>' + grunt.util.linefeed;

    grunt.log.writeln('Processed ' + files.length + ' files, ' +
    failures + ' failed. Threshold is ' + options.toleratedFailures);
    if (options.reporterOutput) {
        grunt.file.write(options.reporterOutput, checkstyleReport);
    }

    return failures <= options.toleratedFailures || options.force;
}

/**
 * C# Linter grunt task.
 * @module tasks/csharplint
 */
module.exports = function(grunt) {
    grunt.registerMultiTask('csharplint', 'CSharp Linter', function() {
        var done = this.async();
        var options = this.options({
            force: false,
            reporter: null,
            toleratedFailures: 0
        });
        var success = validateOptions(grunt, options) && csharplint(grunt, this.filesSrc, options);
        done(success);
    });

    grunt.registerTask('csharplintsingle', 'C# lint a single file', function() {
        var file = grunt.option('file');
        var options = {};
        var reporter = function(file, message, severity, lineNumber) {
            if (lineNumber) {
                grunt.log.warn(file + ' - ' + message + ' at line ' + lineNumber);
            } else {
                grunt.log.warn(file + ' - ' + message);
            }
        };

        checkSingleFile(grunt, file, options, reporter);
    });
};
