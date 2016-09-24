var assert = require('chai').assert;

describe('csharplint', function() {
    describe('FullyCommentedOutFileLinter', function() {
        it('should report when a file is entirely commented out', function() {
            var lines = [
                '// using System;'
            ];

            var linter = require('../../../tasks/lib/csharplint/FullyCommentedOutFileLinter');
            assert.deepEqual(
                linter(lines),
                [
                    {
                        msg: 'File is entirely commented out'
                    }
                ]);
        });
    });
});
