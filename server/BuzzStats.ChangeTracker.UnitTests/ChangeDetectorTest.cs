using BuzzStats.ChangeTracker.Mongo;
using BuzzStats.DTOs;
using FluentAssertions;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using Moq;
using System;
using System.Linq;

namespace BuzzStats.ChangeTracker.UnitTests
{
    [TestClass]
    public class ChangeDetectorTest
    {
        private Mock<IRepository> repositoryMock;
        private ChangeDetector changeDetector;

        [TestInitialize]
        public void SetUp()
        {
            repositoryMock = new Mock<IRepository>();
            changeDetector = new ChangeDetector(repositoryMock.Object);
        }

        [TestMethod]
        public void WhenStoryDoesNotExist_ItShouldReportNewStory()
        {
            // arrange
            var msg = new Story
            {
                StoryId = 42,
                CreatedAt = new DateTime(2018, 1, 26)
            };

            repositoryMock.Setup(p => p.Load(42)).ReturnsAsync((Story)null);

            var expectedMsg = new StoryEvent
            {
                StoryId = 42,
                CreatedAt = new DateTime(2018, 1, 26),
                EventType = StoryEventType.StoryCreated
            };

            // act
            var result = changeDetector.FindChangesAsync(msg).Result.ToArray();

            // assert
            result.Should().BeEquivalentTo(new[] { expectedMsg });
            repositoryMock.Verify(p => p.Save(msg));
        }

        [TestMethod]
        public void WhenStoryExists_ButHasNoChanges_ItShouldReportNoEvents()
        {
            var msg = new Story
            {
                StoryId = 42,
                CreatedAt = new DateTime(2018, 1, 26)
            };

            repositoryMock.Setup(p => p.Load(42)).ReturnsAsync(msg);

            // act
            var result = changeDetector.FindChangesAsync(msg).Result.ToArray();

            // assert
            result.Should().BeEmpty();
            repositoryMock.Verify(p => p.Save(msg), Times.Never());
        }


        [TestMethod]
        public void WhenStoryExists_ItShouldReportNewVotes()
        {
            var msg = new Story
            {
                StoryId = 42,
                Voters = new[]
                {
                    "voter1", "voter2"
                }
            };

            var dbStory = new Story
            {
                StoryId = 42,
                Voters = new[]
                {
                    "voter1"
                }
            };

            repositoryMock.Setup(p => p.Load(42)).ReturnsAsync(dbStory);

            // act
            var result = changeDetector.FindChangesAsync(msg).Result.ToArray();

            // assert
            result.Should().BeEquivalentTo(new[]
            {
                new StoryEvent
                {
                    StoryId = 42,
                    EventType = StoryEventType.StoryVoted,
                    Username = "voter2"
                }
            });

            repositoryMock.Verify(p => p.Save(msg));
        }

        [TestMethod]
        public void WhenStoryExists_ItShouldReportNewComments()
        {
            var msg = new Story
            {
                StoryId = 42,
                Comments = new[]
                {
                    new Comment
                    {
                        CommentId = 100,
                        Username = "user1"
                    }
                }
            };

            var dbStory = new Story
            {
                StoryId = 42
            };

            repositoryMock.Setup(p => p.Load(42)).ReturnsAsync(dbStory);

            // act
            var result = changeDetector.FindChangesAsync(msg).Result.ToArray();

            // assert
            result.Should().BeEquivalentTo(new[]
            {
                new StoryEvent
                {
                    StoryId = 42,
                    EventType = StoryEventType.CommentCreated,
                    Username = "user1"
                }
            });

            repositoryMock.Verify(p => p.Save(msg));
        }

        [TestMethod]
        public void WhenStoryExists_ItShouldReportNewVotesAndNewComments()
        {
            var msg = new Story
            {
                StoryId = 42,
                Voters = new[]
                {
                    "voter1", "voter2"
                },
                Comments = new[]
                {
                    new Comment
                    {
                        CommentId = 100,
                        Username = "hello"
                    },
                    new Comment
                    {
                        CommentId = 200,
                        Username = "bye"
                    }
                }
            };

            var dbStory = new Story
            {
                StoryId = 42,
                Voters = new[]
                {
                    "voter1"
                },
                Comments = new[]
                {
                    new Comment
                    {
                        CommentId = 100
                    }
                }
            };

            repositoryMock.Setup(p => p.Load(42)).ReturnsAsync(dbStory);

            // act
            var result = changeDetector.FindChangesAsync(msg).Result.ToArray();

            // assert
            result.Should().BeEquivalentTo(new[]
            {
                new StoryEvent
                {
                    StoryId = 42,
                    EventType = StoryEventType.StoryVoted,
                    Username = "voter2"
                }, new StoryEvent
                {
                    StoryId = 42,
                    EventType = StoryEventType.CommentCreated,
                    Username = "bye"
                }
            });

            repositoryMock.Verify(p => p.Save(msg));
        }

        [TestMethod]
        public void WhenStoryExists_ItShouldReportNewChildComments()
        {
            var msg = new Story
            {
                StoryId = 42,
                Comments = new[]
                {
                    new Comment
                    {
                        CommentId = 100,
                        Username = "user1",
                        Comments = new[]
                        {
                            new Comment
                            {
                                CommentId = 200,
                                Username = "user2"
                            }
                        }
                    }
                }
            };

            var dbStory = new Story
            {
                StoryId = 42
            };

            repositoryMock.Setup(p => p.Load(42)).ReturnsAsync(dbStory);

            // act
            var result = changeDetector.FindChangesAsync(msg).Result.ToArray();

            // assert
            result.Should().BeEquivalentTo(new[]
            {
                new StoryEvent
                {
                    StoryId = 42,
                    EventType = StoryEventType.CommentCreated,
                    Username = "user1"
                },
                new StoryEvent
                {
                    StoryId = 42,
                    EventType = StoryEventType.CommentCreated,
                    Username = "user2"
                }
            });

            repositoryMock.Verify(p => p.Save(msg));
        }

        [TestMethod]
        public void WhenStoryExists_ItShouldReportNewChildCommentsOnExistingParentComment()
        {
            var msg = new Story
            {
                StoryId = 42,
                Comments = new[]
                {
                    new Comment
                    {
                        CommentId = 100,
                        Username = "user1",
                        Comments = new[]
                        {
                            new Comment
                            {
                                CommentId = 200,
                                Username = "user2"
                            }
                        }
                    }
                }
            };

            var dbStory = new Story
            {
                StoryId = 42,
                Comments = new[]
                {
                    new Comment
                    {
                        CommentId = 100,
                        Username = "user1"
                    }
                }
            };

            repositoryMock.Setup(p => p.Load(42)).ReturnsAsync(dbStory);

            // act
            var result = changeDetector.FindChangesAsync(msg).Result.ToArray();

            // assert
            result.Should().BeEquivalentTo(new[]
            {
                new StoryEvent
                {
                    StoryId = 42,
                    EventType = StoryEventType.CommentCreated,
                    Username = "user2"
                }
            });

            repositoryMock.Verify(p => p.Save(msg));
        }

        [TestMethod]
        [Ignore("Needs connection to Mongo")] // Needs connection to Mongo
        public void TestMongo()
        {
            var story = new Story
            {
                StoryId = 42,
                Title = "hello",
                CreatedAt = new DateTime(2018, 1, 26, 0, 0, 0, DateTimeKind.Utc)
            };

            var repo = new Repository();

            repo.Save(story).Wait();

            var loadedStory = repo.Load(42).Result;
            loadedStory.Should().Be(story);
        }
    }
}
