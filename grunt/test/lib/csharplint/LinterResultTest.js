describe('csharplint', function() {
    describe('LinterResult', function() {
        var assert = require('chai').assert;
        var LinterResult = require('../../../tasks/lib/csharplint/LinterResult');

        it('should report success when created with the success method', function() {
            var obj = LinterResult.success();
            assert.equal(true, obj.isSuccess());
        });

        it('should not report failure when created with the success method', function() {
            var obj = LinterResult.success();
            assert.equal(false, obj.isFailure());
        });

        it('should return contents when created with the failure method', function() {
            var obj = LinterResult.failure('replacement');
            assert.equal('replacement', obj.getNewContents());
        });
    });
});
