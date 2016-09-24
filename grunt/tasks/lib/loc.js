var endOfLine = require('os').EOL;

exports.fileLoc = function(contents) {
    var _ = require('underscore');
    var lines = contents.split(endOfLine);
    var loc = lines.length;
    var sloc;
    var ssloc;

    lines = _.filter(lines, function(line) {
        return line && line.trim();
    });

    sloc = lines.length;

    lines = _.filter(lines, function(line) {
        return line.indexOf('//') !== 0;
    });

    ssloc = lines.length;

    return {
        loc: loc,
        sloc: sloc,
        ssloc: ssloc
    };
};

exports.write = function(grunt, dest, loc, sloc, ssloc) {
    var fieldSeparator = ',';

    grunt.file.write(
    dest,
    'LOC' + fieldSeparator + 'SLOC' + fieldSeparator + 'SSLOC' + endOfLine +
    loc + fieldSeparator + sloc + fieldSeparator + ssloc + endOfLine);
};
