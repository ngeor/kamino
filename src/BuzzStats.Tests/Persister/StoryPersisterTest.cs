//
//  StoryPersisterTest.cs
//
//  Author:
//       ngeor
//
//  Copyright (c) 2014 ngeor

using System;
using Moq;
using NUnit.Framework;
using NGSoftware.Common;
using BuzzStats.Data;
using BuzzStats.Parsing;
using BuzzStats.Persister;

namespace BuzzStats.Tests.Persister
{
    [TestFixture]
    public class StoryPersisterTest
    {
        private Mock<IStoryDataLayer> _mockStoryDataLayer;
        private Mock<ICommentDataLayer> _mockCommentDataLayer;
        private Mock<IStoryVoteDataLayer> _mockStoryVoteDataLayer;
        private Mock<ICommentVoteDataLayer> _mockCommentVoteDataLayer;
        private IDbSession _dbSession;

        [SetUp]
        public void SetUp()
        {
            TestableDateTime.UtcNowStrategy = () => new DateTime(2014, 2, 13);

            _mockStoryDataLayer = new Mock<IStoryDataLayer>(MockBehavior.Strict);
            _mockCommentDataLayer = new Mock<ICommentDataLayer>(MockBehavior.Strict);
            _mockStoryVoteDataLayer = new Mock<IStoryVoteDataLayer>(MockBehavior.Strict);
            _mockCommentVoteDataLayer = new Mock<ICommentVoteDataLayer>(MockBehavior.Strict);

            _dbSession = Mock.Of<IDbSession>(s => s.Stories == _mockStoryDataLayer.Object
                && s.Comments == _mockCommentDataLayer.Object
                && s.StoryVotes == _mockStoryVoteDataLayer.Object
                && s.CommentVotes == _mockCommentVoteDataLayer.Object);
        }

        [TearDown]
        public void TearDown()
        {
            TestableDateTime.UtcNowStrategy = null;
        }

        [Test]
        public void NewStory_SingleVote()
        {
            var story = new StoryData
            {
                StoryId = 42,
                CreatedAt = new DateTime(2013, 1, 1),
                Category = 1,
                DetectedAt = TestableDateTime.UtcNow,
                Host = "ngeor.net",
                LastCheckedAt = TestableDateTime.UtcNow,
                LastModifiedAt = TestableDateTime.UtcNow,
                Title = "My blog",
                Url = "http://ngeor.net/blog/",
                Username = "ngeor",
                VoteCount = 1,
                TotalChecks = 1,
                TotalUpdates = 1
            };

            var storyVote = new StoryVoteData
            {
                CreatedAt = TestableDateTime.UtcNow,
                Username = "ngeor",
                Story = story
            };

            _mockStoryDataLayer.Setup(p => p.Read(42)).Returns((StoryData) null);
            _mockStoryDataLayer.Setup(p => p.Create(story)).Returns(story);
            _mockStoryVoteDataLayer.Setup(p => p.Create(storyVote));

            var persister = new BasicPersister();
            persister.DbSession = _dbSession;
            var result = persister.Save(new Story
            {
                StoryId = 42,
                Category = 1,
                Url = "http://ngeor.net/blog/",
                Title = "My blog",
                CreatedAt = new DateTime(2013, 1, 1),
                Username = "ngeor",
                Voters = new[] {"ngeor"}
            });

            Assert.AreEqual(UpdateResult.Created, result.Changes);

            _mockStoryDataLayer.VerifyAll();
            _mockStoryVoteDataLayer.VerifyAll();
        }

        [Test]
        public void NewStory_TwoVotes()
        {
            var story = new StoryData
            {
                StoryId = 42,
                CreatedAt = new DateTime(2013, 1, 1),
                Category = 1,
                DetectedAt = TestableDateTime.UtcNow,
                Host = "ngeor.net",
                LastCheckedAt = TestableDateTime.UtcNow,
                LastModifiedAt = TestableDateTime.UtcNow,
                Title = "My blog",
                Url = "http://ngeor.net/blog/",
                Username = "ngeor",
                VoteCount = 2,
                TotalChecks = 1,
                TotalUpdates = 1
            };

            var storyVote1 = new StoryVoteData
            {
                CreatedAt = TestableDateTime.UtcNow,
                Username = "ngeor",
                Story = story
            };

            var storyVote2 = new StoryVoteData
            {
                CreatedAt = TestableDateTime.UtcNow,
                Username = "nikolaos",
                Story = story
            };

            _mockStoryDataLayer.Setup(p => p.Read(42)).Returns((StoryData) null);
            _mockStoryDataLayer.Setup(p => p.Create(story)).Returns(story);
            _mockStoryVoteDataLayer.Setup(p => p.Create(storyVote1));
            _mockStoryVoteDataLayer.Setup(p => p.Create(storyVote2));

            var persister = new BasicPersister();
            persister.DbSession = _dbSession;
            var result = persister.Save(new Story
            {
                StoryId = 42,
                Category = 1,
                Url = "http://ngeor.net/blog/",
                Title = "My blog",
                CreatedAt = new DateTime(2013, 1, 1),
                Username = "ngeor",
                Voters = new[] {"ngeor", "nikolaos"}
            });

            _mockStoryDataLayer.VerifyAll();
            _mockStoryVoteDataLayer.VerifyAll();

            Assert.AreEqual(UpdateResult.Created | UpdateResult.NewVotes, result.Changes);
        }

        [Test]
        public void NewStory_SingleComment()
        {
            var story = new StoryData
            {
                StoryId = 42,
                CreatedAt = new DateTime(2013, 1, 1),
                Category = 1,
                DetectedAt = TestableDateTime.UtcNow,
                Host = "ngeor.net",
                LastCheckedAt = TestableDateTime.UtcNow,
                LastCommentedAt = new DateTime(2013, 2, 1),
                LastModifiedAt = TestableDateTime.UtcNow,
                Title = "My blog",
                Url = "http://ngeor.net/blog/",
                Username = "ngeor",
                VoteCount = 1,
                TotalChecks = 1,
                TotalUpdates = 1
            };

            var storyVote = new StoryVoteData
            {
                CreatedAt = TestableDateTime.UtcNow,
                Username = "ngeor",
                Story = story
            };

            var comment = new CommentData
            {
                CommentId = 100,
                CreatedAt = new DateTime(2013, 2, 1),
                DetectedAt = TestableDateTime.UtcNow,
                IsBuried = false,
                ParentComment = null,
                Story = story,
                Username = "ngeor",
                VotesDown = 0,
                VotesUp = 0
            };

            var commentVote = new CommentVoteData
            {
                Comment = comment,
                CreatedAt = TestableDateTime.UtcNow,
                IsBuried = false,
                VotesDown = 0,
                VotesUp = 0
            };

            _mockStoryDataLayer.Setup(p => p.Read(42)).Returns((StoryData) null);
            _mockStoryDataLayer.Setup(p => p.Create(story)).Returns(story);
            _mockStoryVoteDataLayer.Setup(p => p.Create(storyVote));
            _mockCommentDataLayer.Setup(p => p.Create(comment)).Returns(comment);
            _mockCommentVoteDataLayer.Setup(p => p.Create(commentVote));

            var persister = new BasicPersister();
            persister.DbSession = _dbSession;
            var result = persister.Save(new Story
            {
                StoryId = 42,
                Category = 1,
                Url = "http://ngeor.net/blog/",
                Title = "My blog",
                CreatedAt = new DateTime(2013, 1, 1),
                Username = "ngeor",
                Voters = new[] {"ngeor"},
                Comments = new[]
                {
                    new Comment
                    {
                        CommentId = 100,
                        Username = "ngeor",
                        VotesUp = 0,
                        VotesDown = 0,
                        IsBuried = false,
                        CreatedAt = new DateTime(2013, 2, 1)
                    }
                }
            });

            _mockStoryDataLayer.VerifyAll();
            _mockCommentDataLayer.VerifyAll();
            _mockStoryVoteDataLayer.VerifyAll();
            _mockCommentVoteDataLayer.VerifyAll();

            Assert.AreEqual(UpdateResult.Created | UpdateResult.NewComments, result.Changes);
        }

        [Test]
        public void NewStory_NestedComment()
        {
            var story = new StoryData
            {
                StoryId = 42,
                CreatedAt = new DateTime(2013, 1, 1),
                Category = 1,
                DetectedAt = TestableDateTime.UtcNow,
                Host = "ngeor.net",
                LastCheckedAt = TestableDateTime.UtcNow,
                LastCommentedAt = new DateTime(2013, 3, 1),
                LastModifiedAt = TestableDateTime.UtcNow,
                Title = "My blog",
                Url = "http://ngeor.net/blog/",
                Username = "ngeor",
                VoteCount = 1,
                TotalChecks = 1,
                TotalUpdates = 1
            };

            var storyVote = new StoryVoteData
            {
                CreatedAt = TestableDateTime.UtcNow,
                Username = "ngeor",
                Story = story
            };

            var comment = new CommentData
            {
                CommentId = 100,
                CreatedAt = new DateTime(2013, 2, 1),
                DetectedAt = TestableDateTime.UtcNow,
                IsBuried = false,
                ParentComment = null,
                Story = story,
                Username = "ngeor",
                VotesDown = 0,
                VotesUp = 0
            };

            var commentVote = new CommentVoteData
            {
                Comment = comment,
                CreatedAt = TestableDateTime.UtcNow,
                IsBuried = false,
                VotesDown = 0,
                VotesUp = 0
            };

            var childComment = new CommentData
            {
                CommentId = 200,
                CreatedAt = new DateTime(2013, 3, 1),
                DetectedAt = TestableDateTime.UtcNow,
                IsBuried = false,
                ParentComment = comment,
                Story = story,
                Username = "nikolaos",
                VotesDown = 1,
                VotesUp = 0
            };

            var childCommentVote = new CommentVoteData
            {
                Comment = childComment,
                CreatedAt = TestableDateTime.UtcNow,
                IsBuried = false,
                VotesDown = 1,
                VotesUp = 0
            };

            _mockStoryDataLayer.Setup(p => p.Read(42)).Returns((StoryData) null);
            _mockStoryDataLayer.Setup(p => p.Create(story)).Returns(story);
            _mockStoryVoteDataLayer.Setup(p => p.Create(storyVote));
            _mockCommentDataLayer.Setup(p => p.Create(comment)).Returns(comment);
            _mockCommentVoteDataLayer.Setup(p => p.Create(commentVote));
            _mockCommentDataLayer.Setup(p => p.Create(childComment)).Returns(childComment);
            _mockCommentVoteDataLayer.Setup(p => p.Create(childCommentVote));

            var persister = new BasicPersister();
            persister.DbSession = _dbSession;
            var result = persister.Save(new Story
            {
                StoryId = 42,
                Category = 1,
                Url = "http://ngeor.net/blog/",
                Title = "My blog",
                CreatedAt = new DateTime(2013, 1, 1),
                Username = "ngeor",
                Voters = new[] {"ngeor"},
                Comments = new[]
                {
                    new Comment
                    {
                        CommentId = 100,
                        Username = "ngeor",
                        VotesUp = 0,
                        VotesDown = 0,
                        IsBuried = false,
                        CreatedAt = new DateTime(2013, 2, 1),
                        Comments = new[]
                        {
                            new Comment
                            {
                                CommentId = 200,
                                Username = "nikolaos",
                                VotesUp = 0,
                                VotesDown = 1,
                                IsBuried = false,
                                CreatedAt = new DateTime(2013, 3, 1)
                            }
                        }
                    }
                }
            });

            _mockStoryDataLayer.VerifyAll();
            _mockCommentDataLayer.VerifyAll();
            _mockStoryVoteDataLayer.VerifyAll();
            _mockCommentVoteDataLayer.VerifyAll();

            Assert.AreEqual(UpdateResult.Created | UpdateResult.NewComments, result.Changes);
        }

        [Test]
        public void NewStory_Removed()
        {
            _mockStoryDataLayer.Setup(p => p.Read(42)).Returns((StoryData) null);

            var persister = new BasicPersister();
            persister.DbSession = _dbSession;
            var result = persister.Save(new Story
            {
                StoryId = 42,
                IsRemoved = true
            });

            _mockStoryDataLayer.VerifyAll();
            Assert.AreEqual(UpdateResult.NoChanges, result.Changes);
        }

        [Test]
        public void ExistingStory_SingleVote_NoChanges()
        {
            var story = new StoryData
            {
                StoryId = 42,
                CreatedAt = new DateTime(2013, 1, 1),
                Category = 1,
                DetectedAt = new DateTime(2014, 1, 1),
                Host = "ngeor.net",
                LastCheckedAt = new DateTime(2014, 2, 1),
                LastModifiedAt = new DateTime(2014, 2, 1),
                Title = "My blog",
                Url = "http://ngeor.net/blog/",
                Username = "ngeor",
                VoteCount = 1,
                TotalChecks = 1,
                TotalUpdates = 1
            };

            var updatedStory = new StoryData
            {
                StoryId = 42,
                CreatedAt = new DateTime(2013, 1, 1),
                Category = 1,
                DetectedAt = new DateTime(2014, 1, 1),
                Host = "ngeor.net",
                LastCheckedAt = TestableDateTime.UtcNow,
                LastModifiedAt = new DateTime(2014, 2, 1),
                Title = "My blog",
                Url = "http://ngeor.net/blog/",
                Username = "ngeor",
                VoteCount = 1,
                TotalChecks = 2,
                TotalUpdates = 1
            };

            var storyVote = new StoryVoteData
            {
                CreatedAt = TestableDateTime.UtcNow,
                Username = "ngeor",
                Story = story
            };

            _mockStoryDataLayer.Setup(p => p.Read(42)).Returns(story);
            _mockStoryVoteDataLayer.Setup(p => p.Query(story)).Returns(new[] {storyVote});
            _mockStoryDataLayer.Setup(p => p.Update(updatedStory));

            var persister = new BasicPersister();
            persister.DbSession = _dbSession;
            var result = persister.Save(new Story
            {
                StoryId = 42,
                Category = 1,
                Url = "http://ngeor.net/blog/",
                Title = "My blog",
                CreatedAt = new DateTime(2013, 1, 1),
                Username = "ngeor",
                Voters = new[] {"ngeor"}
            });

            _mockStoryDataLayer.VerifyAll();
            _mockStoryVoteDataLayer.VerifyAll();

            Assert.AreEqual(UpdateResult.NoChanges, result.Changes);
        }

        [Test]
        public void ExistingStory_SingleVote_NewVote()
        {
            var story = new StoryData
            {
                StoryId = 42,
                CreatedAt = new DateTime(2013, 1, 1),
                Category = 1,
                DetectedAt = new DateTime(2014, 1, 1),
                Host = "ngeor.net",
                LastCheckedAt = new DateTime(2014, 2, 1),
                LastModifiedAt = new DateTime(2014, 2, 1),
                Title = "My blog",
                Url = "http://ngeor.net/blog/",
                Username = "ngeor",
                VoteCount = 1,
                TotalChecks = 1,
                TotalUpdates = 1
            };

            var updatedStory = new StoryData
            {
                StoryId = 42,
                CreatedAt = new DateTime(2013, 1, 1),
                Category = 1,
                DetectedAt = new DateTime(2014, 1, 1),
                Host = "ngeor.net",
                LastCheckedAt = TestableDateTime.UtcNow,
                LastModifiedAt = TestableDateTime.UtcNow,
                Title = "My blog",
                Url = "http://ngeor.net/blog/",
                Username = "ngeor",
                VoteCount = 2,
                TotalChecks = 2,
                TotalUpdates = 2
            };

            var storyVote = new StoryVoteData
            {
                CreatedAt = TestableDateTime.UtcNow,
                Username = "ngeor",
                Story = story
            };

            var newStoryVote = new StoryVoteData
            {
                CreatedAt = TestableDateTime.UtcNow,
                Username = "nikolaos",
                Story = story
            };

            _mockStoryDataLayer.Setup(p => p.Read(42)).Returns(story);
            _mockStoryVoteDataLayer.Setup(p => p.Query(story)).Returns(new[] {storyVote});
            _mockStoryVoteDataLayer.Setup(p => p.Create(newStoryVote));
            _mockStoryDataLayer.Setup(p => p.Update(updatedStory));

            var persister = new BasicPersister();
            persister.DbSession = _dbSession;
            var result = persister.Save(new Story
            {
                StoryId = 42,
                Category = 1,
                Url = "http://ngeor.net/blog/",
                Title = "My blog",
                CreatedAt = new DateTime(2013, 1, 1),
                Username = "ngeor",
                Voters = new[] {"ngeor", "nikolaos"}
            });

            _mockStoryDataLayer.VerifyAll();
            _mockStoryVoteDataLayer.VerifyAll();

            Assert.AreEqual(UpdateResult.NewVotes, result.Changes);
        }

        [Test]
        public void ExistingStory_TwoVotes_OneVoteLess()
        {
            var story = new StoryData
            {
                StoryId = 42,
                CreatedAt = new DateTime(2013, 1, 1),
                Category = 1,
                DetectedAt = new DateTime(2014, 1, 1),
                Host = "ngeor.net",
                LastCheckedAt = new DateTime(2014, 2, 1),
                LastModifiedAt = new DateTime(2014, 2, 1),
                Title = "My blog",
                Url = "http://ngeor.net/blog/",
                Username = "ngeor",
                VoteCount = 2,
                TotalChecks = 1,
                TotalUpdates = 1
            };

            var updatedStory = new StoryData
            {
                StoryId = 42,
                CreatedAt = new DateTime(2013, 1, 1),
                Category = 1,
                DetectedAt = new DateTime(2014, 1, 1),
                Host = "ngeor.net",
                LastCheckedAt = TestableDateTime.UtcNow,
                LastModifiedAt = TestableDateTime.UtcNow,
                Title = "My blog",
                Url = "http://ngeor.net/blog/",
                Username = "ngeor",
                VoteCount = 1,
                TotalChecks = 2,
                TotalUpdates = 2
            };

            var existingStoryVote1 = new StoryVoteData
            {
                CreatedAt = TestableDateTime.UtcNow,
                Username = "ngeor",
                Story = story
            };

            var existingStoryVote2 = new StoryVoteData
            {
                CreatedAt = TestableDateTime.UtcNow,
                Username = "nikolaos",
                Story = story
            };

            _mockStoryDataLayer.Setup(p => p.Read(42)).Returns(story);
            _mockStoryVoteDataLayer.Setup(p => p.Query(story)).Returns(new[] {existingStoryVote1, existingStoryVote2});
            _mockStoryVoteDataLayer.Setup(p => p.Delete(story, "nikolaos"));
            _mockStoryDataLayer.Setup(p => p.Update(updatedStory));

            var persister = new BasicPersister();
            persister.DbSession = _dbSession;
            var result = persister.Save(new Story
            {
                StoryId = 42,
                Category = 1,
                Url = "http://ngeor.net/blog/",
                Title = "My blog",
                CreatedAt = new DateTime(2013, 1, 1),
                Username = "ngeor",
                Voters = new[] {"ngeor"}
            });

            _mockStoryDataLayer.VerifyAll();
            _mockStoryVoteDataLayer.VerifyAll();

            Assert.AreEqual(UpdateResult.LessVotes, result.Changes);
        }

        [Test]
        public void ExistingStory_SingleComment_NoChanges()
        {
            var story = new StoryData
            {
                StoryId = 42,
                CreatedAt = new DateTime(2013, 1, 1),
                Category = 1,
                DetectedAt = new DateTime(2014, 1, 1),
                Host = "ngeor.net",
                LastCheckedAt = new DateTime(2014, 2, 1),
                LastCommentedAt = new DateTime(2013, 2, 1),
                LastModifiedAt = new DateTime(2014, 2, 1),
                Title = "My blog",
                Url = "http://ngeor.net/blog/",
                Username = "ngeor",
                VoteCount = 1,
                TotalChecks = 1,
                TotalUpdates = 1
            };

            var updatedStory = new StoryData
            {
                StoryId = 42,
                CreatedAt = new DateTime(2013, 1, 1),
                Category = 1,
                DetectedAt = new DateTime(2014, 1, 1),
                Host = "ngeor.net",
                LastCheckedAt = TestableDateTime.UtcNow,
                LastCommentedAt = new DateTime(2013, 2, 1),
                LastModifiedAt = new DateTime(2014, 2, 1),
                Title = "My blog",
                Url = "http://ngeor.net/blog/",
                Username = "ngeor",
                VoteCount = 1,
                TotalChecks = 2,
                TotalUpdates = 1
            };

            var storyVote = new StoryVoteData
            {
                CreatedAt = new DateTime(2014, 1, 9),
                Username = "ngeor",
                Story = story
            };

            var comment = new CommentData
            {
                CommentId = 100,
                CreatedAt = new DateTime(2013, 2, 1),
                DetectedAt = new DateTime(2014, 1, 10),
                IsBuried = false,
                ParentComment = null,
                Story = story,
                Username = "ngeor",
                VotesDown = 0,
                VotesUp = 0
            };

            _mockStoryDataLayer.Setup(p => p.Read(42)).Returns(story);
            _mockStoryVoteDataLayer.Setup(p => p.Query(story)).Returns(new[] {storyVote});
            _mockCommentDataLayer.Setup(p => p.Read(100)).Returns(comment);
            _mockStoryDataLayer.Setup(p => p.Update(updatedStory));

            var persister = new BasicPersister();
            persister.DbSession = _dbSession;
            var result = persister.Save(new Story
            {
                StoryId = 42,
                Category = 1,
                Url = "http://ngeor.net/blog/",
                Title = "My blog",
                CreatedAt = new DateTime(2013, 1, 1),
                Username = "ngeor",
                Voters = new[] {"ngeor"},
                Comments = new[]
                {
                    new Comment
                    {
                        CommentId = 100,
                        Username = "ngeor",
                        VotesUp = 0,
                        VotesDown = 0,
                        IsBuried = false,
                        CreatedAt = new DateTime(2013, 2, 1)
                    }
                }
            });

            _mockStoryDataLayer.VerifyAll();
            _mockCommentDataLayer.VerifyAll();
            _mockStoryVoteDataLayer.VerifyAll();

            Assert.AreEqual(UpdateResult.NoChanges, result.Changes);
        }

        [Test]
        public void ExistingStory_SingleComment_NewCommentVote()
        {
            var story = new StoryData
            {
                StoryId = 42,
                CreatedAt = new DateTime(2013, 1, 1),
                Category = 1,
                DetectedAt = new DateTime(2014, 1, 1),
                Host = "ngeor.net",
                LastCheckedAt = new DateTime(2014, 2, 1),
                LastCommentedAt = new DateTime(2013, 2, 1),
                LastModifiedAt = new DateTime(2014, 2, 1),
                Title = "My blog",
                Url = "http://ngeor.net/blog/",
                Username = "ngeor",
                VoteCount = 1,
                TotalChecks = 1,
                TotalUpdates = 1
            };

            var updatedStory = new StoryData
            {
                StoryId = 42,
                CreatedAt = new DateTime(2013, 1, 1),
                Category = 1,
                DetectedAt = new DateTime(2014, 1, 1),
                Host = "ngeor.net",
                LastCheckedAt = TestableDateTime.UtcNow,
                LastCommentedAt = new DateTime(2013, 2, 1),
                LastModifiedAt = TestableDateTime.UtcNow,
                Title = "My blog",
                Url = "http://ngeor.net/blog/",
                Username = "ngeor",
                VoteCount = 1,
                TotalChecks = 2,
                TotalUpdates = 2
            };

            var storyVote = new StoryVoteData
            {
                CreatedAt = new DateTime(2014, 1, 9),
                Username = "ngeor",
                Story = story
            };

            var comment = new CommentData
            {
                CommentId = 100,
                CreatedAt = new DateTime(2013, 2, 1),
                DetectedAt = new DateTime(2014, 1, 10),
                IsBuried = false,
                ParentComment = null,
                Story = story,
                Username = "ngeor",
                VotesDown = 0,
                VotesUp = 0
            };

            var updatedComment = new CommentData
            {
                CommentId = 100,
                CreatedAt = new DateTime(2013, 2, 1),
                DetectedAt = new DateTime(2014, 1, 10),
                IsBuried = false,
                ParentComment = null,
                Story = story,
                Username = "ngeor",
                VotesDown = 0,
                VotesUp = 2
            };

            var commentVote = new CommentVoteData
            {
                Comment = comment,
                CreatedAt = TestableDateTime.UtcNow,
                IsBuried = false,
                VotesDown = 0,
                VotesUp = 2
            };

            _mockStoryDataLayer.Setup(p => p.Read(42)).Returns(story);
            _mockStoryVoteDataLayer.Setup(p => p.Query(story)).Returns(new[] {storyVote});
            _mockCommentDataLayer.Setup(p => p.Read(100)).Returns(comment);
            _mockStoryDataLayer.Setup(p => p.Update(updatedStory));
            _mockCommentDataLayer.Setup(p => p.Update(updatedComment));
            _mockCommentVoteDataLayer.Setup(p => p.Create(commentVote));

            var persister = new BasicPersister();
            persister.DbSession = _dbSession;
            var result = persister.Save(new Story
            {
                StoryId = 42,
                Category = 1,
                Url = "http://ngeor.net/blog/",
                Title = "My blog",
                CreatedAt = new DateTime(2013, 1, 1),
                Username = "ngeor",
                Voters = new[] {"ngeor"},
                Comments = new[]
                {
                    new Comment
                    {
                        CommentId = 100,
                        Username = "ngeor",
                        VotesUp = 2,
                        VotesDown = 0,
                        IsBuried = false,
                        CreatedAt = new DateTime(2013, 2, 1)
                    }
                }
            });

            _mockStoryDataLayer.VerifyAll();
            _mockCommentDataLayer.VerifyAll();
            _mockStoryVoteDataLayer.VerifyAll();
            _mockCommentVoteDataLayer.VerifyAll();

            Assert.AreEqual(UpdateResult.NewCommentVotes, result.Changes);
        }

        [Test]
        public void ExistingStory_SingleComment_NewSiblingComment()
        {
            var story = new StoryData
            {
                StoryId = 42,
                CreatedAt = new DateTime(2013, 1, 1),
                Category = 1,
                DetectedAt = new DateTime(2014, 1, 1),
                Host = "ngeor.net",
                LastCheckedAt = new DateTime(2014, 2, 1),
                LastCommentedAt = new DateTime(2013, 2, 1),
                LastModifiedAt = new DateTime(2014, 2, 1),
                Title = "My blog",
                Url = "http://ngeor.net/blog/",
                Username = "ngeor",
                VoteCount = 1,
                TotalChecks = 1,
                TotalUpdates = 1
            };

            var updatedStory = new StoryData
            {
                StoryId = 42,
                CreatedAt = new DateTime(2013, 1, 1),
                Category = 1,
                DetectedAt = new DateTime(2014, 1, 1),
                Host = "ngeor.net",
                LastCheckedAt = TestableDateTime.UtcNow,
                LastCommentedAt = new DateTime(2013, 3, 1),
                LastModifiedAt = TestableDateTime.UtcNow,
                Title = "My blog",
                Url = "http://ngeor.net/blog/",
                Username = "ngeor",
                VoteCount = 1,
                TotalChecks = 2,
                TotalUpdates = 2
            };

            var storyVote = new StoryVoteData
            {
                CreatedAt = new DateTime(2014, 1, 9),
                Username = "ngeor",
                Story = story
            };

            var comment = new CommentData
            {
                CommentId = 100,
                CreatedAt = new DateTime(2013, 2, 1),
                DetectedAt = new DateTime(2014, 1, 10),
                IsBuried = false,
                ParentComment = null,
                Story = story,
                Username = "ngeor",
                VotesDown = 0,
                VotesUp = 0
            };

            var newComment = new CommentData
            {
                CommentId = 200,
                CreatedAt = new DateTime(2013, 3, 1),
                DetectedAt = TestableDateTime.UtcNow,
                IsBuried = false,
                ParentComment = null,
                Story = story,
                Username = "nikolaos",
                VotesDown = 1,
                VotesUp = 3
            };

            var commentVote = new CommentVoteData
            {
                Comment = newComment,
                CreatedAt = TestableDateTime.UtcNow,
                IsBuried = false,
                VotesDown = 1,
                VotesUp = 3
            };

            _mockStoryDataLayer.Setup(p => p.Read(42)).Returns(story);
            _mockStoryVoteDataLayer.Setup(p => p.Query(story)).Returns(new[] {storyVote});
            _mockCommentDataLayer.Setup(p => p.Read(100)).Returns(comment);
            _mockCommentDataLayer.Setup(p => p.Read(200)).Returns((CommentData) null);
            _mockCommentDataLayer.Setup(p => p.Create(newComment)).Returns(newComment);
            _mockCommentVoteDataLayer.Setup(p => p.Create(commentVote));
            _mockStoryDataLayer.Setup(p => p.Update(updatedStory));

            var persister = new BasicPersister();
            persister.DbSession = _dbSession;
            var result = persister.Save(new Story
            {
                StoryId = 42,
                Category = 1,
                Url = "http://ngeor.net/blog/",
                Title = "My blog",
                CreatedAt = new DateTime(2013, 1, 1),
                Username = "ngeor",
                Voters = new[] {"ngeor"},
                Comments = new[]
                {
                    new Comment
                    {
                        CommentId = 100,
                        Username = "ngeor",
                        VotesUp = 0,
                        VotesDown = 0,
                        IsBuried = false,
                        CreatedAt = new DateTime(2013, 2, 1)
                    },
                    new Comment
                    {
                        CommentId = 200,
                        Username = "nikolaos",
                        VotesUp = 3,
                        VotesDown = 1,
                        IsBuried = false,
                        CreatedAt = new DateTime(2013, 3, 1)
                    }
                }
            });

            _mockStoryDataLayer.VerifyAll();
            _mockCommentDataLayer.VerifyAll();
            _mockStoryVoteDataLayer.VerifyAll();
            _mockCommentVoteDataLayer.VerifyAll();

            Assert.AreEqual(UpdateResult.NewComments, result.Changes);
        }

        [Test]
        public void ExistingStory_SingleComment_NewChildComment()
        {
            var story = new StoryData
            {
                StoryId = 42,
                CreatedAt = new DateTime(2013, 1, 1),
                Category = 1,
                DetectedAt = new DateTime(2014, 1, 1),
                Host = "ngeor.net",
                LastCheckedAt = new DateTime(2014, 2, 1),
                LastCommentedAt = new DateTime(2013, 2, 1),
                LastModifiedAt = new DateTime(2014, 2, 1),
                Title = "My blog",
                Url = "http://ngeor.net/blog/",
                Username = "ngeor",
                VoteCount = 1,
                TotalChecks = 1,
                TotalUpdates = 1
            };

            var updatedStory = new StoryData
            {
                StoryId = 42,
                CreatedAt = new DateTime(2013, 1, 1),
                Category = 1,
                DetectedAt = new DateTime(2014, 1, 1),
                Host = "ngeor.net",
                LastCheckedAt = TestableDateTime.UtcNow,
                LastCommentedAt = new DateTime(2013, 3, 1),
                LastModifiedAt = TestableDateTime.UtcNow,
                Title = "My blog",
                Url = "http://ngeor.net/blog/",
                Username = "ngeor",
                VoteCount = 1,
                TotalChecks = 2,
                TotalUpdates = 2
            };

            var storyVote = new StoryVoteData
            {
                CreatedAt = new DateTime(2014, 1, 9),
                Username = "ngeor",
                Story = story
            };

            var comment = new CommentData
            {
                CommentId = 100,
                CreatedAt = new DateTime(2013, 2, 1),
                DetectedAt = new DateTime(2014, 1, 10),
                IsBuried = false,
                ParentComment = null,
                Story = story,
                Username = "ngeor",
                VotesDown = 0,
                VotesUp = 0
            };

            var newComment = new CommentData
            {
                CommentId = 200,
                CreatedAt = new DateTime(2013, 3, 1),
                DetectedAt = TestableDateTime.UtcNow,
                IsBuried = false,
                ParentComment = comment,
                Story = story,
                Username = "nikolaos",
                VotesDown = 1,
                VotesUp = 3
            };

            var commentVote = new CommentVoteData
            {
                Comment = newComment,
                CreatedAt = TestableDateTime.UtcNow,
                IsBuried = false,
                VotesDown = 1,
                VotesUp = 3
            };

            _mockStoryDataLayer.Setup(p => p.Read(42)).Returns(story);
            _mockStoryVoteDataLayer.Setup(p => p.Query(story)).Returns(new[] {storyVote});
            _mockCommentDataLayer.Setup(p => p.Read(100)).Returns(comment);
            _mockCommentDataLayer.Setup(p => p.Read(200)).Returns((CommentData) null);
            _mockCommentDataLayer.Setup(p => p.Create(newComment)).Returns(newComment);
            _mockCommentVoteDataLayer.Setup(p => p.Create(commentVote));
            _mockStoryDataLayer.Setup(p => p.Update(updatedStory));

            var persister = new BasicPersister();
            persister.DbSession = _dbSession;
            var result = persister.Save(new Story
            {
                StoryId = 42,
                Category = 1,
                Url = "http://ngeor.net/blog/",
                Title = "My blog",
                CreatedAt = new DateTime(2013, 1, 1),
                Username = "ngeor",
                Voters = new[] {"ngeor"},
                Comments = new[]
                {
                    new Comment
                    {
                        CommentId = 100,
                        Username = "ngeor",
                        VotesUp = 0,
                        VotesDown = 0,
                        IsBuried = false,
                        CreatedAt = new DateTime(2013, 2, 1),
                        Comments = new[]
                        {
                            new Comment
                            {
                                CommentId = 200,
                                Username = "nikolaos",
                                VotesUp = 3,
                                VotesDown = 1,
                                IsBuried = false,
                                CreatedAt = new DateTime(2013, 3, 1)
                            }
                        }
                    }
                }
            });

            _mockStoryDataLayer.VerifyAll();
            _mockCommentDataLayer.VerifyAll();
            _mockStoryVoteDataLayer.VerifyAll();
            _mockCommentVoteDataLayer.VerifyAll();

            Assert.AreEqual(UpdateResult.NewComments, result.Changes);
        }

        [Test]
        public void NewStory_MarkAsUnmodified()
        {
            StoryData nullStory = null;
            _mockStoryDataLayer.Setup(d => d.Read(42)).Returns(nullStory);

            var persister = new BasicPersister();
            persister.DbSession = _dbSession;
            Assert.AreEqual(
                new PersisterResult(null, UpdateResult.NoChanges),
                persister.MarkAsUnmodified(42));
        }

        [Test]
        public void ExistingStory_MarkAsUnmodified()
        {
            StoryData existingStory = new StoryData
            {
                TotalChecks = 1,
                LastCheckedAt = new DateTime(2014, 2, 13)
            };

            _mockStoryDataLayer.Setup(d => d.Read(42)).Returns(existingStory);
            _mockStoryDataLayer.Setup(d => d.Update(existingStory));

            var persister = new BasicPersister();
            persister.DbSession = _dbSession;
            Assert.AreEqual(
                new PersisterResult(existingStory, UpdateResult.NoChanges),
                persister.MarkAsUnmodified(42));

            Assert.AreEqual(2, existingStory.TotalChecks);
        }

        [Test]
        public void ExistingStory_Removed()
        {
            var story = new StoryData
            {
                StoryId = 42,
                CreatedAt = new DateTime(2013, 1, 1),
                Category = 1,
                DetectedAt = new DateTime(2014, 1, 1),
                Host = "ngeor.net",
                LastCheckedAt = new DateTime(2014, 2, 1),
                LastCommentedAt = new DateTime(2013, 2, 1),
                LastModifiedAt = new DateTime(2014, 2, 1),
                Title = "My blog",
                Url = "http://ngeor.net/blog/",
                Username = "ngeor",
                VoteCount = 1,
                TotalChecks = 1,
                TotalUpdates = 1
            };

            var updatedStory = new StoryData
            {
                StoryId = 42,
                CreatedAt = new DateTime(2013, 1, 1),
                Category = 1,
                DetectedAt = new DateTime(2014, 1, 1),
                Host = "ngeor.net",
                LastCheckedAt = TestableDateTime.UtcNow,
                LastCommentedAt = new DateTime(2013, 2, 1),
                LastModifiedAt = TestableDateTime.UtcNow,
                Title = "My blog",
                Url = "http://ngeor.net/blog/",
                Username = "ngeor",
                VoteCount = 1,
                TotalChecks = 2,
                TotalUpdates = 2,
                RemovedAt = TestableDateTime.UtcNow
            };

            _mockStoryDataLayer.Setup(p => p.Read(42)).Returns(story);
            _mockStoryDataLayer.Setup(p => p.Update(updatedStory));

            var persister = new BasicPersister();
            persister.DbSession = _dbSession;
            var result = persister.Save(new Story
            {
                StoryId = 42,
                IsRemoved = true
            });

            _mockStoryDataLayer.VerifyAll();
            Assert.AreEqual(UpdateResult.Removed, result.Changes);
        }
    }
}
