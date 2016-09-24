module.exports = function(grunt) {
    var csprojFiles = grunt.file.expand('src/**/*.csproj');
    var csprojFilesSrcDest = {};
    csprojFiles.forEach(function(f) {
        csprojFilesSrcDest[f] = f;
    });

    return {
        options: {
            failIfMissing: false
        },

        csproj: {
            options: {
                replacements: [
                    {
                        xpath: '/Project/@ToolsVersion',
                        value: '12.0'
                    },

                    {
                        xpath: '//ProjectReference/Project',
                        value: function(node) {
                            return node.firstChild.nodeValue.toLowerCase();
                        }
                    }
                ]
            },

            files: csprojFilesSrcDest
        }
    };
};
