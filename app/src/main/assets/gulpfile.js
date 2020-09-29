const gulp = require('gulp');
const minify = require('gulp-minify');

gulp.task('minify', async () => {
    gulp.src('./websocketBridge.js')
        .pipe(minify({
            ext: {
                min: '.min.js'
            }
        }))
        .pipe(gulp.dest('./'));
})