/* global browser */
var expect = require('chai').expect;

describe('hosts', function() {
    before(function() {
        browser.url('/Hosts.aspx');
        browser.waitForVisible('header');
    });

    it('should have correct title', function() {
        var title = browser.getTitle();
        expect(title).to.equal('ngeor.net | BuzzStats | Ιστότοποι');
    });

    it('should show popular hosts', function() {
        browser.waitForVisible('.js-host');
        var text = browser.getText('.js-host');
        expect(text).to.contain('πριν');
    });
});
