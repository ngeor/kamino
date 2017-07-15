var $ = require('jquery');
var readService = require('./ReadService').readService;
var ko = require('knockout');

/**
 * Populates the popular sites model.
 */
function populatePopularSites() {
    var model = {
        popularStories: ko.observableArray(),
        frequentStories: ko.observableArray(),
        scoreStories: ko.observableArray()
    };

    ko.applyBindings(model);

    /**
     * Stub error handler.
     */
    function onError() {
    }

    readService.getHostStats(0, 0, 0, 'VoteCount DESC', function(response) {
        model.popularStories(response.d);
    }, onError);

    readService.getHostStats(0, 0, 0, 'StoryCount DESC', function(response) {
        model.frequentStories(response.d);
    }, onError);

    /**
     * Updates the model based on user input.
     */
    function refreshScoreStories() {
        var minimumStories = parseInt($('input#txtMinimumStories').val());
        if (!minimumStories || minimumStories <= 0) {
            minimumStories = 1;
        }

        readService.getHostStats(0, 0, minimumStories, 'VoteStoryRatio DESC', function(response) {
            model.scoreStories(response.d);
        }, onError);
    }

    refreshScoreStories();

    $('#btnRefreshSitesScore').click(function() {
        refreshScoreStories();
        return false;
    });
}

$(function() {
    if (!document.getElementById('progressPopularSites')) {
        // not on hosts page, let's exit.
        return;
    }

    populatePopularSites();
});
