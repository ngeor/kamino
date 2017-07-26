// --------------------------------------------------------------------------------
// <copyright file="StoryUrlProviderDSL.cs" company="Nikolaos Georgiou">
//   Copyright (C) Nikolaos Georgiou 2010-2015
// </copyright>
// <author>Nikolaos Georgiou</author>
// * Date: 2015/10/17
// * Time: 16:08:48
// --------------------------------------------------------------------------------

using BuzzStats.Common;
using Moq;

namespace BuzzStats.UnitTests.DSL
{
    public static class StoryUrlProviderDSL
    {
        public static IUrlProvider SetupStoryUrl(this IUrlProvider urlProvider, int storyId, string url)
        {
            Mock.Get<IUrlProvider>(urlProvider).Setup(p => p.StoryUrl(storyId, null)).Returns(url);
            return urlProvider;
        }
    }
}