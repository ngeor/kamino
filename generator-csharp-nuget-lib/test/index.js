/* eslint-disable class-methods-use-this, max-classes-per-file */
const path = require('path');
const fs = require('fs');
const helpers = require('yeoman-test');
const assert = require('yeoman-assert');
const filenameConvert = require('../app/filename_convert');
const readdirSyncRecursive = require('../app/readdir');

describe('app', () => {
  describe('files', () => {
    beforeEach(() => helpers.run(path.join(__dirname, '../app'))
      .withPrompts({
        name: 'SomeLib',
        description: 'SomeDescription',
        companyName: 'SomeCompany',
        indentationCharacter: 'spaces',
        user: 'githubuser',
      }));

    it('should generate expected files', () => {
      assert.file([
        '.gitignore',
        '.travis.yml',
        '.editorconfig',
        'README.md',

        'SomeLib.sln',

        'SomeLib/SomeLib.csproj',
        'SomeLib/SomeLib.nuspec',
        'SomeLib/Class1.cs',

        'SomeLib.Tests/SomeLib.Tests.csproj',
        'SomeLib.Tests/Class1Test.cs',
      ]);
    });

    /**
     * Removes GUIDs from files.
     */
    class GuidHandler {
      /**
       * Checks if the file might contain GUIDs.
       * @param {string} filename - The filename to check.
       * @returns {boolean} A value indicating whether the file is
       * expected to contain GUIDs.
       */
      shouldHandle(filename) {
        const ext = path.extname(filename);
        return ext === '.sln';
      }

      transformActualData(input) {
        return input.replace(
          /[0-9a-z]{8}-[0-9a-z]{4}-[0-9a-z]{4}-[0-9a-z]{4}-[0-9a-z]{12}/ig,
          '',
        );
      }

      transformExpectedData(input) {
        return this.transformActualData(input);
      }
    }

    /**
     * Converts years in files.
     */
    class YearHandler {
      /**
       * Checks if the file might contain the present year.
       * @param {string} filename - The filename to check.
       * @returns {boolean} A value indicating whether the file is
       * expected to contain the present year.
       */
      shouldHandle(filename) {
        const ext = path.extname(filename);
        return ext === '.csproj';
      }

      transformActualData(actualData) {
        return actualData.replace(
          new Date().toISOString().substr(0, 4),
          '2017',
        ); // the year of the test data
      }

      transformExpectedData(expectedData) {
        return expectedData;
      }
    }

    /**
     * Creates a unit test for every file.
     */
    function templateTests() {
      const expectedDataDirectory = path.join(__dirname, 'data');
      const expectedFiles = readdirSyncRecursive(expectedDataDirectory)
        .map((f) => path.relative(expectedDataDirectory, f));
      const handlers = [
        new GuidHandler(),
        new YearHandler(),
      ];
      expectedFiles.forEach((fixtureFile) => {
        const sourceFile = fixtureFile;
        const destFile = filenameConvert(fixtureFile, { name: 'SomeLib', templateName: 'MyLib' });
        it(`should map ${sourceFile} to ${destFile}`, () => {
          let actualData = fs.readFileSync(destFile, 'utf8');
          const expectedFile = path.join(expectedDataDirectory, sourceFile);
          let expectedData = fs.readFileSync(expectedFile, 'utf8');
          for (let index = 0; index < handlers.length; index += 1) {
            const handler = handlers[index];
            if (handler.shouldHandle(sourceFile)) {
              actualData = handler.transformActualData(actualData);
              expectedData = handler.transformExpectedData(expectedData);
            }
          }

          assert.textEqual(actualData, expectedData);
        });
      });
    }

    templateTests();
  });

  describe('with tabs', () => {
    beforeEach(() => helpers.run(path.join(__dirname, '../app'))
      .withPrompts({
        name: 'SomeLib',
        companyName: 'SomeCompany',
        indentationCharacter: 'tabs',
      }));

    it('should indent Class1.cs with tabs', () => {
      assert.fileContent('SomeLib/Class1.cs', /\tpublic class Class1/);
    });
  });

  describe('with spaces', () => {
    beforeEach(() => helpers.run(path.join(__dirname, '../app'))
      .withPrompts({
        name: 'SomeLib',
        companyName: 'SomeCompany',
        indentationCharacter: 'spaces',
      }));

    it('should indent Class1.cs with spaces', () => {
      assert.fileContent('SomeLib/Class1.cs', / {4}public class Class1/);
    });
  });
});
