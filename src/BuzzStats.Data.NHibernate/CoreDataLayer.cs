using System;
using System.Linq;
using NHibernate;
using NHibernate.Linq;
using BuzzStats.Data.NHibernate.Entities;

namespace BuzzStats.Data.NHibernate
{
    public sealed class CoreDataLayer : SessionClient
    {
        public CoreDataLayer(ISession session) : base(session)
        {
        }

        public CommentEntity LoadCommentEntity(int commentBusinessId)
        {
            if (commentBusinessId <= 0)
            {
                throw new InvalidCommentIdException();
            }

            CommentEntity commentEntity = Session
                .Query<CommentEntity>()
                .FirstOrDefault(c => c.CommentId == commentBusinessId);
            return commentEntity;
        }

        public StoryEntity LoadStoryEntity(int storyBusinessId)
        {
            if (storyBusinessId <= 0)
            {
                throw new InvalidStoryIdException();
            }

            try
            {
                StoryEntity story = Session.Query<StoryEntity>().FirstOrDefault(s => s.StoryId == storyBusinessId);
                return story;
            }
            catch (Exception ex)
            {
                throw new InvalidOperationException("LoadStory " + storyBusinessId + " failed", ex);
            }
        }

        public StoryEntity SessionMap(StoryData storyData)
        {
            if (storyData == null)
            {
                throw new ArgumentNullException("storyData");
            }

            if (storyData.StoryId <= 0)
            {
                throw new InvalidStoryIdException();
            }

            StoryEntity storyEntity = LoadStoryEntity(storyData.StoryId);
            if (storyEntity == null)
            {
                throw new ObjectNotFoundException(storyData.StoryId, typeof (StoryEntity));
            }

            storyEntity = storyData.ToEntity(storyEntity);
            return storyEntity;
        }

        /// <summary>
        /// Converts a <see cref="CommentData"/> instance into a <see cref="CommentEntity"/> instance,
        /// checking first if it already exists in the current NHibernate Session.
        /// This avoids exceptions from NHibernate regarding non unique objects.
        /// </summary>
        /// <param name="comment">
        /// The comment to convert.
        /// </param>
        /// <returns>
        /// The mapped Comment Entity instance.
        /// </returns>
        /// <exception cref="ArgumentNullException">
        /// if <paramref name="comment"/> is <c>null</c>.
        /// </exception>
        public CommentEntity SessionMap(CommentData comment)
        {
            return SessionMap(comment, allowNull: false);
        }

        /// <summary>
        /// Converts a <see cref="CommentData"/> instance into a <see cref="CommentEntity"/> instance,
        /// checking first if it already exists in the current NHibernate Session.
        /// This avoids exceptions from NHibernate regarding non unique objects.
        /// </summary>
        /// <param name="comment">
        /// The comment to convert.
        /// </param>
        /// <param name="allowNull">
        /// Determines if <c>null</c> is allowed for the <paramref name="comment"/>.
        /// </param>
        /// <returns>
        /// The mapped Comment Entity instance.
        /// </returns>
        public CommentEntity SessionMap(CommentData comment, bool allowNull)
        {
            if (comment == null)
            {
                if (allowNull)
                {
                    return null;
                }

                throw new ArgumentNullException("comment");
            }

            CommentEntity result = LoadCommentEntity(comment.CommentId);
            if (result == null)
            {
                throw new ObjectNotFoundException(comment.CommentId, typeof (CommentEntity));
            }

            return comment.ToEntity(result);
        }
    }
}
