// --------------------------------------------------------------------------------
// <copyright file="StoryVoteDataLayer.cs" company="Nikolaos Georgiou">
//   Copyright (C) Nikolaos Georgiou 2010-2015
// </copyright>
// <author>Nikolaos Georgiou</author>
// * Date: 2015/03/28
// * Time: 12:55:52
// --------------------------------------------------------------------------------

using System;
using System.Collections.Generic;
using System.Linq;
using NHibernate;
using NHibernate.Linq;
using StackExchange.Profiling;
using NGSoftware.Common;
using BuzzStats.Data.NHibernate.Entities;

namespace BuzzStats.Data.NHibernate
{
    /// <summary>
    /// Implementation of the Data Layer around an NHibernate ISession.
    /// </summary>
    internal sealed class StoryVoteDataLayer : CoreDataClient, IStoryVoteDataLayer
    {
        public StoryVoteDataLayer(ISession session) : base(session)
        {
        }

        public void Create(StoryVoteData newStoryVote)
        {
            if (newStoryVote == null)
            {
                throw new ArgumentNullException("newStoryVote");
            }

            StoryVoteEntity sv = newStoryVote.ToEntity();
            sv.Story = CoreData.SessionMap(newStoryVote.Story);
            Session.SaveOrUpdate(sv);
        }

        public void Delete(StoryData story, string voter)
        {
            StoryEntity storyEntity = CoreData.SessionMap(story);
            StoryVoteEntity storyVote = Session.Query<StoryVoteEntity>()
                .Where(sv => sv.Story == storyEntity && sv.Username == voter)
                .SingleOrDefault();

            if (storyVote != null)
            {
                Session.Delete(storyVote);
            }
        }

        public bool Exists(StoryData story, string voter)
        {
            if (string.IsNullOrWhiteSpace(voter))
            {
                throw new ArgumentNullException("voter");
            }

            // need to map it inside the NHibernate session, can't use automapper here.
            StoryEntity storyEntity = CoreData.SessionMap(story);

            return Session.Query<StoryVoteEntity>()
                .Where(sv => sv.Story == storyEntity && sv.Username == voter)

                // optimization, only fetch one column since we're only checking for existence
                .Select(sv => sv.Username)
                .FirstOrDefault() != null;
        }

        public Dictionary<string, int> SumPerHost(DateRange dateRange)
        {
            return Session.Query<StoryEntity>()
                .FilterOnCreatedAt(dateRange)
                .Where(s => s.Host != null && s.RemovedAt == null)
                .GroupBy(s => s.Host)
                .Select(g => new
                {
                    g.Key,
                    Count = g.Sum(s2 => s2.VoteCount)
                }).ToDictionary(k => k.Key, v => v.Count);
        }

        public StoryVoteData[] Query(int maxCount, string username)
        {
            MiniProfiler profiler = MiniProfiler.Current;
            using (profiler.Step("GetNewVotes"))
            {
                StoryEntity storyAlias = null;

                var q =
                    Session.QueryOver<StoryVoteEntity>()
                        .JoinAlias(sv => sv.Story, () => storyAlias)
                        .Where(sv => sv.Username != storyAlias.Username && storyAlias.RemovedAt == null);

                if (!string.IsNullOrWhiteSpace(username))
                {
                    q = q.Where(sv => sv.Username == username);
                }

                return q.OrderBy(sv => sv.CreatedAt).Desc
                    .Take(maxCount)
                    .List()
                    .ToData();
            }
        }

        public StoryVoteData[] Query(StoryData story)
        {
            return Session.Query<StoryVoteEntity>()
                .Where(sv => sv.Story.StoryId == story.StoryId)
                .OrderBy(sv => sv.CreatedAt)
                .ToData(story);
        }
    }
}
