/**
 * Represents the result of a linter operation.
 * @constructor
 * @param {boolean} success - was the linting successful or not
 * @param {String} newContents - optional modified file contents of the linting operation
 */
function LinterResult(success, newContents) {
    this.success = success;
    this.newContents = newContents;
}

LinterResult.prototype.isSuccess = function isSuccess() {
    return this.success;
};

LinterResult.prototype.isFailure = function isFailure() {
    return !this.success;
};

LinterResult.prototype.hasNewContents = function hasNewContents() {
    return !!this.newContents;
};

LinterResult.prototype.getNewContents = function getNewContents() {
    return this.newContents;
};

LinterResult.success = function() {
    return new LinterResult(true);
};

LinterResult.failure = function(newContents) {
    return new LinterResult(false, newContents);
};

module.exports = LinterResult;
