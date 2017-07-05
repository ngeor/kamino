// --------------------------------------------------------------------------------
// <copyright file="Parser.cs" company="Nikolaos Georgiou">
//      Copyright (C) Nikolaos Georgiou 2010-2013
// </copyright>
// <author>Nikolaos Georgiou</author>
// * Date: 2013/09/22
// * Time: 5:56 μμ
// --------------------------------------------------------------------------------

using System;
using System.Collections.Generic;
using System.Linq;
using System.Text.RegularExpressions;

namespace BuzzStats.ParserWebApi
{
    public sealed class Parser
    {
        /// <summary>
        /// regex that matches a recent comments block specific to a story
        /// </summary>
        private static readonly Regex RegexRecentCommentsBlock = new Regex(
            @"<li class=['""]threado['""]>" +
            @"\s*" +
            @"<span [^>]+>" +
            @"\s*" +
            @"<a class=['""]sbcommentlink['""] href=['""]\/story\.php\?id=(?<storyId>\d+)", RegexOptions.Compiled);

        /// <summary>
        /// regex that matches a recent comment in the right side bar
        /// </summary>
        private static readonly Regex RegexRecentComments = new Regex(
            @"<a href=['""]\/story\.php\?id=(?<storyId>\d+)#wholecomment(?<commentId>\d+)['""]>" +
            @"(?<username>[^<]+)<\/a>",
            RegexOptions.Compiled);

        private static readonly Regex RegexStoryComments = new Regex(
            @"<li>\s*" +
            @"<div class=""user-[^""]+"" id=""comment-wrap"">\s*" +
            "(" + // match normal comment or buried comment
            "(" + // begin normal comment
            @"<div id=""comment-head(-feat)?"">\s*" +
            ")" + // end normal comment
            "|" +
            "(" + // begin buried comment
            @"(?<isBuried><div class=""buried_comment"">)" +
            @"[\s\S]+?" +
            @"commento-(?<commentId>\d+)" +
            @"[\s\S]+?" +
            ")" + // end buried comment
            ")" +
            @"... <a href=""\/user\.php\?login=[^""]+"">(?<username>[^<]+)<\/a>\s*" +
            @"(?<time>πριν [^<]+)" +
            "(" + // match normal comment or buried comment
            "(" + // begin normal comment
            @"<a href=""#wholecomment(?<commentId>\d+)""" +
            @"[\s\S]+?" +
            @"<a id=""cvoteup[^>]+><strong>(?<votesUp>\d*)<\/strong><\/a>" +
            @"[\s\S]+?" +
            @"<a id=""cvotedown[^>]+><strong>(?<votesDown>\d*)<\/strong><\/a>" +
            ")" + // end normal comment
            "|" +
            "(" + // begin buried comment
            @"<\/div>\s*<div class=""comment-body-buried""" +
            ")" + // end buried comment
            ")" +
            @"[\s\S]+?" +
            @"<\/li>" +
            @"(?<childComments>\s*<ul id=""comments-list"">[\s\S]+?<\/ul>)?",
            RegexOptions.Compiled);

        private static readonly Regex RegexVoterUrl = new Regex(@"""\/user\.php\?login=(?<username>[^""]+)""",
            RegexOptions.Compiled);

        private static readonly Regex RegexStories = new Regex(
            @"<ul[^>]+?id=""cat(?<storyCategory>\d)"">\s*" +
            @"<li[^>]+?><a id=""[^""]+"" href=""" +
            @"javascript:menealo\(\d+,(?<storyId>\d+),[^,]+,[^,]+,[^\)]+\)"">(?<storyVotes>\d+)<\/a><\/li>" +
            @"[\s\S]+?" + @"<h4[^>]+>" + @"\s*" +
            @"<a href=""(?<storyUrl>[^""]+)""\s*>(?<storyTitle>[^>]+)<\/a>\s*</h4>" +
            @"[\s\S]+?" + @"<span id=""ls_link_submitter-\d+"">(?<storyUsername>[^<]+)</span>" +
            @"[\s\S]+?" + @"<span id=""ls_timeago-\d+"">(?<storyCreatedAt>[^\(]+)\(", RegexOptions.Compiled);

        private static readonly Regex RegexStory = new Regex(
            @"<ul[^>]+?id=""cat(?<storyCategory>\d)"">\s*"
            + @"<li[^>]+?><a id=""[^""]+"" href="""
            + @"javascript:menealo\(\d+,(?<storyId>\d+),[^,]+,[^,]+,[^\)]+\)"">(?<storyVotes>\d+)<\/a><\/li>"
            + @"[\s\S]+?"
            + @"<h4[^>]+>"
            + @"\s*"
            + @"<a href=""(?<storyUrl>[^""]+)""[^>]*>(?<storyTitle>[^>]+)<\/a>\s*</h4>"
            + @"[\s\S]+?"
            + @"<span id=""ls_link_submitter-\d+"">(?<storyUsername>[^<]+)</span>"
            + @"[\s\S]+?"
            + @"<span id=""ls_timeago-\d+"">(?<storyCreatedAt>[^\(]+)\("
            + @"[\s\S]+?"
            + @"<h2>Σχόλια<\/h2>(?<commentsHtml>[\s\S]+)<div id=""commentform"""
            + @"[\s\S]+?"
            +
            @"<div class\s*=\s*""whovotedwrapper"" id\s*=\s*""idwhovotedwrapper"">(?<whoVotedList>[\s\S]+?)<\/div>",
            RegexOptions.Compiled);

        private static readonly Regex RegexRemovedStory = new Regex(
            @"<title>\s*Buzz\s*-\s*(?<storyTitle>[^<]+?)\s*</title>" +
            @"[\s\S]+?" +
            @"<p>Αυτή η καταχώριση δεν είναι πλέον διαθέσιμη</p>",
            RegexOptions.Compiled);

        private static readonly Regex RegexTimeSpan =
            new Regex(
                @"((?<days>\d+) (μέρες|μέρα))?\s*((?<hours>\d+) (ώρες|ώρα))?\s*" +
                @"((?<minutes>\d+) (λεπτά|λεπτό))?\s*(?<seconds>λίγα δευτερόλεπτα)?",
                RegexOptions.Compiled);

        public static TimeSpan? ToTimeSpan(string relativeTimespan)
        {
            if (string.IsNullOrEmpty(relativeTimespan))
            {
                return null;
            }

            relativeTimespan = relativeTimespan.Trim();

            MatchCollection mc = RegexTimeSpan.Matches(relativeTimespan);

            foreach (Match m in mc)
            {
                if (m.Success &&
                    (m.Groups["days"].Success || m.Groups["hours"].Success || m.Groups["minutes"].Success ||
                     m.Groups["seconds"].Success))
                {
                    int days = m.Groups["days"].Success ? Convert.ToInt32(m.Groups["days"].Value) : 0;
                    int hours = m.Groups["hours"].Success ? Convert.ToInt32(m.Groups["hours"].Value) : 0;
                    int minutes = m.Groups["minutes"].Success ? Convert.ToInt32(m.Groups["minutes"].Value) : 0;
                    return TimeSpan.FromDays(days).Add(TimeSpan.FromHours(hours).Add(TimeSpan.FromMinutes(minutes)));
                }
            }

            return null;
        }

        public Story ParseStoryPage(string storyPageContents, int requestedStoryId)
        {
            if (string.IsNullOrEmpty(storyPageContents))
            {
                throw new ArgumentNullException("storyPageContents");
            }

            return ParseNonRemovedStoryPage(storyPageContents, requestedStoryId)
                   ?? ParseRemovedStoryPage(storyPageContents, requestedStoryId)
                   ?? ParseFailed();
        }

        private Story ParseFailed()
        {
            throw new ParserFailedException();
        }

        public IEnumerable<StoryListingSummary> ParseListingPage(string htmlUpcomingPage)
        {
            var storyIds = ParseListingPageMainArea(htmlUpcomingPage);
            var commentedStoryIds = ParseListingPageRecentComments(htmlUpcomingPage);
            return Merge(storyIds, commentedStoryIds);
        }

        /// <summary>
        /// Gets the first match of this collection, as long as it was successful.
        /// </summary>
        /// <param name="matchCollection">
        /// The match collection.
        /// </param>
        /// <returns>
        /// The first match, if it was successful. <c>null</c> otherwise.
        /// </returns>
        private static Match FirstMatch(MatchCollection matchCollection)
        {
            return matchCollection != null && matchCollection.Count >= 1 && matchCollection[0].Success
                ? matchCollection[0]
                : null;
        }

        private static IEnumerable<StoryListingSummary> ParseListingPageMainArea(string upcomingPage)
        {
            if (string.IsNullOrWhiteSpace(upcomingPage))
            {
                return Enumerable.Empty<StoryListingSummary>();
            }

            return
                from Match match in RegexStories.Matches(upcomingPage)
                select new StoryListingSummary
                {
                    StoryId = Convert.ToInt32(match.Groups["storyId"].Value),
                    VoteCount = Convert.ToInt32(match.Groups["storyVotes"].Value)
                };

            //int storyCategory = Convert.ToInt32(match.Groups["storyCategory"].Value);
            //string storyUrl = match.Groups["storyUrl"].Value;
            //string storyTitle = match.Groups["storyTitle"].Value.Trim();
            //DateTime storyCreatedAt = ToDateTime(match.Groups["storyCreatedAt"].Value);
            //string storyUsername = match.Groups["storyUsername"].Value;
        }

        private static DateTime ToDateTime(string relativeTimespan)
        {
            TimeSpan? timeSpan = ToTimeSpan(relativeTimespan);
            return timeSpan.HasValue ? DateTime.UtcNow.Subtract(timeSpan.Value) : DateTime.MinValue;
        }

        private static int ToVoteCount(string s)
        {
            return string.IsNullOrEmpty(s) ? 0 : Convert.ToInt32(s);
        }

        private static IEnumerable<string> Voters(string whoVotedList)
        {
            return
                from Match match in RegexVoterUrl.Matches(whoVotedList)
                select match.Groups["username"].Value;
        }

        private Comment MatchToComment(Match m)
        {
            int commentId = Convert.ToInt32(m.Groups["commentId"].Value);
            string username = m.Groups["username"].Value;
            int votesUp = ToVoteCount(m.Groups["votesUp"].Value);
            int votesDown = ToVoteCount(m.Groups["votesDown"].Value);
            DateTime createdAt = ToDateTime(m.Groups["time"].Value);
            string childCommentsHtml = m.Groups["childComments"].Value;
            bool isBuried = m.Groups["isBuried"].Success;

            return new Comment
            {
                CommentId = commentId,
                Username = username,
                VotesUp = votesUp,
                VotesDown = votesDown,
                IsBuried = isBuried,
                CreatedAt = createdAt,
                Comments = AddComments(childCommentsHtml)
            };
        }

        private Comment[] AddComments(string storyPageContents)
        {
            IEnumerable<Match> commentMatches = string.IsNullOrEmpty(storyPageContents)
                ? Enumerable.Empty<Match>()
                : RegexStoryComments.Matches(storyPageContents).Cast<Match>();
            return commentMatches.Select(MatchToComment).ToArray();
        }

        private Story ParseNonRemovedStoryPage(string storyPageContents, int requestedStoryId)
        {
            MatchCollection mc = RegexStory.Matches(storyPageContents);
            Match match = FirstMatch(mc);
            if (match == null)
            {
                return null;
            }

            int storyId = Convert.ToInt32(match.Groups["storyId"].Value);

            int storyCategory = Convert.ToInt32(match.Groups["storyCategory"].Value);
            string storyUrl = match.Groups["storyUrl"].Value;
            string storyTitle = match.Groups["storyTitle"].Value.Trim();
            DateTime storyCreatedAt = ToDateTime(match.Groups["storyCreatedAt"].Value);
            string storyUsername = match.Groups["storyUsername"].Value;
            string whoVotedList = match.Groups["whoVotedList"].Value;
            string[] voters = Voters(whoVotedList).ToArray();
            string commentsHtml = match.Groups["commentsHtml"].Value;

            return new Story
            {
                StoryId = storyId,
                Category = storyCategory,
                Url = storyUrl,
                Title = storyTitle,
                CreatedAt = storyCreatedAt,
                Username = storyUsername,
                Voters = voters,
                Comments = AddComments(commentsHtml)
            };
        }

        private Story ParseRemovedStoryPage(string storyPageContents, int requestedStoryId)
        {
            MatchCollection mc = RegexRemovedStory.Matches(storyPageContents);
            Match match = FirstMatch(mc);
            if (match == null)
            {
                return null;
            }

            string storyTitle = match.Groups["storyTitle"].Value;
            return new Story
            {
                IsRemoved = true,
                StoryId = requestedStoryId,
                Title = storyTitle
            };
        }

        private IEnumerable<StoryListingSummary> ParseListingPageRecentComments(string homePageContents)
        {
            if (string.IsNullOrEmpty(homePageContents))
            {
                return Enumerable.Empty<StoryListingSummary>();
            }

            return
                from Match m in RegexRecentCommentsBlock.Matches(homePageContents)
                select new StoryListingSummary
                {
                    StoryId = Convert.ToInt32(m.Groups["storyId"].Value)
                };
        }

        private IEnumerable<StoryListingSummary> Merge(
            IEnumerable<StoryListingSummary> first,
            IEnumerable<StoryListingSummary> second)
        {
            Dictionary<int, StoryListingSummary> map = new Dictionary<int, StoryListingSummary>();

            // take all the first ones
            foreach (var f in first)
            {
                map.Add(f.StoryId, f);
            }

            // take the second ones that aren't already there
            foreach (var s in second)
            {
                if (!map.ContainsKey(s.StoryId))
                {
                    map.Add(s.StoryId, s);
                }
            }

            return map.Values;
        }
    }
}