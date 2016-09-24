var xpath = require('xpath');
var DOMParser = require('xmldom').DOMParser; // jshint ignore:line

function processFile(grunt, file, xpathQuery) {
    var xml = grunt.file.read(file);
    var doc = new DOMParser().parseFromString(xml);
    var nodes = xpath.select(xpathQuery, doc);
    var success = true;

    if (nodes.length > 0) {
        success = false;
        grunt.log.warn('File %s contained xpath %s %d times', file, xpathQuery, nodes.length);
    } else {
        grunt.verbose.ok('File %s did not contain xpath %s', file, xpathQuery);
    }

    return success;
}

module.exports = function(grunt) {
    grunt.registerMultiTask('xml_check', 'Check XML files for XPath and fail if they match', function() {
        var files = this.filesSrc;
        var options = this.options();
        var done = this.async();
        var success = true;
        var i;
        for (i = files.length - 1; i >= 0; i--) {
            grunt.verbose.ok('Checking %s', files[i]);
            success = success && processFile(grunt, files[i], options.xpathQuery);
        }

        done(success);
    });
};
