using System.Collections.Generic;
using BuzzStats.Parsing.DTOs;
using BuzzStats.WebApi.DTOs;

namespace BuzzStats.WebApi.Storage
{
    public interface IStorageClient
    {
        void Save(Story story);
        IList<CommentWithStory> GetRecentComments();
        IList<RecentActivity> GetRecentActivity();
    }
}