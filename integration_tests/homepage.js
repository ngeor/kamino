/* global browser */
var expect = require('chai').expect;

describe('home', function() {
    before(function() {
        browser.url('/');
        browser.waitForVisible('header');
    });

    it('should have correct title', function() {
        var title = browser.getTitle();
        expect(title).to.equal('ngeor.net | BuzzStats');
    });

    it('should show the version in the footer', function() {
        var text = browser.getText('footer');
        expect(text).to.match(/version: [0-9]+(\.[0-9]+){3}$/);
    });

    it('should have recent activity', function() {
        var text = browser.getText('#sectionRecentActivity > .recent-activity');
        expect(text).to.contain('πριν');
    });
});
