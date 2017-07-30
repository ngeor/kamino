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
#pragma warning disable 0649
        private StorageClient _storageClient;
        private Mock<ISessionFactory> _mockSessionFactory;
        private Mock<ISession> _mockSession;
        private Mock<IUpdater> _mockUpdater;
        private Mock<CommentRepository> _mockCommentRepository;
        private Mock<IMapper> _mockMapper;
#pragma warning restore 0649
        
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
        public void Save_ValidStory_UpdatesStory()
        {
            // arrange
            var story = CreateValidStory();
            
            // act
            _storageClient.Save(story);
            
            // assert
            _mockUpdater.Verify(u=>u.Save(_mockSession.Object, story));
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
        
        private Story CreateValidStory()
        {
            return new Story
            {
                StoryId = 42,
                Title = "test title",
                Username = "test user",
                Url = "http://localhost/",
                Voters = new[] {"test user", "test user 2"},
                CreatedAt = new DateTime(2017, 7, 20),
                Comments = new[]
                {
                    new Comment
                    {
                        CommentId = 42,
                        Username = "test user 2",
                        CreatedAt = new DateTime(2017, 7, 20),
                        Comments = new[]
                        {
                            new Comment
                            {
                                CommentId = 43,
                                Username = "test user",
                                CreatedAt = new DateTime(2017, 7, 20)
                            }
                        }
                    }
                }
            };
        }
    }
}