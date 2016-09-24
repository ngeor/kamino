// --------------------------------------------------------------------------------
// <copyright file="StoryDataLayer.cs" company="Nikolaos Georgiou">
//   Copyright (C) Nikolaos Georgiou 2010-2015
// </copyright>
// <author>Nikolaos Georgiou</author>
// * Date: 2015/03/28
// * Time: 12:55:47
// --------------------------------------------------------------------------------

using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.Linq;
using NHibernate;
using NHibernate.Linq;
using NGSoftware.Common;
using BuzzStats.Data.NHibernate.Entities;

namespace BuzzStats.Data.NHibernate
{
    /// <summary>
    /// Implementation of the Data Layer around an NHibernate ISession.
    /// </summary>
    public sealed class StoryDataLayer : CoreDataClient, IStoryDataLayer
    {
        public StoryDataLayer(ISession session) : base(session)
        {
        }

        public StoryData Create(StoryData storyData)
        {
            storyData.ValidatePreCreate();
            StoryEntity existing = CoreData.LoadStoryEntity(storyData.StoryId);
            if (existing != null && existing.Id > 0)
            {
                throw new PersistentObjectException(string.Format("Story {0} already exists", storyData.StoryId));
            }

            Debug.WriteLine("DataLayer.{0} StoryId={1}", "Create", storyData.StoryId);
            StoryEntity storyEntity = storyData.ToEntity();
            Debug.Assert(storyEntity != null, "StoryEntity should not be null");
            Session.SaveOrUpdate(storyEntity);

            // map back the db id
            return storyEntity.ToData();
        }

        public Dictionary<string, int> GetCommentedStoryCountsPerUser(DateRange dateRange)
        {
            return Session.Query<CommentEntity>()
                .FilterOnCreatedAt(dateRange)
                .Where(c => c.Story.RemovedAt == null)
                .GroupBy(c => c.Username)
                .Select(g => new
                {
                    g.Key,
                    Count = g.Select(c => c.Story.Id).Distinct().Count()
                }).ToDictionary(k => k.Key, v => v.Count);
        }

        public Dictionary<string, int> GetStoryCountsPerHost(DateRange dateRange)
        {
            return Session.Query<StoryEntity>()
                .FilterOnCreatedAt(dateRange)
                .Where(s => s.Host != null && s.RemovedAt == null)
                .GroupBy(s => s.Host)
                .Select(g => new
                {
                    g.Key,
                    Count = g.Count()
                }).ToDictionary(k => k.Key, v => v.Count);
        }

        public Dictionary<string, int> GetStoryCountsPerUser(DateRange dateRange)
        {
            return Session.Query<StoryEntity>()
                .FilterOnCreatedAt(dateRange)
                .Where(s => s.RemovedAt == null)
                .GroupBy(s => s.Username)
                .Select(g => new
                {
                    g.Key,
                    Count = g.Count()
                }).ToDictionary(k => k.Key, v => v.Count);
        }

        public IStoryQuery Query()
        {
            return new StoryQuery(this);
        }

        public StoryData Read(int storyBusinessId)
        {
            StoryEntity story = CoreData.LoadStoryEntity(storyBusinessId);
            return story.ToData();
        }

        public DateTime OldestStoryDate()
        {
            return Session.Query<StoryEntity>()
                .OrderBy(s => s.CreatedAt)
                .Select(s => s.CreatedAt)
                .FirstOrDefault();
        }

        public void Update(StoryData storyData)
        {
            storyData.ValidatePreUpdate();
            StoryEntity storyEntity;

            try
            {
                storyEntity = CoreData.SessionMap(storyData);
            }
            catch (ObjectNotFoundException ex)
            {
                throw new InvalidStoryIdException(ex.Message, ex);
            }

            Session.SaveOrUpdate(storyEntity);
        }

        public MinMaxStats GetMinMaxStats()
        {
            var sqlQuery = Session.CreateSQLQuery(
                "SELECT MIN(LastCheckedAt) AS minLastCheckedAt, MAX(LastCheckedAt) AS maxLastCheckedAt, " +
                    "MIN(TotalChecks) AS minTotalChecks, MAX(TotalChecks) AS maxTotalChecks " +
                    "FROM Story WHERE RemovedAt IS NULL");

            sqlQuery.AddScalar("minLastCheckedAt", NHibernateUtil.DateTime);
            sqlQuery.AddScalar("maxLastCheckedAt", NHibernateUtil.DateTime);
            sqlQuery.AddScalar("minTotalChecks", NHibernateUtil.Int32);
            sqlQuery.AddScalar("maxTotalChecks", NHibernateUtil.Int32);
            object[] result = (object[]) sqlQuery.UniqueResult();
            return new MinMaxStats
            {
                LastCheckedAt = new MinMaxValue<DateTime>
                {
                    Min = (DateTime) result[0],
                    Max = (DateTime) result[1]
                },
                TotalChecks = new MinMaxValue<int>
                {
                    Min = (int) result[2],
                    Max = (int) result[3]
                }
            };
        }
    }
}
