import { parseDate, agoString } from '../src/date';
import { expect } from 'chai';

describe('date', () => {
    describe('parseDate', () => {
        it('should parse date', () => {
            const date = parseDate('2017-06-07T18:29:38');
            expect(date).to.eql(new Date(2017, 5, 7, 18, 29, 38).getTime());
        });
    });

    describe('agoString', () => {
        it('should support milliseconds', () => {
            expect(agoString(1)).to.eql('a few seconds ago');
        });

        it('should support seconds', () => {
            expect(agoString(2000)).to.eql('a few seconds ago');
        });

        it('should support minute', () => {
            expect(agoString(1000 * 60)).to.eql('1 minute ago');
        });

        it('should support minutes', () => {
            expect(agoString(1000 * 60 * 2)).to.eql('2 minutes ago');
        });

        it('should support hour', () => {
            expect(agoString(1000 * 60 * 60)).to.eql('1 hour ago');
        });

        it('should support hours', () => {
            expect(agoString(1000 * 60 * 60 * 2)).to.eql('2 hours ago');
        });

        it('should support day', () => {
            expect(agoString(1000 * 60 * 60 * 24)).to.eql('1 day ago');
        });

        it('should support days', () => {
            expect(agoString(1000 * 60 * 60 * 24 * 3)).to.eql('3 days ago');
        });

        it('should support week', () => {
            expect(agoString(1000 * 60 * 60 * 24 * 7)).to.eql('1 week ago');
        });

        it('should support weeks', () => {
            expect(agoString(1000 * 60 * 60 * 24 * 7 * 2)).to.eql('2 weeks ago');
        });

        it('should support month', () => {
            expect(agoString(1000 * 60 * 60 * 24 * 30)).to.eql('1 month ago');
        });

        it('should support months', () => {
            expect(agoString(1000 * 60 * 60 * 24 * 30 * 4)).to.eql('4 months ago');
        });
    });
});
