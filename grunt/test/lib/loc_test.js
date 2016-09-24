var assert = require('chai').assert;
var loc = require('../../tasks/lib/loc');
var endOfLine = require('os').EOL;

describe('loc', function() {
    describe('fileLoc', function() {
        var content = 'abc' + endOfLine + 'def' + endOfLine + endOfLine + '// ghi';
        var result = loc.fileLoc(content);

        it('should measure all lines as loc', function() {
            assert.equal(4, result.loc);
        });

        it('should exclude empty lines as sloc', function() {
            assert.equal(3, result.sloc);
        });

        it('should exclude empty and commented lines as ssloc', function() {
            assert.equal(2, result.ssloc);
        });
    });

    describe('write', function() {
        it('should write the file', function() {
            // arrange
            var actualContents = '';
            var actualDest = '';

            var grunt = {
                file: {
                    write: function(dest, contents) {
                        actualDest = dest;
                        actualContents = contents;
                    }
                }
            };

            var dest = 'a.csv';

            // act
            loc.write(grunt, dest, 100, 80, 60);

            // assert
            assert.equal(dest, actualDest);
            assert.equal('LOC,SLOC,SSLOC' + endOfLine + '100,80,60' + endOfLine, actualContents);
        });
    });
});
