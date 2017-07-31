using System.Collections.Generic;
using BuzzStats.WebApi.Storage.Entities;

namespace BuzzStats.WebApi.Storage.Repositories
{
    public interface IStoryVoteRepository
    {
        IList<StoryVoteEntity> Get(StoryEntity story);
    }
}