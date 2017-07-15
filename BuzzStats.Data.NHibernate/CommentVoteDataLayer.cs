// --------------------------------------------------------------------------------
// <copyright file="CommentVoteDataLayer.cs" company="Nikolaos Georgiou">
//      Copyright (C) Nikolaos Georgiou 2010-2015
// </copyright>
// <author>Nikolaos Georgiou</author>
// * Date: 2015/03/28
// * Time: 12:55:48
// --------------------------------------------------------------------------------

using System.Linq;
using NHibernate;
using NHibernate.Linq;
using BuzzStats.Data.NHibernate.Entities;

namespace BuzzStats.Data.NHibernate
{
    /// <summary>
    /// Implementation of the Comment Vote Data Layer around an NHibernate ISession.
    /// </summary>
    internal sealed class CommentVoteDataLayer : CoreDataClient, ICommentVoteDataLayer
    {
        public CommentVoteDataLayer(ISession session) : base(session)
        {
        }

        public void Create(CommentVoteData newCommentVote)
        {
            CommentVoteEntity commentVoteEntity = newCommentVote.ToEntity();
            commentVoteEntity.Comment = CoreData.SessionMap(newCommentVote.Comment);
            Session.Save(commentVoteEntity);
        }

        public bool Exists(CommentData ownerComment, int votesUp, int votesDown, bool isBuried)
        {
            // map comment into NHibernate session
            CommentEntity ownerCommentEntity = CoreData.SessionMap(ownerComment);

            return Session.Query<CommentVoteEntity>()
                       .Where(cv => cv.Comment == ownerCommentEntity
                                    && cv.VotesUp == votesUp
                                    && cv.VotesDown == votesDown
                                    && cv.IsBuried == isBuried)
                       .Select(cv => cv.Id)
                       .FirstOrDefault() > 0;
        }

        public CommentVoteData[] Query(CommentData comment)
        {
            IOrderedQueryable<CommentVoteEntity> query = from commentVote in Session.Query<CommentVoteEntity>()
                where commentVote.Comment == CoreData.SessionMap(comment)
                orderby commentVote.CreatedAt
                select commentVote;

            return query.ToData();
        }
    }
}