using System;
using System.Net;
using System.Net.Http;
using System.Threading.Tasks;
using BuzzStats.WebApi.DTOs;
using NUnit.Framework;

namespace BuzzStats.StorageWebApi.IntegrationTests
{
    [TestFixture]
    public class Tests
    {
        private IDisposable _webhost;

        [SetUp]
        public void SetUp()
        {
            //_webhost = Program.Start();
        }

        [TearDown]
        public void TearDown()
        {
            _webhost.Dispose();
        }

        [Test]
        public async Task CreateStory_WithNoTitle_IsBadRequest()
        {
            // arrange
            Story story = CreateValidStory();
            story.Title = null;
            HttpClient httpClient = new HttpClient();
            
            // act
            var httpResponseMessage = await httpClient.PostAsJsonAsync("http://localhost:9003/api/story", story);
            
            // assert
            Assert.NotNull(httpResponseMessage);
            Assert.AreEqual(false, httpResponseMessage.IsSuccessStatusCode);
            Assert.AreEqual(HttpStatusCode.BadRequest, httpResponseMessage.StatusCode);
        }
        
        [Test]
        public async Task CreateStory_WithNoStoryId_IsBadRequest()
        {
            // arrange
            Story story = CreateValidStory();
            story.StoryId = 0;
            HttpClient httpClient = new HttpClient();
            
            // act
            var httpResponseMessage = await httpClient.PostAsJsonAsync("http://localhost:9003/api/story", story);
            
            // assert
            Assert.NotNull(httpResponseMessage);
            Assert.AreEqual(false, httpResponseMessage.IsSuccessStatusCode);
            Assert.AreEqual(HttpStatusCode.BadRequest, httpResponseMessage.StatusCode);
        }

        [Test]
        public async Task CreateStory_WithNoCreationDate_IsBadRequest()
        {
            // arrange
            Story story = CreateValidStory();
            story.CreatedAt = default(DateTime);
            HttpClient httpClient = new HttpClient();
            
            // act
            var httpResponseMessage = await httpClient.PostAsJsonAsync("http://localhost:9003/api/story", story);
            
            // assert
            Assert.NotNull(httpResponseMessage);
            Assert.AreEqual(false, httpResponseMessage.IsSuccessStatusCode);
            Assert.AreEqual(HttpStatusCode.BadRequest, httpResponseMessage.StatusCode);
        }

        [Test]
        public async Task CreateStory_WithCorrectData_IsSuccessful()
        {
            // TODO StoryId = 0 and CreatedAt = 0000 should also fail with 400
            // arrange
            Story story = CreateValidStory();
            HttpClient httpClient = new HttpClient();
            
            // act
            var httpResponseMessage = await httpClient.PostAsJsonAsync("http://localhost:9003/api/story", story);
            
            // assert
            Assert.NotNull(httpResponseMessage);
            Assert.AreEqual(true, httpResponseMessage.IsSuccessStatusCode);
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