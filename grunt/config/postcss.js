var stylelint = require('stylelint');
var stylelintReporter = require('postcss-reporter');
var scss = require('postcss-scss');

module.exports = {
    options: {
        syntax: scss,
        processors: [
            stylelint(),
            stylelintReporter({
                clearMessages: true,
                throwError: true
            })
        ]
    },
    dist: {
        files: [{
            src: ['<%= paths.webRoot %>/scss/**/*.scss']
        }]
    }
};
