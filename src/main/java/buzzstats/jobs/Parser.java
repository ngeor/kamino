package buzzstats.jobs;

import java.time.Clock;
import java.time.LocalDateTime;
import org.jsoup.nodes.Element;

import buzzstats.db.ThingEntity;

/**
 * Parses the HTML of a page.
 */
public class Parser {
    /**
     * Parses the HTML of a page.
     * @param thingElement The HTML element.
     * @return The parsed story.
     */
    public ThingEntity parse(Element thingElement) {
        Element storyLink = thingElement.selectFirst("a.storylink");

        String title = storyLink.text();
        String url = storyLink.attr("href");

        Element nextRow = thingElement.nextElementSibling();
        Element subText = nextRow.selectFirst("td.subtext");
        Element scoreElement = subText.selectFirst(".score"); // 100 points
        if (scoreElement == null) {
            // if the score is null, it is most likely an ad
            return null;
        }

        int score = parseScore(scoreElement.text());

        Element byUser = subText.selectFirst(".hnuser");
        String username = byUser.text();

        Element ageElement = subText.selectFirst(".age a"); // 2 hours ago, 55 minutes ago
        LocalDateTime age = parseAge(ageElement.text());
        String internalUrl = ageElement.attr("href");

        Element commentsElement = subText.children().last(); // 181 comments, discuss
        int comments = parseComments(commentsElement.text());

        ThingEntity thingEntity = new ThingEntity();
        thingEntity.setTitle(title);
        thingEntity.setUrl(url);
        thingEntity.setScore(score);
        thingEntity.setUsername(username);
        thingEntity.setComments(comments);
        thingEntity.setPublishedAt(age);
        thingEntity.setInternalUrl(internalUrl);
        thingEntity.setCreatedAt(LocalDateTime.now(Clock.systemUTC()));
        thingEntity.setLastModifiedAt(LocalDateTime.now(Clock.systemUTC()));
        thingEntity.setLastCheckedAt(LocalDateTime.now(Clock.systemUTC()));
        return thingEntity;
    }

    private int parseScore(String score) {
        String[] parts = score.split(" ", 2);
        if (parts.length != 2 || !"points".equals(parts[1])) {
            throw new IllegalArgumentException("Cannot parse score " + score);
        }

        return Integer.parseInt(parts[0]);
    }

    private LocalDateTime parseAge(String age) {
        String[] parts = age.split(" ");
        int amount = Integer.parseInt(parts[0]);
        if ("minutes".equals(parts[1]) || "minute".equals(parts[1])) {
            return LocalDateTime.now(Clock.systemUTC()).minusMinutes(amount);
        } else if ("hours".equals(parts[1]) || "hour".equals(parts[1])) {
            return LocalDateTime.now(Clock.systemUTC()).minusHours(amount);
        } else if ("days".equals(parts[1]) || "day".equals(parts[1])) {
            return LocalDateTime.now(Clock.systemUTC()).minusDays(amount);
        } else {
            throw new IllegalArgumentException("Unsupported unit " + age);
        }
    }

    private int parseComments(String comments) {
        if ("discuss".equals(comments)) {
            return 0;
        }

        String[] parts = comments.split(" ");
        return Integer.parseInt(parts[0]);
    }
}
