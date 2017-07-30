using System;
using System.Collections.Generic;
using System.Web.Http;
using AutoMapper;
using BuzzStats.WebApi.DTOs;
using BuzzStats.WebApi.Storage;
using BuzzStats.WebApi.Storage.Entities;
using BuzzStats.WebApi.Storage.Repositories;
using BuzzStats.WebApi.UnitTests.TestHelpers;
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
            MockHelper.InjectMocks(this);
            _mockSessionFactory.Setup(f => f.OpenSession()).Returns(_mockSession.Object);
            _storageClient = MockHelper.Create<StorageClient>(this);
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