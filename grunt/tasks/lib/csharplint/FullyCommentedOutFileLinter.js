module.exports = function(lines) {
    var i;
    var line;
    for (i = 0; i < lines.length; i++) {
        line = lines[i].trim();
        if (line && line.indexOf('//') !== 0) {
            return [];
        }
    }

    return [{
        msg: 'File is entirely commented out'
    }];
};
