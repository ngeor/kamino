function WindowsProvider() {}

WindowsProvider.prototype.getBinPath = function() {
    return 'C:\\Program Files (x86)\\NUnit.org\\nunit-console\\nunit3-console.exe';
};

WindowsProvider.prototype.excludeCategories = function(categories) {
    return '--where:cat!=' + categories;
};

WindowsProvider.prototype.includeCategories = function(categories) {
    return '--where:cat==' + categories;
};

WindowsProvider.prototype.outputFile = function(file) {
    return '--output=' + file;
};

WindowsProvider.prototype.additionalArguments = function() {
    return [];
};

function LinuxProvider() {}

LinuxProvider.prototype.getBinPath = function() {
    return 'nunit-console4';
};

LinuxProvider.prototype.excludeCategories = function(categories) {
    return '-exclude=' + categories;
};

LinuxProvider.prototype.includeCategories = function(categories) {
    return '-include=' + categories;
};

LinuxProvider.prototype.outputFile = function(file) {
    return '-xml=' + file;
};

LinuxProvider.prototype.additionalArguments = function() {
    return ['-noshadow', '-nothread'];
};

module.exports = function(grunt, files, options, done) {
    var path = require('path');
    var isWin = /^win/.test(process.platform);
    var binPath;
    var provider;
    var dirname;
    var args = [];
    var additionalArguments;
    var i;

    if (isWin) {
        provider = new WindowsProvider();
    } else {
        provider = new LinuxProvider();
    }

    // start with the binary
    binPath = provider.getBinPath();

    // append file and cd if needed
    files.forEach(function(file) {
        dirname = path.dirname(file);
        if (dirname && dirname !== '.') {
            args.push(path.basename(file));
        } else {
            args.push(file);
        }
    });

    if (options.excludeCategories) {
        args.push(provider.excludeCategories(options.excludeCategories));
    }

    if (options.includeCategories) {
        args.push(provider.includeCategories(options.includeCategories));
    }

    if (options.dest) {
        args.push(provider.outputFile(options.dest));
    }

    additionalArguments = provider.additionalArguments();
    for (i = 0; i < additionalArguments.length; i++) {
        args.push(additionalArguments[i]);
    }

    grunt.verbose.ok('cd ' + dirname);
    grunt.verbose.ok(binPath);
    grunt.verbose.ok(args);

    grunt.util.spawn({
        cmd: binPath,
        args: args,
        opts: {
            stdio: 'inherit',
            cwd: dirname
        }
    }, function(error, result, code) {
        done(!code || !!options.force);
    });
};
