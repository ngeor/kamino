var gulp = require('gulp');
var pug = require('gulp-pug');
var less = require('gulp-less');
var clean = require('gulp-clean');
var data = require('gulp-data');
var webpack = require('webpack');
var webpackStream = require('webpack-stream');
var webpackConfig = require('./webpack.config.js');

var fs = require('fs');
var path = require('path');

var packageJson = JSON.parse(fs.readFileSync('package.json', 'utf8'));
var version = packageJson.version;

gulp.task('clean', function() {
    return gulp.src('bin/Debug', { read: false })
        .pipe(clean());
});

gulp.task('html', function() {
    return gulp.src(['templates/*.pug', '!templates/layout.pug'])
        .pipe(data(function(file) {
            return {
                basename: path.basename(file.path, '.pug')
            };
        }))
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

gulp.task('webpack', function() {
   return gulp.src('scripts/index.js')
       .pipe(webpackStream(webpackConfig, webpack))
       .pipe(gulp.dest('bin/Debug'));
});

gulp.task('watch', function() {
    gulp.watch('templates/*.pug', ['html']);
    gulp.watch('styles/*.less', ['css']);
});

gulp.task('default', ['html', 'css', 'webpack']);
