module.exports = function(grunt) {
    return {
        options: {
            maximumLineLength: 128
        },

        local: {
            src: '<%= lintspaces.cs.src %>',
            options: {
                fix: grunt.option('fix')
            }
        },

        fix: {
            src: '<%= csharplint.local.src %>',
            options: {
                fix: true
            }
        },

        'fix-force': {
            src: '<%= csharplint.local.src %>',
            options: {
                fix: true,
                force: true
            }
        },

        'pre-commit': {
            src: '<%= csharplint.local.src %>',
            options: {
                toleratedFailures: 0
            }
        },

        ci: {
            src: '<%= csharplint.local.src %>',
            options: {
                force: true,
                reporterOutput: 'dist/csharplint.checkstyle.xml'
            }
        }
    };
};
