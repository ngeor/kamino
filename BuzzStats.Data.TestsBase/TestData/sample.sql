DELETE FROM StoryPollHistory;
DELETE FROM CommentVote;
DELETE FROM StoryVote;
DELETE FROM Comment WHERE parentcomment_id<>0;
DELETE FROM Comment;
DELETE FROM Story;

-- first story (different TotalChecks)
INSERT INTO Story (Id, StoryId, Title, Url, Host, Category, VoteCount, Username, CreatedAt, DetectedAt,
	LastCheckedAt, LastModifiedAt, TotalUpdates, TotalChecks, LastCommentedAt, RemovedAt) VALUES
	(1, 42, 'test story 1', 'http://ngeor.net/test', 'ngeor.net', 1, 1, 'ngeor', '2015-06-01', '2015-06-01',
	'2015-06-01 01:00:00', '2015-06-01', 1, 2, null, null);
INSERT INTO StoryVote (Username, CreatedAt, Story_id) VALUES ('ngeor', '2015-06-01', 1);

-- second story (different dates)
INSERT INTO Story (Id, StoryId, Title, Url, Host, Category, VoteCount, Username, CreatedAt, DetectedAt,
	LastCheckedAt, LastModifiedAt, TotalUpdates, TotalChecks, LastCommentedAt, RemovedAt) VALUES
	(2, 43, 'test story 2', 'http://ngeor.net/test2', 'ngeor.net', 1, 1, 'ngeor', '2015-06-06', '2015-06-06',
	'2015-06-02', '2015-06-02', 1, 1, null, null);
INSERT INTO StoryVote (Username, CreatedAt, Story_id) VALUES ('ngeor', '2015-06-06', 2);

-- third story (different user)
INSERT INTO Story (Id, StoryId, Title, Url, Host, Category, VoteCount, Username, CreatedAt, DetectedAt,
	LastCheckedAt, LastModifiedAt, TotalUpdates, TotalChecks, LastCommentedAt, RemovedAt) VALUES
	(3, 44, 'test story 3', 'http://ngeor.net/test3', 'ngeor.org', 1, 1, 'nikolaos', '2015-06-06', '2015-06-06',
	'2015-06-03 09:00:00', '2015-06-03', 1, 1, null, null);
INSERT INTO StoryVote (Username, CreatedAt, Story_id) VALUES ('nikolaos', '2015-06-06', 3);

-- fourth story (removed story)
INSERT INTO Story (Id, StoryId, Title, Url, Host, Category, VoteCount, Username, CreatedAt, DetectedAt,
	LastCheckedAt, LastModifiedAt, TotalUpdates, TotalChecks, LastCommentedAt, RemovedAt) VALUES
	(4, 45, 'test story 4', 'http://ngeor.net/test4', 'ngeor.org', 1, 1, 'nikolaos', '2015-06-06', '2015-06-06',
	'2015-06-04 08:00:00', '2015-06-04', 1, 1, null, '2015-06-06');
