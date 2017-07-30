using System;
using BuzzStats.WebApi.DTOs;
using BuzzStats.WebApi.IoC;
using BuzzStats.WebApi.Storage;
using NUnit.Framework;

namespace BuzzStats.StorageWebApi.IntegrationTests
{
    [TestFixture]
    public class StorageClientTest
    {
        private IStorageClient _storageClient;

        [SetUp]
        public void SetUp()
        {
            _storageClient = ContainerHolder.Container.GetInstance<IStorageClient>();
        }

        [TearDown]
        public void TearDown()
        {
        }

        [Test]
        public void Save_WithNullStory_ThrowsArgumentNullException()
        {
            Assert.Throws<ArgumentNullException>(() =>
            {
                _storageClient.Save(null);
            });
        }

        [Test]
        public void Save_WithNoTitle_ThrowsArgumentException()
        {
            // arrange
            Story story = CreateValidStory();
            story.Title = null;
            
            // act & assert
            Assert.Throws<ArgumentException>(() =>
            {
                _storageClient.Save(story);
            });
        }
        
        [Test]
        public void Save_WithNoStoryId_ThrowsArgumentException()
        {
            // arrange
            Story story = CreateValidStory();
            story.StoryId = 0;
            
            // act & assert
            Assert.Throws<ArgumentException>(() =>
            {
                _storageClient.Save(story);
            });
        }

        [Test]
        public void Save_WithNoCreationDate_ThrowsArgumentException()
        {
            // arrange
            Story story = CreateValidStory();
            story.CreatedAt = default(DateTime);
            
            // act & assert
            Assert.Throws<ArgumentException>(() =>
            {
                _storageClient.Save(story);
            });
        }

        [Test]
        public void Save_WithCorrectData_IsSuccessful()
        {
            // TODO StoryId = 0 and CreatedAt = 0000 should also fail with 400
            // arrange
            Story story = CreateValidStory();
            
            // act
            _storageClient.Save(story);
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