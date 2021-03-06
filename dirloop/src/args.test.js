const { expect } = require('chai');
const args = require('./args');

describe('args', () => {
  let oldArgV;

  beforeEach(() => {
    oldArgV = process.argv;
  });

  afterEach(() => {
    process.argv = oldArgV;
  });

  describe('--dir', () => {
    it('should default to current', () => {
      // arrange
      process.argv = ['node', 'index.js'];

      // act
      const result = args.parseArguments();

      // assert
      expect(result.dir).to.eql('.');
    });

    it('should read from parameter', () => {
      // arrange
      process.argv = ['node', 'index.js', '--dir', '../test'];

      // act
      const result = args.parseArguments();

      // assert
      expect(result.dir).to.eql('../test');
    });
  });

  describe('--dir-prefix', () => {
    it('should default to empty list', () => {
      // arrange
      process.argv = ['node', 'index.js'];

      // act
      const result = args.parseArguments();

      // assert
      expect(result.dirPrefix).to.eql([]);
    });

    it('should read from parameter', () => {
      // arrange
      process.argv = ['node', 'index.js', '--dir-prefix', 'pref'];

      // act
      const result = args.parseArguments();

      // assert
      expect(result.dirPrefix).to.eql(['pref']);
    });

    it('should read from multiple parameters', () => {
      // arrange
      process.argv = ['node', 'index.js', '--dir-prefix', 'pref,bar'];

      // act
      const result = args.parseArguments();

      // assert
      expect(result.dirPrefix).to.eql(['pref', 'bar']);
    });
  });

  describe('--has-file', () => {
    it('should default to undefined', () => {
      // arrange
      process.argv = ['node', 'index.js'];

      // act
      const result = args.parseArguments();

      // assert
      expect(result.hasFile).to.be.undefined;
    });

    it('should read from parameter', () => {
      // arrange
      process.argv = ['node', 'index.js', '--has-file', 'pom.xml'];

      // act
      const result = args.parseArguments();

      // assert
      expect(result.hasFile).to.eql('pom.xml');
    });
  });

  describe('--dry-run', () => {
    it('should default to undefined', () => {
      // arrange
      process.argv = ['node', 'index.js'];

      // act
      const result = args.parseArguments();

      // assert
      expect(result.dryRun).to.be.undefined;
    });

    it('should read from parameter', () => {
      // arrange
      process.argv = ['node', 'index.js', '--dry-run'];

      // act
      const result = args.parseArguments();

      // assert
      expect(result.dryRun).to.be.true;
    });
  });

  describe('--verbose', () => {
    it('should default to undefined', () => {
      // arrange
      process.argv = ['node', 'index.js'];

      // act
      const result = args.parseArguments();

      // assert
      expect(result.verbose).to.be.undefined;
    });

    it('should read from parameter', () => {
      // arrange
      process.argv = ['node', 'index.js', '--verbose'];

      // act
      const result = args.parseArguments();

      // assert
      expect(result.verbose).to.be.true;
    });
  });

  describe('--shell', () => {
    it('should default to falsy', () => {
      // arrange
      process.argv = ['node', 'index.js'];

      // act
      const result = args.parseArguments();

      // assert
      expect(result.shell).to.be.undefined;
    });

    it('should read from parameter', () => {
      // arrange
      process.argv = ['node', 'index.js', '--shell'];

      // act
      const result = args.parseArguments();

      // assert
      expect(result.shell).to.be.true;
    });
  });

  describe('args', () => {
    it('should default to empty', () => {
      // arrange
      process.argv = ['node', 'index.js'];

      // act
      const result = args.parseArguments();

      // assert
      expect(result.args).to.eql([]);
    });

    it('should read from parameter', () => {
      // arrange
      process.argv = ['node', 'index.js', '--', 'echo', '--hello'];

      // act
      const result = args.parseArguments();

      // assert
      expect(result.args).to.eql(['echo', '--hello']);
    });
  });

  describe('--eval-js', () => {
    it('should default to empty', () => {
      // arrange
      process.argv = ['node', 'index.js'];

      // act
      const result = args.parseArguments();

      // assert
      expect(result.evalJs).to.eql('');
    });

    it('should read from parameter', () => {
      // arrange
      process.argv = ['node', 'index.js', '--eval-js', 'var x = 42;'];

      // act
      const result = args.parseArguments();

      // assert
      expect(result.evalJs).to.eql('var x = 42;');
    });
  });

  describe('--has-json', () => {
    it('should default to empty', () => {
      // arrange
      process.argv = ['node', 'index.js'];

      // act
      const result = args.parseArguments();

      // assert
      expect(result.hasJson).to.eql('');
    });

    it('should read from parameter', () => {
      // arrange
      process.argv = ['node', 'index.js', '--has-json', 'package.json;devDependencies.eslint'];

      // act
      const result = args.parseArguments();

      // assert
      expect(result.hasJson).to.eql('package.json;devDependencies.eslint');
    });
  });

  describe('--set-json', () => {
    it('should default to empty', () => {
      // arrange
      process.argv = ['node', 'index.js'];

      // act
      const result = args.parseArguments();

      // assert
      expect(result.setJson).to.eql('');
    });

    it('should read from parameter', () => {
      // arrange
      process.argv = ['node', 'index.js', '--set-json', 'package.json;devDependencies.eslint'];

      // act
      const result = args.parseArguments();

      // assert
      expect(result.setJson).to.eql('package.json;devDependencies.eslint');
    });
  });

  describe('--csv', () => {
    it('should default to falsy', () => {
      // arrange
      process.argv = ['node', 'index.js'];

      // act
      const result = args.parseArguments();

      // assert
      expect(result.csv).to.be.undefined;
    });

    it('should read from parameter', () => {
      // arrange
      process.argv = ['node', 'index.js', '--csv'];

      // act
      const result = args.parseArguments();

      // assert
      expect(result.csv).to.be.true;
    });
  });

  describe('--line-count', () => {
    it('should default to falsy', () => {
      // arrange
      process.argv = ['node', 'index.js'];

      // act
      const result = args.parseArguments();

      // assert
      expect(result.lineCount).to.be.undefined;
    });

    it('should read from parameter', () => {
      // arrange
      process.argv = ['node', 'index.js', '--line-count'];

      // act
      const result = args.parseArguments();

      // assert
      expect(result.lineCount).to.be.true;
    });
  });
});
