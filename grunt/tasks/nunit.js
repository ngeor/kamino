module.exports = function(grunt) {
    grunt.registerMultiTask('nunit', 'Run nunit tests', function() {
        var files = this.filesSrc;
        var options = this.options();
        var done = this.async();
        require('./lib/nunit')(grunt, files, options, done);
    });
};
