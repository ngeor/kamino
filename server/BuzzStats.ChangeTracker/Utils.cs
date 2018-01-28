using System.Collections.Generic;
using System.Linq;

namespace BuzzStats.ChangeTracker
{
    /// <summary>
    /// Utility and extension methods.
    /// </summary>
    static class Utils
    {
        /// <summary>
        /// Converts the collection to an empty enumerable if it is null.
        /// </summary>
        /// <typeparam name="T">The type of the elements.</typeparam>
        /// <param name="collection">This collection.</param>
        /// <returns>The collection unchanged if not null; an empty enumerable otherwise.</returns>
        public static IEnumerable<T> EnsureNotNull<T>(this IEnumerable<T> collection)
        {
            return collection ?? Enumerable.Empty<T>();
        }
    }
}
