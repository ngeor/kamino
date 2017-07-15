using System;

namespace BuzzStats.StorageWebApi.DTOs
{
    public class Story
    {
        public int StoryId { get; set; }

        public string Title { get; set; }

        public string Url { get; set; }

        public string Username { get; set; }

        public DateTime CreatedAt { get; set; }

        public int Category { get; set; }
        
        public string[] Voters { get; set; }

        public Comment[] Comments { get; set; }

        public bool IsRemoved { get; set; }
    }
}