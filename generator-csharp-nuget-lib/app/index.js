const Generator = require('yeoman-generator');
const path = require('path');
const readdirSyncRecursive = require('./readdir');
const convertFilename = require('./filename_convert');
const buildOptions = require('./build_options');
const copier = require('./copier');

module.exports = class extends Generator {
  async prompting() {
    this.props = await this.prompt([
      {
        type: 'input',
        name: 'name',
        message: 'Your project name',
        default: this.appname, // default to current folder name
        store: true,
      },
      {
        type: 'input',
        name: 'description',
        message: 'Description of the project',
        store: true,
      },
      {
        type: 'input',
        name: 'companyName',
        message: 'Company name (for copyright fields)',
        store: true,
      },
      {
        type: 'list',
        name: 'indentationCharacter',
        message: 'Indentation with tabs or spaces?',
        choices: [
          'tabs',
          'spaces',
        ],
        default: 'spaces',
        store: true,
      },
      {
        type: 'input',
        name: 'user',
        message: 'GitHub username (for badges, URLs, etc)',
        store: true,
      },
      {
        type: 'input',
        name: 'version',
        message: 'Semantic version',
        default: '0.1.0',
        store: true,
      },
    ]);
  }

  writing() {
    const options = buildOptions(this.props);
    const copyFn = copier.buildCopier(this.fs, options, this.props.indentationCharacter);
    const sourceRoot = this.sourceRoot();
    const files = readdirSyncRecursive(sourceRoot);
    const filenameConvertOptions = {
      name: options.name,
      templateName: 'MyLib',
    };

    files.forEach((file) => {
      const relativeFile = path.relative(sourceRoot, file);
      const relativeDestination = convertFilename(relativeFile, filenameConvertOptions);
      copyFn(file, this.destinationPath(relativeDestination));
    });
  }
};
