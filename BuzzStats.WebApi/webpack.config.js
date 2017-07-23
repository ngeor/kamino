var path = require('path');

module.exports = {
    entry: [
        'babel-polyfill',
        './scripts/index.js'
    ],
    output: {
        filename: 'bundle.js',
        path: path.resolve(__dirname, 'bin/Debug')
    },
    module: {
        loaders: [{
            test: /\.js$/,
            exclude: /node_modules/,
            loader: 'babel-loader'
        }]
    }
};
