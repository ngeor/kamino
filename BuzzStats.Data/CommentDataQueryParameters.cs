// --------------------------------------------------------------------------------
// <copyright file="CommentDataQueryParameters.cs" company="Nikolaos Georgiou">
//      Copyright (C) Nikolaos Georgiou 2010-2013
// </copyright>
// <author>Nikolaos Georgiou</author>
// * Date: 2013/10/04
// * Time: 10:03 μμ
// --------------------------------------------------------------------------------

using NGSoftware.Common;
using NodaTime;

namespace BuzzStats.Data
{
    public class CommentDataQueryParameters : QueryParameters<CommentSortField>
    {
        public DateInterval CreatedAt { get; set; }

        public int? StoryId { get; set; }
        public string Username { get; set; }

        public override bool Equals(object obj)
        {
            if (!base.Equals(obj))
            {
                return false;
            }

            CommentDataQueryParameters that = obj as CommentDataQueryParameters;
            return that != null
                   && that.StoryId == StoryId
                   && that.CreatedAt == CreatedAt
                   && string.Equals(that.Username, Username);
        }

        public override int GetHashCode()
        {
            int result = base.GetHashCode();

            result = result * 13 + StoryId.GetValueOrDefault();
            result = result * 13 + (CreatedAt == null ? 0 : CreatedAt.GetHashCode());
            result = result * 13 + Username.SafeHashCode();
            return result;
        }

        public override string ToString()
        {
            return string.Format("{0} StoryId={1} CreatedAt={2} Username={3}", base.ToString(), StoryId, CreatedAt,
                Username);
        }
    }
}