var GruntLogMock = require('./GruntLogMock');
var GruntFailMock = require('./GruntFailMock');

function GruntMock() {
    this.log = new GruntLogMock();
    this.verbose = new GruntLogMock();
    this.fail = new GruntFailMock();
    this.util = {
        spawn: function() {
        }
    };
}

module.exports = GruntMock;
