/* global browser */
var expect = require('chai').expect;

describe('Access stories page', function() {
    before(function() {
        browser.url('/Stories.aspx');
        browser.waitForVisible('header');
    });

    it('should have correct title', function() {
        var title = browser.getTitle();
        expect(title).to.equal('ngeor.net | BuzzStats | Άρθρα');
    });
});
