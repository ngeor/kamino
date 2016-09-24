var assert = require('chai').assert;
var GruntMock = require('../mocks/GruntMock');
var sinon = require('sinon');

describe('nunit', function() {
    var mockGrunt;
    var nunit;
    var done;
    var mockGruntUtil;
    var spawn;

    beforeEach(function() {
        mockGrunt = new GruntMock();
        nunit = require('../../tasks/lib/nunit');
        mockGruntUtil = sinon.mock(mockGrunt.util);
        spawn = mockGruntUtil.expects('spawn');
        done = sinon.spy();
    });

    it('should not fail when nunit exits with zero', function() {
        nunit(mockGrunt, ['a.dll'], {}, done);
        spawn.yield(null, null, 0);
        assert.isTrue(done.called);
        assert.isTrue(done.getCall(0).args[0]);
    });

    it('should fail when nunit exits with non-zero', function() {
        nunit(mockGrunt, ['a.dll'], {}, done);
        spawn.yield(null, null, 1);
        assert.isTrue(done.called);
        assert.isFalse(done.getCall(0).args[0]);
    });

    it('should change directory when assembly has a relative path', function() {
        nunit(mockGrunt, ['abc/a.dll'], { });
        assert.equal('abc', spawn.getCall(0).args[0].opts.cwd);
    });

    it('should not change directory when assembly is in the dot folder', function() {
        nunit(mockGrunt, ['./a.dll'], { });
        assert.equal('.', spawn.getCall(0).args[0].opts.cwd);
    });

    it('should not change directory when assembly is in the current folder', function() {
        nunit(mockGrunt, ['a.dll'], { });
        assert.equal('.', spawn.getCall(0).args[0].opts.cwd);
    });

    it('should have assembly file as first argument', function() {
        nunit(mockGrunt, ['abc/a.dll'], { });
        assert.equal('a.dll', spawn.getCall(0).args[0].args[0]);
    });

    describe('when on windows', function() {
        var oldPlatform;

        before(function() {
            oldPlatform = process.platform;
            Object.defineProperty(process, 'platform', {
                value: 'win32'
            });
        });

        after(function() {
            Object.defineProperty(process, 'platform', {
                value: oldPlatform
            });
        });

        it('should call nunit-console.exe', function() {
            nunit(mockGrunt, ['a.dll'], {});
            assert.equal(
            'C:\\Program Files (x86)\\NUnit.org\\nunit-console\\nunit3-console.exe',
            spawn.getCall(0).args[0].cmd
            );
        });

        it('should exclude categories', function() {
            nunit(
            mockGrunt,
            ['a.dll'],
                {
                    excludeCategories: [
                        'Integration'
                    ]
                });

            assert.equal(
            '--where:cat!=Integration',
            spawn.getCall(0).args[0].args[1]
            );
        });

        it('should include categories', function() {
            nunit(
            mockGrunt,
            ['a.dll'],
                {
                    includeCategories: [
                        'Integration'
                    ]
                });

            assert.equal(
                '--where:cat==Integration',
                spawn.getCall(0).args[0].args[1]
            );
        });

        it('should specify output file', function() {
            nunit(
            mockGrunt,
            ['a.dll'],
                {
                    dest: [
                        'out.xml'
                    ]
                });

            assert.equal(
                '--output=out.xml',
                spawn.getCall(0).args[0].args[1]
            );
        });
    });

    describe('when on linux', function() {
        var oldPlatform;

        before(function() {
            oldPlatform = process.platform;
            Object.defineProperty(process, 'platform', {
                value: 'linux'
            });
        });

        after(function() {
            Object.defineProperty(process, 'platform', {
                value: oldPlatform
            });
        });

        it('should call nunit-console4', function() {
            nunit(mockGrunt, ['a.dll'], {});

            assert.equal(
            'nunit-console4',
            spawn.getCall(0).args[0].cmd
            );
        });

        it('should exclude categories', function() {
            nunit(
                mockGrunt,
                ['a.dll'],
                {
                    excludeCategories: [
                        'Integration'
                    ]
                });

            assert.equal(
                '-exclude=Integration',
                spawn.getCall(0).args[0].args[1]
            );
        });

        it('should include categories', function() {
            nunit(
                mockGrunt,
                ['a.dll'],
                {
                    includeCategories: [
                        'Integration'
                    ]
                });

            assert.equal(
                '-include=Integration',
                spawn.getCall(0).args[0].args[1]
            );
        });

        it('should specify output file', function() {
            nunit(
                mockGrunt,
                ['a.dll'],
                {
                    dest: [
                        'out.xml'
                    ]
                });

            assert.equal(
                '-xml=out.xml',
                spawn.getCall(0).args[0].args[1]
            );
        });
    });
});
