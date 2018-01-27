const expect = require('chai').expect;

describe('about', function() {
    before(function() {
        browser.url('/About.aspx');
        browser.waitForVisible('header');
    });

    it('should have correct title', function() {
        const title = browser.getTitle();
        expect(title).to.equal('ngeor.net | BuzzStats | Για το BuzzStats');
    });

    it('should show the crawler to be up', function() {
        const text = browser.getText('#ctl00_cphBody_lblServiceStatus');
        expect(text).to.equal('OK');
    });
});
