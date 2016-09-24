// --------------------------------------------------------------------------------
// <copyright file="StoryPollHistoryDataLayerTest.cs" company="Nikolaos Georgiou">
//   Copyright (C) Nikolaos Georgiou 2010-2015
// </copyright>
// <author>Nikolaos Georgiou</author>
// * Date: 2015/03/29
// * Time: 12:06:42
// --------------------------------------------------------------------------------

using System;
using NUnit.Framework;
using NGSoftware.Common;

namespace BuzzStats.Data.NHibernate.Tests
{
    [TestFixture]
    [Category("Integration")]
    public class StoryPollHistoryDataLayerTest : LayerTestBase
    {
        [Test]
        public void TestCreate()
        {
            var storyData = DbSession.Stories.Create(new StoryData
            {
                StoryId = 42,
                Title = "my story",
                Username = "ngeor",
                TotalChecks = 1,
                CreatedAt = new DateTime(2015, 1, 2)
            });

            var storyPollHistory = DbSession.StoryPollHistories.Create(new StoryPollHistoryData
            {
                Story = storyData,
                SourceId = "poller",
                CheckedAt = new DateTime(2015, 3, 29),
                HadChanges = 1
            });

            Assert.IsNotNull(storyPollHistory);
            FlushAndReopenSession();

            Assert.AreEqual(1, DbSession.StoryPollHistories.Count(
                DateRange.Create(new DateTime(2015, 1, 2), new DateTime(2015, 3, 30))));
        }
    }
}
