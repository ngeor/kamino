// --------------------------------------------------------------------------------
// <copyright file="IParser.cs" company="Nikolaos Georgiou">
//      Copyright (C) Nikolaos Georgiou 2010-2013
// </copyright>
// <author>Nikolaos Georgiou</author>
// * Date: 2013/10/04
// * Time: 5:03 μμ
// --------------------------------------------------------------------------------

using System.Collections.Generic;

namespace BuzzStats.Parsing
{
    /// <summary>
    /// Defines the parser.
    /// </summary>
    public interface IParser
    {
        Story ParseStoryPage(string storyPageContents, int requestedStoryId);
        IEnumerable<StoryListingSummary> ParseListingPage(string html);
    }
}
