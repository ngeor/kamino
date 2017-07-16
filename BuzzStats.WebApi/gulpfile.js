var gulp = require('gulp');
var pug = require('gulp-pug');
var less = require('gulp-less');

gulp.task('html', function() {
    return gulp.src('templates/*.pug')
        .pipe(pug({ pretty: true }))
        .pipe(gulp.dest('bin/Debug'));
});

gulp.task('css', function() {
    return gulp.src('styles/*.less')
        .pipe(less())
        .pipe(gulp.dest('bin/Debug/css'));
});

gulp.task('default', ['html', 'css']);
