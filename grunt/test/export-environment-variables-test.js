var assert = require('chai').assert;
var exportEnvironmentVariables = require('../tasks/export-environment-variables');

describe('export-environment-variables', function() {
    var endOfLine = require('os').EOL;
    var mockGrunt = {};
    var actualContents = null;

    before(function() {
        mockGrunt.registerTask = function(taskName, taskDescription, taskFunction) {
            assert.equal('export-environment-variables', taskName);

            // run the task upon registration
            taskFunction();
        };

        mockGrunt.file = {
            write: function(fileName, fileContents) {
                actualContents = fileContents;
            }
        };

        mockGrunt.config = {
            data: {}
        };
    });

    it('should work on master branch', function() {
        var expectedContents = null;
        var mockConfig = {
            gitBranch: 'origin/master',
            semanticVersion: '1.2.3',
            buildNumber: '4',
            version: '1.2.3.4'
        };

        mockGrunt.config.data = mockConfig;
        exportEnvironmentVariables(mockGrunt);

        expectedContents = 'SEMANTIC_VERSION=1.2.3' + endOfLine +
        'GIT_BRANCH=origin/master' + endOfLine +
        'BRANCH=master' + endOfLine +
        'VERSION=1.2.3.4' + endOfLine +
        'FOLDER=1.2.3.4-master' + endOfLine;

        assert.equal(actualContents, expectedContents);
    });

    it('should work on a folder branch and convert the folder to lowercase', function() {
        var expectedContents = null;
        var mockConfig = {
            gitBranch: 'origin/feature/BZ-123',
            semanticVersion: '1.2.3',
            buildNumber: '4',
            version: '1.2.3.4'
        };

        mockGrunt.config.data = mockConfig;
        exportEnvironmentVariables(mockGrunt);

        expectedContents = 'SEMANTIC_VERSION=1.2.3' + endOfLine +
        'GIT_BRANCH=origin/feature/BZ-123' + endOfLine +
        'BRANCH=BZ-123' + endOfLine +
        'VERSION=1.2.3.4' + endOfLine +
        'FOLDER=1.2.3.4-bz-123' + endOfLine;

        assert.equal(actualContents, expectedContents);
    });

    it('should calculate the version when it is missing', function() {
        var expectedContents = null;
        var mockConfig = {
            gitBranch: 'origin/master',
            semanticVersion: '1.2.3',
            buildNumber: '4'
        };

        mockGrunt.config.data = mockConfig;
        exportEnvironmentVariables(mockGrunt);

        expectedContents = 'SEMANTIC_VERSION=1.2.3' + endOfLine +
        'GIT_BRANCH=origin/master' + endOfLine +
        'BRANCH=master' + endOfLine +
        'VERSION=1.2.3.4' + endOfLine +
        'FOLDER=1.2.3.4-master' + endOfLine;

        assert.equal(actualContents, expectedContents);
    });
});
