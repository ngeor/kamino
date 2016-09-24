module.exports = {
    distWeb: {
        expand: true,
        cwd: 'src/BuzzStats.Web',
        src: [
            '**/*.*',
            '!**/*.cs',
            '!**/*.csproj',
            '!xsp.sh',
            '!packages.config',
            '!obj/**/*.*',
            '!web.config',
            '!web.*.config'
        ],
        dest: 'dist/web/'
    },

    distCrawler: {
        nonull: true,
        expand: true,
        cwd: 'src/BuzzStats.Crawler/bin/<%= projectConfiguration %>',
        src: ['**/*.*'],
        dest: 'dist/crawler/'
    }
};
