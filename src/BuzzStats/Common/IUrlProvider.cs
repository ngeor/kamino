// --------------------------------------------------------------------------------
// <copyright file="IUrlProvider.cs" company="Nikolaos Georgiou">
//   Copyright (C) Nikolaos Georgiou 2010-2015
// </copyright>
// <author>Nikolaos Georgiou</author>
// * Date: 2015/10/17
// * Time: 10:18:54
// --------------------------------------------------------------------------------

namespace BuzzStats.Common
{
    public interface IUrlProvider
    {
        string StoryUrl(int storyId, int? commentId = null);
    }
}
