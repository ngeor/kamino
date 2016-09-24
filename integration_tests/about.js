/* global browser */
var expect = require('chai').expect;

describe('about', function() {
    before(function() {
        browser.url('/About.aspx');
        browser.waitForVisible('header');
    });

    it('should have correct title', function() {
        var title = browser.getTitle();
        expect(title).to.equal('ngeor.net | BuzzStats | Για το BuzzStats');
    });

    it('should show the crawler to be up', function() {
        var text = browser.getText('#ctl00_cphBody_lblServiceStatus');
        expect(text).to.equal('OK');
    });
});
