using System;
using BuzzStats.Data;
using BuzzStats.Web.Mvp;
using Moq;
using NUnit.Framework;

namespace BuzzStats.UnitTests.Web.Mvp
{
    [TestFixture]
    public class StoryCountControlPresenterTest : PresenterTestBase<ICountControlView>
    {
        [Test]
        public void Test()
        {
            PrepareMocks();

            var mockStoryQuery = new Mock<IStoryQuery>();
            mockStoryQuery.Setup(p => p.Count()).Returns(40);

            mockView.SetupSet(v => v.Count = 40);

            var presenter = new StoryCountControlPresenter(
                Mock.Of<IDbSession>(x => x.Stories == Mock.Of<IStoryDataLayer>(
                                             y => y.Query() == mockStoryQuery.Object)))
            {
                View = mockView.Object
            };
            mockView.Raise(v => v.ViewLoaded += null, EventArgs.Empty);

            VerifyMocks();
        }
    }
}