//
//  Comment.cs
//
//  Author:
//       ngeor
//
//  Copyright (c) 2014 ngeor

using System;
using System.Runtime.Serialization;

namespace BuzzStats.Parsing
{
    /// <summary>
    /// A parsed comment.
    /// </summary>
    [DataContract]
    public class Comment
    {
        [DataMember]
        public int CommentId { get; set; }

        [DataMember]
        public string Username { get; set; }

        [DataMember]
        public DateTime CreatedAt { get; set; }

        [DataMember]
        public int VotesUp { get; set; }

        [DataMember]
        public int VotesDown { get; set; }

        [DataMember]
        public bool IsBuried { get; set; }

        [DataMember]
        public Comment[] Comments { get; set; }
    }
}