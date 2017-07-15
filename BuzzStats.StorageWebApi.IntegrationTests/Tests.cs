using System;
using System.Net;
using System.Net.Http;
using System.Threading.Tasks;
using BuzzStats.StorageWebApi.DTOs;
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
            _webhost = Program.Start();
        }

        [TearDown]
        public void TearDown()
        {
            _webhost.Dispose();
        }

        [Test]
        public async Task CreateStory_WithNoTitle_IsBadRequest()
        {
            Story story = new Story();
            HttpClient httpClient = new HttpClient();
            var httpResponseMessage = await httpClient.PostAsJsonAsync("http://localhost:9003/api/story", story);
            Assert.NotNull(httpResponseMessage);
            Assert.AreEqual(false, httpResponseMessage.IsSuccessStatusCode);
            Assert.AreEqual(HttpStatusCode.BadRequest, httpResponseMessage.StatusCode);
        }

        [Test]
        public async Task CreateStory_WithCorrectData_IsSuccessful()
        {
            // TODO StoryId = 0 and CreatedAt = 0000 should also fail with 400
            Story story = new Story
            {
                Title = "test title",
                Username = "test user",
                Url = "http://localhost/",
                Voters = new[] {"test user", "test user 2"},
                Comments = new[]
                {
                    new Comment
                    {
                        CommentId = 42,
                        Username = "test user 2",
                        Comments = new[]
                        {
                            new Comment
                            {
                                CommentId = 43,
                                Username = "test user"
                            }
                        }
                    }
                }
            };

            HttpClient httpClient = new HttpClient();
            var httpResponseMessage = await httpClient.PostAsJsonAsync("http://localhost:9003/api/story", story);
            Assert.NotNull(httpResponseMessage);
            Assert.AreEqual(true, httpResponseMessage.IsSuccessStatusCode);
        }
    }
}