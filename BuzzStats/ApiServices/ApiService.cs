// --------------------------------------------------------------------------------
// <copyright file="ApiService.cs" company="Nikolaos Georgiou">
//      Copyright (C) Nikolaos Georgiou 2010-2013
// </copyright>
// <author>Nikolaos Georgiou</author>
// * Date: 2013/10/04
// * Time: 10:12 μμ
// --------------------------------------------------------------------------------

using System;
using System.Collections.Generic;
using System.Linq;
using NGSoftware.Common;
using NGSoftware.Common.Collections;
using BuzzStats.Data;

namespace BuzzStats.ApiServices
{
    public class ApiService : IApiService
    {
        public ApiService(IDbSession dbSession)
        {
            if (dbSession == null)
            {
                throw new ArgumentNullException("dbSession");
            }

            DbSession = dbSession;
        }

        private IDbSession DbSession { get; set; }

        public CountStatsResponse GetCommentCountStats(CountStatsRequest request)
        {
            return CountStats(request, dr => DbSession.Comments.Count(dr));
        }

        public HostStats[] GetHostStats(HostStatsRequest request)
        {
            if (request == null)
            {
                throw new ArgumentNullException("request");
            }

            if (request.MaxResults < 0)
            {
                throw new ArgumentOutOfRangeException("request", "MaxResults cannot be negative");
            }

            const int DefaultMaxResults = 15;

            int maxResults = request.MaxResults == 0 ? DefaultMaxResults : request.MaxResults;

            Dictionary<string, int> h1 = DbSession.Stories.GetStoryCountsPerHost(request.DateRange);
            Dictionary<string, int> h2 = DbSession.StoryVotes.SumPerHost(request.DateRange);

            Dictionary<string, HostStats> result = new Dictionary<string, HostStats>();
            IEnumerable<HostStats> values = result.StartMerge((hs, host) => hs.Host = host)
                .Merge(h1, (hs, count) => hs.StoryCount = count)
                .Merge(h2, (hs, sum) => hs.VoteCount = sum)
                .Values
                .Where(h => h.StoryCount >= request.MinStoryCount);

            List<HostStats> sortedResults = ReflectionSorter.Sort(values, request.SortExpression);

            return sortedResults.Skip(request.StartIndex).Take(maxResults).ToArray();
        }

        public RecentActivity[] GetRecentActivity(RecentActivityRequest request)
        {
            const int maxCount = 10;
            if (request == null)
            {
                request = new RecentActivityRequest();
            }

            if (request.MaxCount <= 0)
            {
                request.MaxCount = maxCount;
            }

            if (DbSession.RecentActivityRepository != null)
            {
                // supported natively
                return DbSession.RecentActivityRepository.Get(request);
            }

            RecentActivity[] newStories = GetNewStories(request);
            RecentActivity[] newStoryVotes = GetNewStoryVotes(request);
            RecentActivity[] newComments = GetNewComments(request);
            RecentActivity[] result =
                newStories.Concat(newStoryVotes)
                    .Concat(newComments)
                    .OrderBy(r => r.Age)
                    .Take(request.MaxCount)
                    .ToArray();
            return result;
        }

        public RecentlyCommentedStory[] GetRecentCommentsPerStory()
        {
            return GetRecentlyCommented(storyCount: 6, commentCount: 5);
        }

        public CommentSummary[] GetRecentPopularComments()
        {
            return DbSession.Comments.Query(new CommentDataQueryParameters
            {
                CreatedAt = DateRange.After(14.Days().Ago()),
                SortBy = new[]
                {
                    CommentSortField.VotesUp.Desc(), CommentSortField.CreatedAt.Desc()
                },
                Count = 10
            }).Select(commentData => commentData.ToCommentSummary()).ToArray();
        }

        public StorySummary[] GetStorySummaries(GetStorySummariesRequest request)
        {
            if (request == null)
            {
                throw new ArgumentNullException("request");
            }

            var q = DbSession.Stories.Query();

            if (request.SortBy.Any())
            {
                q = q.OrderBy(request.SortBy.First());
                q = request.SortBy.Skip(1).Aggregate(q, (current, sortExpression) => current.ThenBy(sortExpression));
            }

            return q.Skip(request.RowIndex)
                .Take(request.MaxRows)
                .AsEnumerable()
                .Select(storyData => storyData.ToStorySummary())
                .ToArray();
        }

        public UserStats[] GetUserStats(UserStatsRequest request)
        {
            string sortExpression = request.SortExpression;
            DateRange dateRange = request.DateRange;
            Dictionary<string, int> storyCountStats = DbSession.Stories.GetStoryCountsPerUser(dateRange);
            Dictionary<string, int> commentedGetStoryCountStats =
                DbSession.Stories.GetCommentedStoryCountsPerUser(dateRange);

            var buriedStats = DbSession.Comments.CountBuriedPerUser(dateRange);
            var commentStats = DbSession.Comments.CountPerUser(dateRange);
            var voteUpStats = DbSession.Comments.SumVotesUpPerUser(dateRange);
            var voteDownStats = DbSession.Comments.SumVotesDownPerUser(dateRange);

            Dictionary<string, UserStats> result = new Dictionary<string, UserStats>();

            List<UserStats> unsorted = result.StartMerge((us, username) => us.Username = username)
                .Merge(storyCountStats, (us, storyCount) => us.StoryCount = storyCount)
                .Merge(commentedGetStoryCountStats,
                    (us, commentedStoriesCount) =>
                        us.CommentedStoriesCount = commentedStoriesCount)
                .Merge(buriedStats, (us, buriedCount) => us.BuriedCommentCount = buriedCount)
                .Merge(commentStats, (us, commentCount) => us.CommentCount = commentCount)
                .Merge(voteUpStats, (us, voteUpSum) => us.VotesUp = voteUpSum)
                .Merge(voteDownStats, (us, voteDownSum) => us.VotesDown = voteDownSum)
                .Values.OrderBy(u => u.Username).ToList();

            if (unsorted.Any())
            {
                UserStats average = CalculateAverage(unsorted);
                unsorted.Insert(0, average);
            }

            ReflectionSorter.Sort(unsorted, sortExpression ?? "Username");
            return unsorted.ToArray();
        }

        public CountStatsResponse GetStoryCountStats(CountStatsRequest request)
        {
            return CountStats(request, dr => DbSession.Stories.Query().CreatedAt.InRange(dr).Count());
        }

        #region Graph Helper

        private CountStatsResponse CountStats(CountStatsRequest request, Func<DateRange, int> countRetriever)
        {
            var dateRange = request.DateRange;
            var interval = request.Interval;

            if (interval == DateTimeUnit.Unknown)
            {
                throw new ArgumentOutOfRangeException("interval", "Cannot be unspecified");
            }

            if (interval == DateTimeUnit.Second || interval == DateTimeUnit.Minute || interval == DateTimeUnit.Hour)
            {
                throw new ArgumentOutOfRangeException("interval", "Interval range too short");
            }

            DateTime actualStartDate = GetStartDate(dateRange);
            DateTime actualStopDate = GetStopDate(dateRange);
            if (actualStartDate > actualStopDate)
            {
                throw new ArgumentOutOfRangeException("dateRange", "End date was before start date");
            }

            TimeSpan period = actualStopDate.Subtract(actualStartDate);
            if (interval == DateTimeUnit.Day && period.TotalDays >= 366)
            {
                throw new NotSupportedException("Date range cannot exceed a year when showing results per day");
            }

            var graphPoints =
                from
                intervalDateRange
                in
                DateRange.Create(actualStartDate, actualStopDate).Split(interval)
                select
                new GraphPoint<DateTime, int>
                {
                    X = intervalDateRange.StartDate.Value,
                    Y = countRetriever(intervalDateRange)
                };

            return new CountStatsResponse(request, graphPoints);
        }

        private DateTime GetStartDate(DateRange dateRange)
        {
            DateTime startDate = dateRange.GetStartDate(DateTime.MinValue);
            DateTime oldestStoryDate = DbSession.Stories.OldestStoryDate();

            if (startDate < oldestStoryDate)
            {
                startDate = oldestStoryDate;
            }

            if (startDate.Year <= 42)
            {
                startDate = TestableDateTime.UtcNow;
            }

            return startDate;
        }

        private DateTime GetStopDate(DateRange dateRange)
        {
            DateTime stopDate = dateRange.GetStopDate(TestableDateTime.UtcNow);
            if (stopDate > TestableDateTime.UtcNow)
            {
                stopDate = TestableDateTime.UtcNow;
            }

            return stopDate;
        }

        #endregion

        #region User Stats

        private UserStats CalculateAverage(List<UserStats> list)
        {
            return new UserStats
            {
                Username = string.Empty,
                BuriedCommentCount = list.Average(u => u.BuriedCommentCount),
                CommentCount = list.Average(u => u.CommentCount),
                CommentedStoriesCount = list.Average(u => u.CommentedStoriesCount),
                StoryCount = list.Average(u => u.StoryCount),
                VotesDown = list.Average(u => u.VotesDown),
                VotesUp = list.Average(u => u.VotesUp)
            };
        }

        #endregion

        #region Recently Commented Stories

        private RecentlyCommentedStory ConvertToFullStoryKeepingComments(StoryData story, int commentCount)
        {
            RecentlyCommentedStory result = story.ToRecentlyCommentedStory();

            CommentData[] comments = DbSession.Comments.Query(
                new CommentDataQueryParameters
                {
                    Count = commentCount,
                    StoryId = story.StoryId,
                    SortBy = new[] {CommentSortField.CreatedAt.Desc()}
                });

            result.Comments = comments.Select(c => c.ToRecentlyCommentedStoryComment()).ToArray();
            return result;
        }

        private RecentlyCommentedStory[] GetRecentlyCommented(int storyCount, int commentCount)
        {
            var stories = DbSession.Stories.Query()
                .OrderBy(StorySortField.LastCommentedAt.Desc())
                .Take(storyCount)
                .AsEnumerable();

            return stories.Select(s => ConvertToFullStoryKeepingComments(s, commentCount)).ToArray();
        }

        #endregion

        #region Recent Activity

        private RecentActivity[] GetNewComments(RecentActivityRequest request)
        {
            CommentData[] comments = DbSession.Comments.Query(new CommentDataQueryParameters
            {
                Count = request.MaxCount,
                Username = request.Username,
                SortBy = new[] {CommentSortField.CreatedAt.Desc()}
            });
            return comments.Select(c => new RecentActivity
            {
                Age = c.CreatedAt.Age(),
                DetectedAtAge = c.DetectedAt.Age(),
                What = RecentActivityKind.NewComment,
                Who = c.Username,
                StoryTitle = c.Story.Title,
                StoryId = c.Story.StoryId,
                CommentId = c.CommentId
            }).ToArray();
        }

        private RecentActivity[] GetNewStories(RecentActivityRequest request)
        {
            var stories = DbSession.Stories.Query()
                .Username(request.Username)
                .Take(request.MaxCount)
                .AsEnumerable();

            return stories.Select(s => new RecentActivity
            {
                Age = s.CreatedAt.Age(),
                DetectedAtAge = s.DetectedAt.Age(),
                What = RecentActivityKind.NewStory,
                Who = s.Username,
                StoryTitle = s.Title,
                StoryId = s.StoryId
            }).ToArray();
        }

        private RecentActivity[] GetNewStoryVotes(RecentActivityRequest request)
        {
            StoryVoteData[] storyVotes = DbSession.StoryVotes.Query(request.MaxCount, request.Username);
            return storyVotes.Select(sv => new RecentActivity
            {
                Age = sv.CreatedAt.Age(),
                DetectedAtAge = sv.CreatedAt.Age(),
                What = RecentActivityKind.NewStoryVote,
                Who = sv.Username,
                StoryTitle = sv.Story.Title,
                StoryId = sv.Story.StoryId
            }).ToArray();
        }

        #endregion
    }
}