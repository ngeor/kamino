var gulp = require('gulp');
var pug = require('gulp-pug');
var less = require('gulp-less');
var fs = require('fs');
var packageJson = JSON.parse(fs.readFileSync('package.json', 'utf8'));
var version = packageJson.version;

gulp.task('html', function() {
    return gulp.src('templates/*.pug')
        .pipe(pug({
            pretty: true,
            data: {
                version: version
            }
        }))
        .pipe(gulp.dest('bin/Debug'));
});

gulp.task('css', function() {
    return gulp.src('styles/*.less')
        .pipe(less())
        .pipe(gulp.dest('bin/Debug/css'));
});

gulp.task('watch', function() {
    gulp.watch('templates/*.pug', ['html']);
    gulp.watch('styles/*.less', ['css']);
});

gulp.task('default', ['html', 'css']);
