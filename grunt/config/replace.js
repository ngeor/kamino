module.exports = function(grunt, config) {
    var result = {
        assemblyInfo: {
            src: 'src/**/AssemblyInfo.cs',
            overwrite: true,
            replacements: [{
                from: /[0-9]+\.[0-9]+\.(\*|([0-9]+\.[0-9]+))/g,
                to: '<%= version %>'
            }]
        }
    };

    if (!config.version || config.version === '0.0.0.0') {
        result.assemblyInfo.replacements = [];
    }

    return result;
};
