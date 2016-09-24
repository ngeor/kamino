var expect = require('chai').expect;
var ensureEolAtEof = require('../tasks/ensure_eol_at_eof');

describe('ensure_eol_at_eof', function() {
    it('should add EOL to file that does not have it', function() {
        var taskFunction = null;
        var contents = '';
        var taskThis = {
            filesSrc: ['a.cs']
        };

        var grunt = {
            registerMultiTask: function(name, description, task) {
                taskFunction = task;
            },

            file: {
                read: function(filename) {
                    return filename + 'contents';
                },

                write: function(filename, buf) {
                    contents = filename + buf;
                }
            },

            util: {
                linefeed: '\n'
            },

            log: {
                debug: function() {},

                ok: function() {}
            }
        };

        ensureEolAtEof(grunt);
        taskFunction.call(taskThis);
        expect(contents).to.eql('a.csa.cscontents\n');
    });

    it('should not add EOL to file that has it', function() {
        var taskFunction = null;
        var contents = '';
        var taskThis = {
            filesSrc: ['a.cs']
        };

        var grunt = {
            registerMultiTask: function(name, description, task) {
                taskFunction = task;
            },

            file: {
                read: function(filename) {
                    return filename + 'contents\n';
                },

                write: function() {
                    throw new Error('should not be here');
                }
            },

            util: {
                linefeed: '\n'
            },

            log: {
                debug: function() {},

                ok: function() {}
            }
        };

        ensureEolAtEof(grunt);
        taskFunction.call(taskThis);
        expect(contents).to.eql('');
    });

    it('should not add EOL to empty file', function() {
        var taskFunction = null;
        var contents = '';
        var taskThis = {
            filesSrc: ['a.cs']
        };

        var grunt = {
            registerMultiTask: function(name, description, task) {
                taskFunction = task;
            },

            file: {
                read: function() {
                    return '';
                },

                write: function() {
                    throw new Error('should not be here');
                }
            },

            util: {
                linefeed: '\n'
            },

            log: {
                debug: function() {},

                ok: function() {}
            }
        };

        ensureEolAtEof(grunt);
        taskFunction.call(taskThis);
        expect(contents).to.eql('');
    });
});
