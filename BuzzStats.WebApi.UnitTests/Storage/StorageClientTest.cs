using System.Web.Http;
using BuzzStats.WebApi.Storage;
using Moq;
using NHibernate;
using NUnit.Framework;

namespace BuzzStats.WebApi.UnitTests.Storage
{
    [TestFixture]
    public class StorageClientTest
    {
        private StorageClient _storageClient;
        private Mock<ISessionFactory> _mockSessionFactory;
        private Mock<IUpdater> _mockUpdater;

        [SetUp]
        public void SetUp()
        {
            _mockSessionFactory = new Mock<ISessionFactory>();
            _mockUpdater = new Mock<IUpdater>();
            _storageClient = new StorageClient(_mockSessionFactory.Object, _mockUpdater.Object);    
        }
        
        [Test]
        public void Post_NullStory_ThrowsException()
        {
            Assert.Throws<HttpResponseException>(() =>
            {
                _storageClient.Save(null);
            });
        }
    }
}