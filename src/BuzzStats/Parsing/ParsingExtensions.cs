// --------------------------------------------------------------------------------
// <copyright file="ParsingExtensions.cs" company="Nikolaos Georgiou">
//   Copyright (C) Nikolaos Georgiou 2010-2015
// </copyright>
// <author>Nikolaos Georgiou</author>
// * Date: 2015/11/28
// * Time: 17:03:47
// --------------------------------------------------------------------------------

using System;
using System.Linq;

namespace BuzzStats.Parsing
{
    public static class ParsingExtensions
    {
        public static DateTime? LastCommentedAt(this Story story)
        {
            return story == null ? (DateTime?) null : story.Comments.LastCommentedAt();
        }

        public static DateTime? LastCommentedAt(this Comment[] comments)
        {
            if (comments == null || comments.Length <= 0)
            {
                return null;
            }

            return comments.Max(c => c.LastCommentedAt());
        }

        public static DateTime? LastCommentedAt(this Comment comment)
        {
            if (comment == null)
            {
                return null;
            }

            DateTime? maxChildCreatedAt = comment.Comments.LastCommentedAt();
            DateTime myCreatedAt = comment.CreatedAt;
            if (maxChildCreatedAt.HasValue)
            {
                return myCreatedAt > maxChildCreatedAt ? myCreatedAt : maxChildCreatedAt;
            }

            return myCreatedAt;
        }
    }
}
