// --------------------------------------------------------------------------------
// <copyright file="StoryDownloadedMessageTest.cs" company="Nikolaos Georgiou">
//   Copyright (C) Nikolaos Georgiou 2010-2015
// </copyright>
// <author>Nikolaos Georgiou</author>
// * Date: 2015/11/19
// * Time: 05:56:35
// --------------------------------------------------------------------------------

using Moq;
using NUnit.Framework;
using BuzzStats.Crawl;
using BuzzStats.Parsing;

namespace BuzzStats.Tests.Crawl
{
    [TestFixture]
    public class StoryDownloadedMessageTest
    {
        [Test]
        public void ShouldBeEqualWhenStoryAndLeafSourceAreEqual()
        {
            Story story = Mock.Of<Story>();
            ILeafSource leafSource = Mock.Of<ILeafSource>();
            StoryDownloadedMessage a = new StoryDownloadedMessage(story, leafSource);
            StoryDownloadedMessage b = new StoryDownloadedMessage(story, leafSource);
            Assert.AreEqual(a, b);
        }

        [Test]
        public void ShouldNotBeEqualWhenStoryIsDifferent()
        {
            Story story1 = Mock.Of<Story>();
            Story story2 = Mock.Of<Story>();
            ILeafSource leafSource = Mock.Of<ILeafSource>();
            StoryDownloadedMessage a = new StoryDownloadedMessage(story1, leafSource);
            StoryDownloadedMessage b = new StoryDownloadedMessage(story2, leafSource);
            Assert.AreNotEqual(a, b);
        }

        [Test]
        public void ShouldNotBeEqualWhenLeafSourceIsDifferent()
        {
            Story story = Mock.Of<Story>();
            ILeafSource leafSource1 = Mock.Of<ILeafSource>();
            ILeafSource leafSource2 = Mock.Of<ILeafSource>();
            StoryDownloadedMessage a = new StoryDownloadedMessage(story, leafSource1);
            StoryDownloadedMessage b = new StoryDownloadedMessage(story, leafSource2);
            Assert.AreNotEqual(a, b);
        }
    }
}