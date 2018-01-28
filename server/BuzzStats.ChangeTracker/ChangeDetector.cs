using BuzzStats.DTOs;
using log4net;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace BuzzStats.ChangeTracker
{
    /// <summary>
    /// Detects changes on parsed stories.
    /// Uses an <see cref="IRepository"/> to track state.
    /// </summary>
    public class ChangeDetector : IChangeDetector
    {
        private static readonly ILog Log = LogManager.GetLogger(typeof(ChangeDetector));
        private readonly IRepository repository;

        public ChangeDetector(IRepository repository)
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
            var parsedVoters = parsedStory.Voters.EnsureNotNull();
            var dbVoters = dbStory.Voters.EnsureNotNull();
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
            var parsedComments = parsedStory.Comments.EnsureNotNull();
            var dbComments = dbStory.Comments.EnsureNotNull();
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
                        parsedComment.Comments.EnsureNotNull(),
                        dbComment.Comments.EnsureNotNull()))
                    {
                        yield return c;
                    }
                }
            }
        }

        private static IEnumerable<Comment> CommentHierarchy(Comment comment)
        {
            yield return comment;
            foreach (Comment childComment in comment.Comments.EnsureNotNull())
            {
                foreach (Comment c in CommentHierarchy(childComment))
                {
                    yield return c;
                }
            }
        }

        public async Task<IEnumerable<StoryEvent>> FindChangesAsync(Story parsedStory)
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
