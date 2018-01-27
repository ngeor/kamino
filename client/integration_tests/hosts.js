const expect = require('chai').expect;

describe('hosts', function() {
    before(function() {
        browser.url('/Hosts.aspx');
        browser.waitForVisible('header');
    });

    it('should have correct title', function() {
        const title = browser.getTitle();
        expect(title).to.equal('ngeor.net | BuzzStats | Ιστότοποι');
    });

    it('should show popular hosts', function() {
        browser.waitForVisible('.js-host');
        const text = browser.getText('.js-host');
        expect(text).to.contain('πριν');
    });
});
