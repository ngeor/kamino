module.exports = function(grunt, config) {
    return {
        xdtWebConfig: {
            command: function() {
                if (!config.target || config.target === 'local') {
                    return '';
                }

                return 'xdt src/BuzzStats.Web/web.config ' +
                'src/BuzzStats.Web/web.<%= target %>.config ' +
                'dist/web/web.config';
            }
        },

        xdtCrawlerConfig: {
            command: function() {
                if (!config.target || config.target === 'local') {
                    return '';
                }

                return 'xdt src/BuzzStats.Crawler/app.config ' +
                'src/BuzzStats.Crawler/app.<%= target %>.config ' +
                'dist/crawler/BuzzStats.Crawler.exe.config';
            }
        }
    };
};
