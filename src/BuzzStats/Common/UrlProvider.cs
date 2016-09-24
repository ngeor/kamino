// --------------------------------------------------------------------------------
// <copyright file="UrlProvider.cs" company="Nikolaos Georgiou">
//      Copyright (C) Nikolaos Georgiou 2010-2013
// </copyright>
// <author>Nikolaos Georgiou</author>
// * Date: 2013/09/04
// * Time: 1:27 μμ
// --------------------------------------------------------------------------------

namespace BuzzStats.Common
{
    public class UrlProvider : IUrlProvider
    {
        private const string BuzzUrl = "http://buzz.reality-tape.com/";

        public static string StoryUrl(int storyId, int commentId = 0)
        {
            return commentId > 0
                ? string.Format("{0}story.php?id={1}#wholecomment{2}", BuzzUrl, storyId, commentId)
                : string.Format("{0}story.php?id={1}", BuzzUrl, storyId);
        }

        public string StoryUrl(int storyId, int? commentId)
        {
            return StoryUrl(storyId, commentId.GetValueOrDefault());
        }
    }
}
