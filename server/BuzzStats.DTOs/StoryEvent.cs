using System;

namespace BuzzStats.DTOs
{
    public class StoryEvent
    {
        public StoryEventType EventType { get; set; }
        public int StoryId { get; set; }
        public DateTime CreatedAt { get; set; }
        public string Username { get; set; }
    }
}
