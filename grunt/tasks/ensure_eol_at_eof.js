'use strict';

module.exports = function(grunt) {
    function ensureEolAtEof(file) {
        var buf = grunt.file.read(file);
        var eol = grunt.util.linefeed;
        var missingEol = buf && buf.substr(buf.length - eol.length) !== eol;
        grunt.log.debug('\n\n file: %s, missingEol: %s', file, missingEol);

        if (missingEol) {
            grunt.log.ok('Adding EOL to ' + file);
            grunt.file.write(file, buf + eol);
        }
    }

    grunt.registerMultiTask('ensure_eol_at_eof', 'Ensures files have a trailing EOL', function() {
        this.filesSrc.forEach(function(filePath) {
            ensureEolAtEof(filePath);
        });
    });
};
