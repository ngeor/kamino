/**
 * Export Environment Variables grunt task.
 * @module tasks/export-environment-variables
 */
module.exports = function(grunt) {
    /**
     * Gets the simple branch name.
     */
    function getSimpleBranchName(fullBranchName) {
        var idx = fullBranchName.lastIndexOf('/');
        return fullBranchName.substring(idx + 1);
    }

    grunt.registerTask('export-environment-variables', 'Export environment variables into a file', function() {
        // jscs:disable requireDotNotation
        /* jshint -W069 */

        var config = grunt.config.data;
        var endOfLine = require('os').EOL;

        // SEMANTIC_VERSION=1.5.12
        var txt = 'SEMANTIC_VERSION=' + config.semanticVersion + endOfLine;

        var simpleBranchName = getSimpleBranchName(config.gitBranch);
        var version = config.version || (config.semanticVersion + '.' + config.buildNumber);

        // GIT_BRANCH=origin/release/something
        txt += 'GIT_BRANCH=' + config.gitBranch + endOfLine;

        // BRANCH=something
        txt += 'BRANCH=' + simpleBranchName + endOfLine;

        // VERSION=1.5.12.66
        txt += 'VERSION=' + version + endOfLine;

        // FOLDER=1.5.12.66-release-something
        txt += 'FOLDER=' + version + '-' + simpleBranchName.toLowerCase() + endOfLine;

        // jscs:enable requireDotNotation
        /* jshint +W069 */

        grunt.file.write('dist/version.txt', txt);
    });
};
