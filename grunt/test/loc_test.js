var assert = require('chai').assert;
var loc = require('../tasks/loc');

describe('loc', function() {
    it('should register a multitask', function() {
        var called = false;
        var grunt = {
            registerMultiTask: function() {
                called = true;
            }
        };

        loc(grunt);
        assert.isTrue(called);
    });
});
