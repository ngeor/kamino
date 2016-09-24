var path = require('path');

function semanticVersion(grunt) {
    var result = process.env.SEMANTIC_VERSION;
    if (!result) {
        result = grunt.file.readJSON('package.json').version;
    }

    return result;
}

function buildNumber() {
    return process.env.BUILD_NUMBER || '0';
}

/**
 * Gets the project version, when running in CI.
 */
function ciVersion(grunt) {
    return process.env.VERSION || (semanticVersion(grunt) + '.' + buildNumber());
}

module.exports = function(grunt) {
    var environments;
    var target;
    var cfg = {};
    var packageJson = grunt.file.readJSON('package.json');

    environments = grunt.file.readJSON('grunt/config/environments.json');
    target = grunt.option('target') || 'local';
    cfg = {
        // Metadata
        pkg: packageJson,

        paths: {
            dist: 'dist/',
            jsCoverage: 'dist/js-coverage/',
            webRoot: 'src/BuzzStats.Web/'
        },

        semanticVersion: semanticVersion(grunt),
        buildNumber: buildNumber(),
        gitBranch: process.env.GIT_BRANCH || 'origin/master',
        version: process.env.VERSION || ciVersion(grunt),
        versionFolder: process.env.FOLDER || (ciVersion(grunt) + '-local'),
        projectConfiguration: grunt.option('projectConfiguration') || 'Debug',
        target: target,
        environment: environments[target]
    };

    grunt.loadTasks('grunt/tasks');

    // display execution time of grunt tasks
    require('time-grunt')(grunt);

    // load all grunt configs
    require('load-grunt-config')(grunt, {
        configPath: path.join(process.cwd(), 'grunt/config'),
        data: cfg
    });
};
