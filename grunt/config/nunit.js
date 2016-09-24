var path = require('path');

module.exports = function(grunt, config) {
    // get all Tests.dll files
    var p = 'src/BuzzStats*Tests/bin/' + config.projectConfiguration + '/*.Tests.dll';
    var files = grunt.file.expand(p);

    // all the nunit:* tasks will be stored in here
    var result = {};

    // aliases for simple tests
    var noIntegrationTasks = [];

    // aliases for integration tests
    var integrationTasks = [];

    // aliases for functional tests
    var functionalTasks = [];

    files.forEach(function(file) {
        var basename = path.basename(file);
        grunt.verbose.writeln('Found file: ' + file);

        // add simple test
        result['noIntegration' + basename] = {
            src: file,
            options: {
                excludeCategories: ['Integration', 'Functional'],
                dest: 'nunit-no-integration.xml',
                force: true
            }
        };

        noIntegrationTasks.push('nunit:noIntegration' + basename);

        // add integration test
        result['integration' + basename] = {
            src: file,
            options: {
                includeCategories: ['Integration'],
                dest: 'nunit-integration.xml',
                force: true
            }
        };

        integrationTasks.push('nunit:integration' + basename);

        // add functional test
        result['functional' + basename] = {
            src: file,
            options: {
                includeCategories: ['Functional'],
                dest: 'nunit-functional.xml',
                force: true
            }
        };

        functionalTasks.push('nunit:functional' + basename);
    });

    grunt.verbose.writeln(result);
    grunt.registerTask('nunitNoIntegration', noIntegrationTasks);
    grunt.registerTask('nunitIntegration', integrationTasks);
    grunt.registerTask('nunitFunctional', functionalTasks);
    return result;
};
