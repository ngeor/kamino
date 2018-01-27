using BuzzStats.DTOs;
using log4net;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace BuzzStats.ChangeTracker
{
    public class EventProducer : IEventProducer
    {
        private static readonly ILog Log = LogManager.GetLogger(typeof(EventProducer));
        private readonly IRepository repository;

        public EventProducer(IRepository repository)
        {
            this.repository = repository ?? throw new ArgumentNullException(nameof(repository));
        }

        private ICollection<StoryEvent> CollectEvents(Story parsedStory, Story dbStory)
        {
            if (dbStory == null)
            {
                // completely new story
                return new[]
                {
                    new StoryEvent
                    {
                        StoryId = parsedStory.StoryId,
                        CreatedAt = parsedStory.CreatedAt,
                        EventType = StoryEventType.StoryCreated
                    }
                };
            }

            return CollectNewStoryVotes(parsedStory, dbStory)
                .Concat(CollectNewComments(parsedStory, dbStory))
                .ToArray();
        }

        private IEnumerable<StoryEvent> CollectNewStoryVotes(Story parsedStory, Story dbStory)
        {
            var parsedVoters = parsedStory.Voters ?? Enumerable.Empty<string>();
            var dbVoters = dbStory.Voters ?? Enumerable.Empty<string>();
            var newVotes = parsedVoters.Where(v => !dbVoters.Contains(v));
            return newVotes.Select(v => new StoryEvent
            {
                StoryId = parsedStory.StoryId,
                Username = v,
                EventType = StoryEventType.StoryVoted
            });
        }

        private IEnumerable<StoryEvent> CollectNewComments(Story parsedStory, Story dbStory)
        {
            var parsedComments = parsedStory.Comments ?? Enumerable.Empty<Comment>();
            var dbComments = dbStory.Comments ?? Enumerable.Empty<Comment>();
            var newComments = CollectNewComments(parsedComments, dbComments);
            return newComments.Select(c => new StoryEvent
            {
                StoryId = parsedStory.StoryId,
                Username = c.Username,
                EventType = StoryEventType.CommentCreated
            });
        }

        private IEnumerable<Comment> CollectNewComments(IEnumerable<Comment> parsedComments, IEnumerable<Comment> dbComments)
        {
            foreach (var parsedComment in parsedComments)
            {
                var dbComment = dbComments.SingleOrDefault(c => c.CommentId == parsedComment.CommentId);
                if (dbComment == null)
                {
                    // parsed comment is new
                    foreach (Comment c in CommentHierarchy(parsedComment))
                    {
                        yield return c;
                    }
                }
                else
                {
                    // parsed comment exists
                    foreach (Comment c in CollectNewComments(
                        parsedComment.Comments ?? Enumerable.Empty<Comment>(),
                        dbComment.Comments ?? Enumerable.Empty<Comment>()))
                    {
                        yield return c;
                    }
                }
            }
        }

        private static IEnumerable<Comment> CommentHierarchy(Comment comment)
        {
            yield return comment;
            foreach (Comment childComment in comment.Comments ?? Enumerable.Empty<Comment>())
            {
                foreach (Comment c in CommentHierarchy(childComment))
                {
                    yield return c;
                }
            }
        }

        public async Task<IEnumerable<StoryEvent>> CreateEventsAsync(Story parsedStory)
        {
            var dbStory = await repository.Load(parsedStory.StoryId);
            var events = CollectEvents(parsedStory, dbStory);
            if (events.Any())
            {
                Log.InfoFormat("Detected changes for story {0}", parsedStory.StoryId);
                await repository.Save(parsedStory);
            }
            else
            {
                Log.InfoFormat("No changes for story {0}", parsedStory.StoryId);
            }

            return events;
        }
    }
}
