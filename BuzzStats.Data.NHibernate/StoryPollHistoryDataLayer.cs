using System.Linq;
using NHibernate;
using NHibernate.Linq;
using NGSoftware.Common;
using BuzzStats.Data.NHibernate.Entities;

namespace BuzzStats.Data.NHibernate
{
    internal sealed class StoryPollHistoryDataLayer : CoreDataClient, IStoryPollHistoryDataLayer
    {
        public StoryPollHistoryDataLayer(ISession session) : base(session)
        {
        }

        public StoryPollHistoryData Create(StoryPollHistoryData storyPollHistory)
        {
            var entity = new StoryPollHistoryEntity
            {
                Story = CoreData.SessionMap(storyPollHistory.Story),
                HadChanges = storyPollHistory.HadChanges,
                SourceId = storyPollHistory.SourceId,
                CheckedAt = storyPollHistory.CheckedAt
            };

            Session.SaveOrUpdate(entity);
            return entity.ToData();
        }

        public int Count(DateRange dateRange)
        {
            StoryEntity storyAlias = null;
            var q = from e in Session.Query<StoryPollHistoryEntity>()
                where e.CheckedAt >= dateRange.StartDate.Value && e.CheckedAt < dateRange.StopDate.Value
                select e;
            return q.Count();
        }
    }
}