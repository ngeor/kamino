using BuzzStats.WebApi.DTOs;
using NHibernate;

namespace BuzzStats.WebApi.Storage
{
    public class Updater : IUpdater
    {
        private readonly IStoryUpdater _storyUpdater;
        private readonly IStoryVoteUpdater _storyVoteUpdater;
        private readonly ICommentUpdater _commentUpdater;

        public Updater(IStoryUpdater storyUpdater, IStoryVoteUpdater storyVoteUpdater, ICommentUpdater commentUpdater)
        {
            _storyUpdater = storyUpdater;
            _storyVoteUpdater = storyVoteUpdater;
            _commentUpdater = commentUpdater;
        }

        public virtual void Save(ISession session, Story story)
        {
            using (var transaction = session.BeginTransaction())
            {
                var storyEntity = _storyUpdater.Save(session, story);
                _storyVoteUpdater.SaveStoryVotes(session, story, storyEntity);
                _commentUpdater.SaveComments(session, story, storyEntity);
                transaction.Commit();
            }
        }
    }
}