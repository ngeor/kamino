using System.Collections.Generic;
using BuzzStats.WebApi.Storage.Entities;

namespace BuzzStats.WebApi.Storage.Repositories
{
    public interface ICommentRepository
    {
        CommentEntity GetByCommentId(int commentId);
        IList<CommentEntity> GetRecent();
    }
}