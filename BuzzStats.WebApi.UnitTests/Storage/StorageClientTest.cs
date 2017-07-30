using System;
using System.Collections.Generic;
using System.Web.Http;
using AutoMapper;
using BuzzStats.WebApi.DTOs;
using BuzzStats.WebApi.Storage;
using BuzzStats.WebApi.Storage.Entities;
using BuzzStats.WebApi.Storage.Repositories;
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
        private Mock<ISession> _mockSession;
        private Mock<IUpdater> _mockUpdater;
        private Mock<CommentRepository> _mockCommentRepository;
        private Mock<IMapper> _mockMapper;

        [SetUp]
        public void SetUp()
        {
            _mockSessionFactory = new Mock<ISessionFactory>();
            _mockSession = new Mock<ISession>();
            _mockSessionFactory.Setup(f => f.OpenSession()).Returns(_mockSession.Object);
            
            _mockUpdater = new Mock<IUpdater>();
            _mockCommentRepository = new Mock<CommentRepository>();
            _mockMapper = new Mock<IMapper>();
            
            _storageClient = new StorageClient(
                _mockSessionFactory.Object,
                _mockUpdater.Object,
                _mockCommentRepository.Object,
                _mockMapper.Object);    
        }
        
        [Test]
        public void Save_NullStory_ThrowsException()
        {
            Assert.Throws<ArgumentNullException>(() =>
            {
                _storageClient.Save(null);
            });
        }

        [Test]
        public void GetRecentComments()
        {
            // arrange
            var commentEntity = new CommentEntity
            {
                CommentId = 42,
                Story = new StoryEntity
                {
                    StoryId = 1
                }
            };
            
            _mockCommentRepository.Setup(r => r.GetRecent(_mockSession.Object))
                .Returns(new List<CommentEntity>
                {
                    commentEntity
                });

            var commentWithStory = new CommentWithStory
            {
                CommentId = 42,
                StoryId = 1
            };
            _mockMapper.Setup(m => m.Map<CommentWithStory>(commentEntity))
                .Returns(commentWithStory);
            
            // act
            var commentWithStories = _storageClient.GetRecentComments();
            
            // assert
            CollectionAssert.AreEqual(new[] { commentWithStory }, commentWithStories);
        }
    }
}