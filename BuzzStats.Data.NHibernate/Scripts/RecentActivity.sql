USE [BuzzStatsLive]
GO

/****** Object:  StoredProcedure [dbo].[RecentActivity]    Script Date: 07/30/2011 13:31:42 ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO



CREATE PROCEDURE [dbo].[RecentActivity]
	@maxCount int
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

    WITH OrderedRecentActivity AS
(
	SELECT
		RecentActivity.*,
		ROW_NUMBER() OVER (ORDER BY [When] DESC) AS RowNumber from (

SELECT
	1 [What], s.CreatedAt [When],
	s.StoryId, s.Title StoryTitle, s.Username StorySubmitter,
	null StoryVoter,
	null CommentId, null CommentUsername,
	null VotesUp, null VotesDown, null IsBuried
FROM Story s

UNION

SELECT
	2 [What], sv.CreatedAt [When],
	s.StoryId, s.Title StoryTitle, s.Username StorySubmitter,
	sv.Username StoryVoter,
	null CommentId, null CommentUsername,
	null VotesUp, null VotesDown, null IsBuried
FROM StoryVote sv
JOIN Story s ON sv.Story_id = s.Id
WHERE sv.Username <> s.Username

UNION 

SELECT
	4 [What], c.CreatedAt [When],
	s.StoryId, s.Title StoryTitle, s.Username StorySubmitter,
	null StoryVoter,
	c.CommentId, c.Username CommentUsername,
	null VotesUp, null VotesDown, null IsBuried
FROM Comment c
JOIN Story s
ON c.Story_id = s.Id

union

SELECT
	8 [What], cv.CreatedAt [When],
	s.StoryId, s.Title StoryTitle, s.Username StorySubmitter,
	null StoryVoter,
	c.CommentId, c.Username CommentUsername,
	cv.VotesUp, cv.VotesDown, cv.IsBuried
FROM CommentVote cv
JOIN Comment c
ON cv.Comment_id = c.Id
JOIN Story s
ON c.Story_id = s.Id
WHERE cv.VotesUp > 0 OR cv.VotesDown > 0 OR cv.IsBuried > 0
) AS RecentActivity
)
SELECT * FROM OrderedRecentActivity
WHERE RowNumber <= @maxCount
ORDER BY RowNumber
END


GO


