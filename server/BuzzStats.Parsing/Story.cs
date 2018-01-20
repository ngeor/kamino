using System;

namespace BuzzStats.Parsing.DTOs
{
    public class Story
    {
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