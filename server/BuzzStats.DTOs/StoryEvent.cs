using System;
using System.Collections.Generic;

namespace BuzzStats.DTOs
{
    public class StoryEvent
    {
        public StoryEventType EventType { get; set; }
        public int StoryId { get; set; }
        public DateTime CreatedAt { get; set; }
        public string Username { get; set; }

        public override bool Equals(object obj)
        {
            var @event = obj as StoryEvent;
            return @event != null &&
                   EventType == @event.EventType &&
                   StoryId == @event.StoryId &&
                   CreatedAt == @event.CreatedAt &&
                   Username == @event.Username;
        }

        public override int GetHashCode()
        {
            var hashCode = 2008694869;
            hashCode = hashCode * -1521134295 + EventType.GetHashCode();
            hashCode = hashCode * -1521134295 + StoryId.GetHashCode();
            hashCode = hashCode * -1521134295 + CreatedAt.GetHashCode();
            hashCode = hashCode * -1521134295 + EqualityComparer<string>.Default.GetHashCode(Username);
            return hashCode;
        }
    }
}
