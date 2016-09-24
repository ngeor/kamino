// --------------------------------------------------------------------------------
// <copyright file="Poller.cs" company="Nikolaos Georgiou">
//   Copyright (C) Nikolaos Georgiou 2010-2015
// </copyright>
// <author>Nikolaos Georgiou</author>
// * Date: 2015/10/17
// * Time: 04:49:20
// --------------------------------------------------------------------------------

using System;
using System.Collections.Generic;
using System.Linq;
using log4net;
using NGSoftware.Common.Collections;
using NGSoftware.Common.Messaging;
using BuzzStats.Common;
using BuzzStats.Data;
using BuzzStats.Persister;

namespace BuzzStats.Crawl
{
    public class Poller : ILeafSource
    {
        const int DefaultStoryCount = 10;
        const int DefaultCycleCount = 20;

        private static readonly ILog Log = LogManager.GetLogger(typeof (Poller));
        private readonly IDbContext _dbContext;
        private readonly IUrlProvider _urlProvider;
        private readonly ILeafProducerMonitor _leafProducerMonitor;

        private HashSet<int> _excludedStoryIds = new HashSet<int>();
        private int _cycles = 0;

        private int _storyCount = DefaultStoryCount;
        private int _cycleCount = DefaultCycleCount;

        public Poller(
            IMessageBus messageBus,
            IDbContext dbContext,
            IUrlProvider urlProvider,
            ILeafProducerMonitor leafProducerMonitor)
        {
            if (messageBus == null)
            {
                throw new ArgumentNullException("messageBus");
            }

            if (dbContext == null)
            {
                throw new ArgumentNullException("dbContext");
            }

            _dbContext = dbContext;

            if (urlProvider == null)
            {
                throw new ArgumentNullException("urlProvider");
            }

            _urlProvider = urlProvider;

            if (leafProducerMonitor == null)
            {
                throw new ArgumentNullException("leafProducerMonitor");
            }

            _leafProducerMonitor = leafProducerMonitor;
            messageBus.Subscribe<StoryCheckedMessage>(OnStoryChecked);
            messageBus.Subscribe<LeafProducerStartingMessage>(OnLeafProducerStarting);
        }

        public int Count
        {
            get { return _storyCount; }

            set
            {
                if (value <= 0)
                {
                    throw new ArgumentOutOfRangeException();
                }

                _storyCount = value;
            }
        }

        public int CycleCount
        {
            get { return _cycleCount; }

            set
            {
                if (value <= 0)
                {
                    throw new ArgumentOutOfRangeException();
                }

                _cycleCount = value;
            }
        }

        public string SourceId
        {
            get
            {
                return string.Format("poller{0}", Count);
            }
        }

        private IEnumerable<int> ExcludedIdsFromLeafProducerMonitor
        {
            get
            {
                return _leafProducerMonitor.GetCollectedLeaves().Select(l => l.StoryId);
            }
        }

        private IEnumerable<int> ExcludedIdsFromPersister
        {
            get
            {
                return _excludedStoryIds;
            }
        }

        private IEnumerable<int> ExcludedIds
        {
            get
            {
                return ExcludedIdsFromLeafProducerMonitor.Concat(ExcludedIdsFromPersister).Distinct();
            }
        }

        public IEnumerable<ISource> GetChildren()
        {
            using (var dbSession = _dbContext.OpenSession())
            {
                var query = dbSession.Stories.Query();
                var excludedIds = ExcludedIds.ToArray();
                if (excludedIds.Any())
                {
                    Log.DebugFormat("Excluding {0}", excludedIds.ToArrayString());
                    query = query.ExcludeIds(excludedIds);
                }

                // TODO: pre-poller to re-check everything that changed in the previous cycle
                var ids = query.OrderBy(StorySortField.ModificationAge.Asc())
                    .Take(Count)
                    .AsEnumerableOfIds()
                    .ToArray();
                Log.DebugFormat("Poller {0} found these stories before filtering: {1}", SourceId, ids.ToArrayString());
                return ids.Select(id => new StoryLeaf(_urlProvider.StoryUrl(id), id, this)).ToArray();
            }
        }

        public override bool Equals(object obj)
        {
            if (obj == null)
                return false;
            if (ReferenceEquals(this, obj))
                return true;
            if (obj.GetType() != typeof (Poller))
                return false;
            Poller other = (Poller) obj;
            return Count == other.Count;
        }

        public override int GetHashCode()
        {
            return Count.GetHashCode();
        }

        public override string ToString()
        {
            return string.Format("[Poller: ${0}]", SourceId);
        }

        private void OnStoryChecked(StoryCheckedMessage message)
        {
            if (message.Changes == UpdateResult.NoChanges)
            {
                Log.DebugFormat("Will exclude {0} next time", message.Story.StoryId);
                _excludedStoryIds.Add(message.Story.StoryId);
            }
        }

        private void OnLeafProducerStarting(LeafProducerStartingMessage message)
        {
            _cycles = (_cycles + 1)%CycleCount;
            Log.DebugFormat("Cycles = {0}", _cycles);
            if (_cycles == 0)
            {
                Log.DebugFormat("Resetting poller memory");
                _excludedStoryIds = new HashSet<int>();
            }
        }
    }
}
