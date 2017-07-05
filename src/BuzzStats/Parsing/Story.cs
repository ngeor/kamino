//
//  Story.cs
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
    /// A parsed story.
    /// </summary>
    [DataContract]
    public class Story
    {
        [DataMember]
        public int StoryId { get; set; }

        [DataMember]
        public string Title { get; set; }

        [DataMember]
        public bool IsRemoved { get; set; }

        [DataMember]
        public int Category { get; set; }

        [DataMember]
        public string Url { get; set; }

        [DataMember]
        public DateTime CreatedAt { get; set; }

        [DataMember]
        public string Username { get; set; }

        [DataMember]
        public string[] Voters { get; set; }

        [DataMember]
        public Comment[] Comments { get; set; }
    }
}