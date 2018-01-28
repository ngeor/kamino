using System.Collections.Generic;
using System.Threading.Tasks;
using BuzzStats.DTOs;

namespace BuzzStats.ChangeTracker
{
    /// <summary>
    /// Detects changes in a parsed story.
    /// </summary>
    public interface IChangeDetector
    {
        /// <summary>
        /// Finds changes of the given story and updates its internal state.
        /// </summary>
        /// <param name="parsedStory">A parsed story.</param>
        /// <returns>A collection of change events.</returns>
        Task<IEnumerable<StoryEvent>> FindChangesAsync(Story parsedStory);
    }
}