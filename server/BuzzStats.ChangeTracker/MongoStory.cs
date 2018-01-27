using System;
using BuzzStats.Parsing.DTOs;
using MongoDB.Bson;

namespace BuzzStats.ChangeTracker
{
    public class MongoStory
    {
        public ObjectId Id { get; set; }

        public int StoryId { get; set; }

        public string Title { get; set; }

        public bool IsRemoved { get; set; }

        public int Category { get; set; }

        public string Url { get; set; }

        public DateTime CreatedAt { get; set; }

        public string Username { get; set; }

        public string[] Voters { get; set; }

        public Comment[] Comments { get; set; }
    }
}
