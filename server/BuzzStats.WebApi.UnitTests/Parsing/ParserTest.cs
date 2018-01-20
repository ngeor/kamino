using System;
using System.Linq;
using BuzzStats.Parsing;
using BuzzStats.Parsing.DTOs;
using NodaTime;
using NodaTime.Extensions;
using NodaTime.Testing;
using NUnit.Framework;

namespace BuzzStats.WebApi.UnitTests.Parsing
{
    [TestFixture]
    public class ParserTest
    {
        public enum KnownStoryCategory
        {
            Unknown = 0,
            Media = 1,
            Misc,
            Blogs,
            English,
            Tech
        }

        private Parser _parser;
        private IClock _clock;

        [SetUp]
        public void SetUp()
        {
            _clock = new FakeClock(Instant.FromUtc(2017, 7, 30, 19, 31));
            _parser = new Parser(_clock);
        }

        [Test]
        public void Bug_Story59806()
        {
            string storyHtml = LoadTestData("bug59806");

            var story = _parser.ParseStoryPage(storyHtml, 59806);
            Assert.IsNotNull(story);
            Assert.AreEqual(59806, story.StoryId);
            Assert.AreEqual("Λίγη ακόμα αυτορρύθμιση", story.Title);
            Assert.AreEqual(10, story.Voters.Count());
            Assert.AreEqual("admin", story.Username, "Username");
            Assert.AreEqual((int) KnownStoryCategory.Misc, story.Category);
        }

        [Test]
        public void ParseBurriedComment()
        {
            string html = LoadTestData("BurriedComment");
            var story = _parser.ParseStoryPage(html, 65483);

            Assert.IsNotNull(story);
            Assert.IsNotNull(story.Comments);
            Assert.AreEqual(14, story.Comments.Count());

            AssertParsedComment(story.Comments.ElementAt(0), "talos", 10, 13, false, "1.16:00", 7);

            AssertParsedComment(story.Comments.ElementAt(0).Comments.ElementAt(0), "neTpen", 9, 0, false, "1.16:00");
            AssertParsedComment(story.Comments.ElementAt(0).Comments.ElementAt(1), "talos", 5, 3, false, "1.14:00");
            AssertParsedComment(story.Comments.ElementAt(0).Comments.ElementAt(2), "kensai", 3, 5, false, "1.14:00");
            AssertParsedComment(story.Comments.ElementAt(0).Comments.ElementAt(3), "cinto", 8, 2, false, "1.13:00");
            AssertParsedComment(story.Comments.ElementAt(0).Comments.ElementAt(4), "noir", 7, 0, false, "1.13:00");
            AssertParsedComment(story.Comments.ElementAt(0).Comments.ElementAt(5), "talos", 4, 1, false, "1.11:00");
            AssertParsedComment(story.Comments.ElementAt(0).Comments.ElementAt(6), "contrabando", 1, 2, false, "11:00");

            AssertParsedComment(story.Comments.ElementAt(1), "dkamen", 12, 5, false, "1.15:00", 2);
            AssertParsedComment(story.Comments.ElementAt(1).Comments.ElementAt(0), "BlondeElena", 8, 3, false,
                "1.14:00");
            AssertParsedComment(story.Comments.ElementAt(1).Comments.ElementAt(1), "dkamen", 2, 1, false, "1.09:00");

            AssertParsedComment(story.Comments.ElementAt(2), "seagazing", 6, 0, false, "1.15:00", 2);
            AssertParsedComment(story.Comments.ElementAt(2).Comments.ElementAt(0), "talos", 5, 3, false, "1.14:00");
            AssertParsedComment(story.Comments.ElementAt(2).Comments.ElementAt(1), "kensai", 3, 6, false, "1.14:00");

            AssertParsedComment(story.Comments.ElementAt(3), "alberich", 8, 4, false, "1.11:00");

            AssertParsedComment(story.Comments.ElementAt(4), "kensai", 2, 0, false, "1.11:00", 1);
            AssertParsedComment(story.Comments.ElementAt(4).Comments.ElementAt(0), "talos", 7, 1, false, "1.10:00");

            AssertParsedComment(story.Comments.ElementAt(5), "kensai", 0, 5, false, "1.10:00", 4);
            AssertParsedComment(story.Comments.ElementAt(5).Comments.ElementAt(0), "talos", 6, 2, false, "1.10:00");
            AssertParsedComment(story.Comments.ElementAt(5).Comments.ElementAt(1), "kensai", 0, 0, true, "1.10:00");
            AssertParsedComment(story.Comments.ElementAt(5).Comments.ElementAt(2), "dytistonniptiron", 8, 0, false,
                "1.10:00");
            AssertParsedComment(story.Comments.ElementAt(5).Comments.ElementAt(3), "alberich", 5, 3, false, "23:00");

            AssertParsedComment(story.Comments.ElementAt(6), "kensai", 0, 4, false, "1.07:00", 1);
            AssertParsedComment(story.Comments.ElementAt(6).Comments.ElementAt(0), "dytistonniptiron", 4, 0, false,
                "1.06:00");

            AssertParsedComment(story.Comments.ElementAt(7), "kensai", 3, 4, false, "21:00");

            AssertParsedComment(story.Comments.ElementAt(8), "kensai", 1, 1, false, "21:00", 1);
            AssertParsedComment(story.Comments.ElementAt(8).Comments.ElementAt(0), "alberich", 5, 4, false, "20:00");

            AssertParsedComment(story.Comments.ElementAt(9), "kensai", 2, 4, false, "12:00", 1);
            AssertParsedComment(story.Comments.ElementAt(9).Comments.ElementAt(0), "alberich", 4, 3, false, "12:00");

            AssertParsedComment(story.Comments.ElementAt(10), "kensai", 1, 3, false, "11:00", 1);
            AssertParsedComment(story.Comments.ElementAt(10).Comments.ElementAt(0), "alberich", 4, 1, false, "10:00");

            AssertParsedComment(story.Comments.ElementAt(11), "kensai", 2, 1, false, "10:00", 1);
            AssertParsedComment(story.Comments.ElementAt(11).Comments.ElementAt(0), "alberich", 1, 0, false, "08:00");

            AssertParsedComment(story.Comments.ElementAt(12), "kensai", 0, 0, false, "09:00", 1);
            AssertParsedComment(story.Comments.ElementAt(12).Comments.ElementAt(0), "alberich", 0, 0, false, "08:00");

            AssertParsedComment(story.Comments.ElementAt(13), "you", 0, 0, false, "00:17");
        }

        [Test]
        public void ParseDateTime()
        {
            Assert.AreEqual(null, Parser.ToTimeSpan("πριν abc"));

            Assert.AreEqual(TimeSpan.Zero, Parser.ToTimeSpan("πριν λίγα δευτερόλεπτα"));
            Assert.AreEqual(TimeSpan.Zero, Parser.ToTimeSpan("πριν  λίγα δευτερόλεπτα"));

            Assert.AreEqual(TimeSpan.FromMinutes(1), Parser.ToTimeSpan("πριν 1 λεπτό"));
            Assert.AreEqual(TimeSpan.FromMinutes(8), Parser.ToTimeSpan("πριν 8 λεπτά"));

            Assert.AreEqual(TimeSpan.FromMinutes(60), Parser.ToTimeSpan("πριν 1 ώρα"));
            Assert.AreEqual(TimeSpan.FromMinutes(61), Parser.ToTimeSpan("πριν 1 ώρα 1 λεπτό"));
            Assert.AreEqual(TimeSpan.FromMinutes(69), Parser.ToTimeSpan("πριν 1 ώρα 9 λεπτά"));
            Assert.AreEqual(TimeSpan.FromMinutes(77), Parser.ToTimeSpan("πριν 1 ώρα 17 λεπτά"));

            Assert.AreEqual(TimeSpan.FromMinutes(300), Parser.ToTimeSpan("πριν 5 ώρες"));
            Assert.AreEqual(TimeSpan.FromMinutes(301), Parser.ToTimeSpan("πριν 5 ώρες 1 λεπτό"));
            Assert.AreEqual(TimeSpan.FromMinutes(359), Parser.ToTimeSpan("πριν 5 ώρες 59 λεπτά"));

            Assert.AreEqual(TimeSpan.FromMinutes(24 * 60), Parser.ToTimeSpan("πριν 1 μέρα"));
            Assert.AreEqual(TimeSpan.FromMinutes(24 * 60 + 1), Parser.ToTimeSpan("πριν 1 μέρα 1 λεπτό"));
            Assert.AreEqual(TimeSpan.FromMinutes(24 * 60 + 42), Parser.ToTimeSpan("πριν 1 μέρα 42 λεπτά"));
            Assert.AreEqual(TimeSpan.FromMinutes(24 * 60 + 61), Parser.ToTimeSpan("πριν 1 μέρα 1 ώρα 1 λεπτό"));
            Assert.AreEqual(TimeSpan.FromMinutes(24 * 60 + 69), Parser.ToTimeSpan("πριν 1 μέρα 1 ώρα 9 λεπτά"));

            Assert.AreEqual(TimeSpan.FromMinutes(24 * 60 + 300), Parser.ToTimeSpan("πριν 1 μέρα 5 ώρες"));
            Assert.AreEqual(TimeSpan.FromMinutes(24 * 60 + 301), Parser.ToTimeSpan("πριν 1 μέρα 5 ώρες 1 λεπτό"));
            Assert.AreEqual(TimeSpan.FromMinutes(24 * 60 + 342),
                Parser.ToTimeSpan("πριν 1 μέρα 5 ώρες 42 λεπτά"));

            Assert.AreEqual(TimeSpan.FromMinutes(21 * 24 * 60), Parser.ToTimeSpan("πριν 21 μέρες"));
            Assert.AreEqual(TimeSpan.FromMinutes(21 * 24 * 60 + 1), Parser.ToTimeSpan("πριν 21 μέρες 1 λεπτό"));
            Assert.AreEqual(TimeSpan.FromMinutes(21 * 24 * 60 + 42), Parser.ToTimeSpan("πριν 21 μέρες 42 λεπτά"));
            Assert.AreEqual(TimeSpan.FromMinutes(21 * 24 * 60 + 61),
                Parser.ToTimeSpan("πριν 21 μέρες 1 ώρα 1 λεπτό"));
            Assert.AreEqual(TimeSpan.FromMinutes(21 * 24 * 60 + 69),
                Parser.ToTimeSpan("πριν 21 μέρες 1 ώρα 9 λεπτά"));

            Assert.AreEqual(TimeSpan.FromMinutes(21 * 24 * 60 + 300), Parser.ToTimeSpan("πριν 21 μέρες 5 ώρες"));
            Assert.AreEqual(TimeSpan.FromMinutes(21 * 24 * 60 + 301),
                Parser.ToTimeSpan("πριν 21 μέρες 5 ώρες 1 λεπτό"));
            Assert.AreEqual(TimeSpan.FromMinutes(21 * 24 * 60 + 342),
                Parser.ToTimeSpan("πριν 21 μέρες 5 ώρες 42 λεπτά"));
        }

        [Test]
        public void ParseHomepageThroughVodafone()
        {
            string html = LoadTestData("homepage_vodafone");
            Assert.IsNotNull(html);
            var ids = _parser.ParseListingPage(html).ToArray();
            Assert.Greater(ids.Length, 0);
        }

        [Test]
        public void ParseRemovedStory()
        {
            var story = _parser.ParseStoryPage(LoadTestData("RemovedStory"), 42);
            Assert.IsNotNull(story);
            Assert.AreEqual(42, story.StoryId);
            Assert.AreEqual("Άντε ρε …αχάριστοι Έλληνες…", story.Title);
            Assert.IsTrue(story.IsRemoved);
        }

        [Test]
        public void ParseStory()
        {
            string storyHtml = LoadTestData("Story");

            var story = _parser.ParseStoryPage(storyHtml, 60906);
            Assert.IsNotNull(story);
            Assert.AreEqual(60906, story.StoryId);
            Assert.AreEqual("Η μύτη του Χατζηδάκη (We are not alone - Argos)", story.Title);
            Assert.AreEqual(26, story.Voters.Count());
            Assert.AreEqual("Rodia", story.Username, "Username");
            string[] expectedVoters = new[]
            {
                "Oneiros",
                "talos",
                "jim_hellas",
                "Rodia",
                "ngeor",
                "exiled",
                "dkamen",
                "plagal",
                "tovytio",
                "renata",
                "Elikas",
                "espoir",
                "Throgos",
                "tobacco",
                "zombieastral",
                "pidyo",
                "nd-light",
                "vegasthedog",
                "Antidrasex",
                "arcades",
                "Marily",
                "simiomatariokipon",
                "Ignatius",
                "KnowDame",
                "SLY",
                "dytistonniptiron"
            };
            Assert.That(expectedVoters.All(expectedVoter => story.Voters.Any(v => v == expectedVoter)), "Voter");

            Assert.AreEqual((int) KnownStoryCategory.Blogs, story.Category);

            Assert.Greater(story.Comments.Count(), 0);
        }

        [Test]
        public void ParseStory79444ThroughVodafone()
        {
            string html = LoadTestData("79444");
            Assert.IsNotNull(html);
            var story = _parser.ParseStoryPage(html, 79444);
            Assert.IsNotNull(story);
            Assert.IsNotNull(story.Voters);
            Assert.AreEqual(5, story.Voters.Count());
            Assert.IsNotNull(story.Comments);
            Assert.AreEqual(8, story.Comments.Count());
            Assert.AreEqual(14, story.Comments.Select(TotalCommentCount).Sum());
        }

        private static int TotalCommentCount(Comment c)
        {
            return 1 + c.Comments.Select(TotalCommentCount).Sum();
        }

        [Test]
        public void ParseStory_7Votes_5Comments()
        {
            string storyHtml = LoadTestData("Story7Votes5Comments");

            var story = _parser.ParseStoryPage(storyHtml, 59685);
            Assert.IsNotNull(story);
            Assert.AreEqual(59685, story.StoryId);
            Assert.AreEqual("Η έλλειψη προσωπικού καταλύει κάθε κανόνα υγιεινής στα νοσοκομεία", story.Title);
            Assert.AreEqual("beta", story.Username, "Username");
            Assert.AreEqual((int) KnownStoryCategory.Blogs, story.Category);
            Assert.AreEqual("http://www.enet.gr/?i=issue.el.home&date=24/11/2010&id=227057", story.Url);
            Assert.AreEqual(7, story.Voters.Count());
            string[] expectedVoters = new[]
            {
                "pidyo",
                "vegasthedog",
                "arcades",
                "rogerios",
                "beta",
                "KnowDame",
                "dytistonniptiron"
            };

            Assert.That(expectedVoters.All(expectedVoter => story.Voters.Any(v => v == expectedVoter)), "Voter");
            Assert.AreEqual(114, _clock.GetCurrentInstant().Minus(story.CreatedAt.ToInstant()).TotalMinutes);

            Assert.IsNotNull(story.Comments);
            Assert.AreEqual(2, story.Comments.Count());

            AssertParsedComment(story.Comments.ElementAt(0), "beta", 7, 2, false, 98, 2);
            AssertParsedComment(story.Comments.ElementAt(0).Comments.ElementAt(0), "talos", 4, 5, false, 23, 0);
            AssertParsedComment(story.Comments.ElementAt(0).Comments.ElementAt(1), "beta", 0, 1, false, 8, 0);
            AssertParsedComment(story.Comments.ElementAt(1), "arcades", 3, 0, false, 88, 1);
            AssertParsedComment(story.Comments.ElementAt(1).Comments.ElementAt(0), "dkamen", 2, 0, false, 37, 0);
        }

        [Test]
        public void ParseStory_7Votes_5Comments_9Votes_13Comments()
        {
            string storyHtml = LoadTestData("Story7Votes5Comments9Votes13Comments");

            var story = _parser.ParseStoryPage(storyHtml, 59685);
            Assert.IsNotNull(story);
            Assert.AreEqual(59685, story.StoryId);
            Assert.AreEqual("Η έλλειψη προσωπικού καταλύει κάθε κανόνα υγιεινής στα νοσοκομεία", story.Title);
            Assert.AreEqual("beta", story.Username, "Username");
            Assert.AreEqual((int) KnownStoryCategory.Blogs, story.Category);
            Assert.AreEqual("http://www.enet.gr/?i=issue.el.home&date=24/11/2010&id=227057", story.Url);
            Assert.AreEqual(9, story.Voters.Count());
            string[] expectedVoters = new[]
            {
                "pidyo", "vegasthedog", "arcades", "rogerios", "beta", "KnowDame", "dytistonniptiron",
                "SLY", "tanevramou"
            };

            Assert.That(expectedVoters.All(expectedVoter => story.Voters.Any(v => v == expectedVoter)), "Voter");
            Assert.AreEqual(180, _clock.GetCurrentInstant().Minus(story.CreatedAt.ToInstant()).TotalMinutes);

            Assert.IsNotNull(story.Comments);
            Assert.AreEqual(4, story.Comments.Count());

            AssertParsedComment(story.Comments.ElementAt(0), "beta", 8, 3, false, 180, 6);
            AssertParsedComment(story.Comments.ElementAt(0).Comments.ElementAt(0), "talos", 9, 9, false, 133, 0);
            AssertParsedComment(story.Comments.ElementAt(0).Comments.ElementAt(1), "beta", 2, 2, false, 118, 0);
            AssertParsedComment(story.Comments.ElementAt(0).Comments.ElementAt(2), "Spyros.Dovas", 5, 1, false, 83, 0);
            AssertParsedComment(story.Comments.ElementAt(0).Comments.ElementAt(3), "talos", 5, 2, false, 74, 0);
            AssertParsedComment(story.Comments.ElementAt(0).Comments.ElementAt(4), "pidyo", 4, 0, false, 59, 0);
            AssertParsedComment(story.Comments.ElementAt(0).Comments.ElementAt(5), "talos", 4, 0, false, 50, 0);
            AssertParsedComment(story.Comments.ElementAt(1), "arcades", 3, 0, false, 180, 1);
            AssertParsedComment(story.Comments.ElementAt(1).Comments.ElementAt(0), "dkamen", 3, 1, false, 148, 0);
            AssertParsedComment(story.Comments.ElementAt(2), "kensai", 3, 3, false, 83, 2);
            AssertParsedComment(story.Comments.ElementAt(2).Comments.ElementAt(0), "SLY", 0, 2, false, 10, 0);
            AssertParsedComment(story.Comments.ElementAt(2).Comments.ElementAt(1), "SLY", 0, 0, false, 1, 0);
            AssertParsedComment(story.Comments.ElementAt(3), "tanevramou", 0, 0, false, 7, 0);
        }

        [Test]
        public void ParseUpcomingPage_StoryIds()
        {
            string html = LoadTestData("Upcoming");
            int[] storyIds = _parser.ParseListingPage(html).Select(s => s.StoryId).ToArray();
            CollectionAssert.AreEquivalent(new[]
            {
                65492,
                65531,
                65562,
                65563,
                65564,
                65565,
                65566,
                65568,
                65571,
                65572,
                65577,
                65578,
                65579,
                65582,
                65585,
                65587,
                65588,
                65590,
                65591,
                65592,
                65593,
                65595
            }, storyIds);
        }

        [Test]
        public void TestCanParseCommentVotes()
        {
            string html = LoadTestData("SingleStoryDownVoteDisabled");
            var story = _parser.ParseStoryPage(html, 72024);
            Assert.IsNotNull(story);
            Assert.IsNotNull(story.Comments);
            Assert.AreEqual(2, story.Comments.Count());
            Assert.AreEqual(0, story.Comments.ElementAt(0).VotesUp);
            Assert.AreEqual(0, story.Comments.ElementAt(0).VotesDown);
            Assert.AreEqual(1, story.Comments.ElementAt(1).VotesUp);
            Assert.AreEqual(0, story.Comments.ElementAt(1).VotesDown);
        }

        [Test]
        public void TestCanParseGoldenComments()
        {
            // it seems that child comments of gold comments ( > 20 votes ) are not parsed correctly
            string html = LoadTestData("BugGoldenComments");
            var story = _parser.ParseStoryPage(html, 72102);
            Assert.IsNotNull(story);
            Assert.IsNotNull(story.Comments);

            var commentators = new[]
            {
                new[] {"cinto", "contrabando", "cinto"},
                new[] {"chaca-khan", "cinto"},
                new[] {"chaca-khan", "cinto"},
                new[] {"talos", "Epanechnikov", "cinto", "talos"},
                new[] {"ermippos", "oldboy", "dkamen"},
                new[] {"dkamen"},
                new[] {"Vega"},
                new[] {"contrabando"},
                new[] {"ermippos"},
            };

            AssertCommentators(story, commentators);
        }

        [Test]
        public void TestCanParseRecursiveBuzz()
        {
            // test if it can parse a buzz that points to itself
            string html = LoadTestData("BugRecursiveBuzz");
            var story = _parser.ParseStoryPage(html, 72120);
            Assert.IsNotNull(story);
        }

        private static void AssertCommentators(Story story, string[][] commentators)
        {
            for (int i = 0; i < commentators.Length; i++)
            {
                var comment = story.Comments.ElementAt(i);
                Assert.IsNotNull(comment);
                Assert.AreEqual(commentators[i][0], comment.Username, "Not match at " + i);
                for (int j = 1; j < commentators[i].Length; j++)
                {
                    Assert.AreEqual(commentators[i][j], comment.Comments.ElementAt(j - 1).Username,
                        "Not match at " + i + "-" + j);
                }
            }
        }

        private void AssertParsedComment(Comment comment, string username, int votesUp, int votesDown, bool isBuried,
            int minutes, int childrenCommentCount)
        {
            Assert.IsNotNull(comment);
            Assert.Greater(comment.CommentId, 0);
            Assert.AreEqual(username, comment.Username);
            Assert.AreEqual(childrenCommentCount, comment.Comments.Count());
            Assert.AreEqual(votesUp, comment.VotesUp);
            Assert.AreEqual(votesDown, comment.VotesDown);
            Assert.AreEqual(
                minutes,
                _clock.GetCurrentInstant().Minus(comment.CreatedAt.ToInstant()).TotalMinutes,
                $"Minutes of comment {comment.CommentId}");
            Assert.AreEqual(isBuried, comment.IsBuried,
                "Comment " + comment.CommentId + " expected to be burried: " + isBuried);
        }

        private void AssertParsedComment(Comment comment, string username, int votesUp, int votesDown, bool isBuried,
            string timeSpan, int childrenCommentCount = 0)
        {
            AssertParsedComment(comment, username, votesUp, votesDown, isBuried,
                (int) TimeSpan.Parse(timeSpan).TotalMinutes,
                childrenCommentCount);
        }

        private string LoadTestData(string id)
        {
            return ResourceLoader.Load(id);
        }
    }
}