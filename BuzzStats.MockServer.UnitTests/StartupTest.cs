using Moq;
using NUnit.Framework;
using Owin;

namespace BuzzStats.MockServer.UnitTests
{
    [TestFixture]
    public class StartupTest
    {
        [Test]
        public void UsesMiddleware()
        {
            // arrange
            IAppBuilder appBuilder = Mock.Of<IAppBuilder>();
            Startup startup = new Startup();
            
            // act
            startup.Configuration(appBuilder);
            
            // assert
            Mock.Get(appBuilder).Verify(a => a.Use(typeof(MockBuzzMiddleware)));
        }
    }
}