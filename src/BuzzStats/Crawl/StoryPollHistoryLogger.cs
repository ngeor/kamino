// --------------------------------------------------------------------------------
// <copyright file="StoryPollHistoryLogger.cs" company="Nikolaos Georgiou">
//   Copyright (C) Nikolaos Georgiou 2010-2015
// </copyright>
// <author>Nikolaos Georgiou</author>
// * Date: 2015/11/22
// * Time: 06:51:37
// --------------------------------------------------------------------------------

using NGSoftware.Common;
using NGSoftware.Common.Messaging;
using BuzzStats.Data;

namespace BuzzStats.Crawl
{
    public class StoryPollHistoryLogger
    {
        private readonly IDbContext _dbContext;

        public StoryPollHistoryLogger(IMessageBus messageBus, IDbContext dbContext)
        {
            _dbContext = dbContext;
            messageBus.Subscribe<StoryCheckedMessage>(OnStoryChecked);
        }

        void OnStoryChecked(StoryCheckedMessage message)
        {
            _dbContext.RunInTransaction(dbSession =>
            {
                dbSession.StoryPollHistories.Create(new StoryPollHistoryData
                {
                    Story = message.Story,
                    SourceId = message.LeafSource.SourceId,
                    CheckedAt = TestableDateTime.UtcNow,
                    HadChanges = (int) message.Changes
                });
            });
        }
    }
}