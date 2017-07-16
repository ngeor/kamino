var gulp = require('gulp');
var pug = require('gulp-pug');

gulp.task('html', function() {
    return gulp.src('templates/*.pug')
        .pipe(pug({ pretty: true }))
        .pipe(gulp.dest('bin/Debug'));
});

gulp.task('default', ['html']);
