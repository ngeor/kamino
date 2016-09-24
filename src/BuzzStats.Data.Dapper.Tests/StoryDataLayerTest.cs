using System.Data.Common;
using NUnit.Framework;
using BuzzStats.Data.TestsBase;

namespace BuzzStats.Data.Dapper.Tests
{
    [TestFixture]
    [Category("Integration")]
    public class StoryDataLayerTest : StoryDataLayerTestBase
    {
        protected override IStoryDataLayer CreateStoryDataLayer(DbConnection dbConnection)
        {
            return new StoryDataLayer(dbConnection);
        }
    }
}
