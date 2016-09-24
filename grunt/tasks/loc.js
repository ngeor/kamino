/**
 * Performs LOC measurements.
 */
function locAnalysis(grunt, files, dest) {
    var locLib = require('./lib/loc');
    var i;
    var file;
    var contents;
    var data;
    var loc = 0;
    var sloc = 0;
    var ssloc = 0;

    for (i = files.length - 1; i >= 0; i--) {
        file = files[i];
        contents = grunt.file.read(file);
        data = locLib.fileLoc(contents);

        loc = loc + data.loc;
        sloc = sloc + data.sloc;
        ssloc = ssloc + data.ssloc;
    }

    console.log(loc); // eslint-disable-line no-console
    console.log(sloc); // eslint-disable-line no-console
    console.log(ssloc); // eslint-disable-line no-console

    locLib.write(grunt, dest, loc, sloc, ssloc);
}

module.exports = function(grunt) {
    grunt.registerMultiTask('loc', 'Measure loc', function() {
        var files = this.filesSrc;
        var dest = this.options().dest;
        locAnalysis(grunt, files, dest);
    });
};
