USE [BuzzStatsLive]
GO
/****** Object:  User [buzzStats]    Script Date: 09/07/2013 22:04:46 ******/
CREATE USER [buzzStats] FOR LOGIN [buzzStats] WITH DEFAULT_SCHEMA=[dbo]
GO
/****** Object:  UserDefinedFunction [dbo].[parseURL]    Script Date: 09/07/2013 22:04:46 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE      FUNCTION [dbo].[parseURL]  (@strURL varchar(1000))
RETURNS varchar(1000)
AS
BEGIN
IF CHARINDEX('http://',@strURL) > 0 OR CHARINDEX('https://',@strURL) > 0
-- Ghetto-tastic
SELECT @strURL = REPLACE(@strURL,'https://','')
SELECT @strURL = REPLACE(@strURL,'http://','')
--SELECT @strURL = REPLACE(@strURL,'www','')
-- Remove everything after "/" if one exists
IF CHARINDEX('/',@strURL) > 0 (SELECT @strURL = LEFT(@strURL,CHARINDEX('/',@strURL)-1))

-- Optional: Remove subdomains but differentiate between www.google.com and www.google.com.au
/*IF (LEN(@strURL)-LEN(REPLACE(@strURL,'.','')))/LEN('.') < 3 -- if there are less than 3 periods
SELECT @strURL = PARSENAME(@strURL,2) + '.' + PARSENAME(@strURL,1)
ELSE -- It's likely a google.co.uk, or google.com.au
SELECT @strURL = PARSENAME(@strURL,3) + '.' + PARSENAME(@strURL,2) + '.' + PARSENAME(@strURL,1)*/
RETURN @strURL
END
GO
/****** Object:  Table [dbo].[Story]    Script Date: 09/07/2013 22:04:35 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[Story](
	[Id] [int] IDENTITY(1,1) NOT NULL,
	[StoryId] [int] NOT NULL,
	[Title] [nvarchar](300) NOT NULL,
	[Url] [nvarchar](400) NOT NULL,
	[Username] [nvarchar](255) NOT NULL,
	[CreatedAt] [datetime] NOT NULL,
	[LastCheckedAt] [datetime] NOT NULL,
	[Category] [int] NOT NULL,
	[DetectedAt] [datetime] NOT NULL,
	[TotalUpdates] [int] NOT NULL,
	[TotalChecks] [int] NOT NULL,
	[LastModifiedAt] [datetime] NOT NULL,
	[LastCommentedAt] [datetime] NULL,
	[Host] [nvarchar](100) NULL,
	[ModificationAge] [bigint] NOT NULL,
	[VoteCount] [int] NOT NULL,
	[RemovedAt] [datetime] NULL,
 CONSTRAINT [PK__Story__3214EC077F60ED59] PRIMARY KEY CLUSTERED 
(
	[Id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = ON, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON, FILLFACTOR = 100) ON [PRIMARY],
 CONSTRAINT [UQ__Story__3E82C049023D5A04] UNIQUE NONCLUSTERED 
(
	[StoryId] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = ON, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON, FILLFACTOR = 100) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  View [dbo].[vLastModifiedAtPerMonth]    Script Date: 09/07/2013 22:04:37 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE VIEW [dbo].[vLastModifiedAtPerMonth]
AS
SELECT L AS LastModifiedAt, COUNT(*) AS StoryCount FROM (
	SELECT Id,
		CONVERT(date, CONVERT(varchar, year(LastModifiedAt)) + '-' + CONVERT(varchar, month(LastModifiedAt)) + '-01') as L
	FROM
		Story
	WHERE
		RemovedAt IS NULL
) s
	GROUP BY L
GO
/****** Object:  View [dbo].[vLastCheckedAtPerMonth]    Script Date: 09/07/2013 22:04:37 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE VIEW [dbo].[vLastCheckedAtPerMonth]
AS
SELECT L AS LastCheckedAt, COUNT(*) AS StoryCount FROM (
	SELECT Id,
		CONVERT(date, CONVERT(varchar, year(LastCheckedAt)) + '-' + CONVERT(varchar, month(LastCheckedAt)) + '-01') as L
	FROM
		Story
	WHERE
		RemovedAt IS NULL
) s
	GROUP BY L
GO
/****** Object:  View [dbo].[vCreatedAtPerMonth]    Script Date: 09/07/2013 22:04:37 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE VIEW [dbo].[vCreatedAtPerMonth]
AS
SELECT L AS CreatedAt, COUNT(*) AS StoryCount FROM (
	SELECT Id,
		CONVERT(date, CONVERT(varchar, year(CreatedAt)) + '-' + CONVERT(varchar, month(CreatedAt)) + '-01') as L
	FROM
		Story
	WHERE
		RemovedAt IS NULL
) s
	GROUP BY L
GO
/****** Object:  Table [dbo].[StoryVote]    Script Date: 09/07/2013 22:04:35 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[StoryVote](
	[Id] [int] IDENTITY(1,1) NOT NULL,
	[Username] [nvarchar](255) NOT NULL,
	[CreatedAt] [datetime] NOT NULL,
	[Story_id] [int] NOT NULL,
 CONSTRAINT [PK__StoryVot__3214EC07060DEAE8] PRIMARY KEY CLUSTERED 
(
	[Id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY],
 CONSTRAINT [UQ__StoryVot__4F2825D808EA5793] UNIQUE NONCLUSTERED 
(
	[Username] ASC,
	[Story_id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[Comment]    Script Date: 09/07/2013 22:04:35 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[Comment](
	[Id] [int] IDENTITY(1,1) NOT NULL,
	[CommentId] [int] NOT NULL,
	[Username] [nvarchar](255) NOT NULL,
	[CreatedAt] [datetime] NULL,
	[VotesUp] [int] NOT NULL,
	[VotesDown] [int] NOT NULL,
	[IsBuried] [bit] NOT NULL,
	[DetectedAt] [datetime] NOT NULL,
	[Story_id] [int] NOT NULL,
	[ParentComment_id] [int] NULL,
 CONSTRAINT [PK__Comment__3214EC070CBAE877] PRIMARY KEY CLUSTERED 
(
	[Id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY],
 CONSTRAINT [UQ__Comment__C3B4DFCB0F975522] UNIQUE NONCLUSTERED 
(
	[CommentId] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  View [dbo].[vStoryCountPerMonth]    Script Date: 09/07/2013 22:04:37 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE VIEW [dbo].[vStoryCountPerMonth] AS
SELECT
	d.CreatedAt,
	c.StoryCount as CreatedStoryCount,
	lc.StoryCount as LastCheckedAtStoryCount,
	lm.StoryCount as LastModifiedAtStoryCount
FROM (
	SELECT CreatedAt FROM vCreatedAtPerMonth
	UNION
	SELECT LastCheckedAt FROM vLastCheckedAtPerMonth
	UNION
	SELECT LastModifiedAt FROM vLastModifiedAtPerMonth) d
LEFT JOIN
	vCreatedAtPerMonth c
ON
	d.CreatedAt = c.CreatedAt
LEFT JOIN
	vLastCheckedAtPerMonth lc
ON
	d.CreatedAt = lc.LastCheckedAt
LEFT JOIN
	vLastModifiedAtPerMonth lm
ON
	d.CreatedAt = lm.LastModifiedAt
GO
/****** Object:  Table [dbo].[CommentVote]    Script Date: 09/07/2013 22:04:35 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[CommentVote](
	[Id] [int] IDENTITY(1,1) NOT NULL,
	[VotesUp] [int] NOT NULL,
	[VotesDown] [int] NOT NULL,
	[IsBuried] [bit] NOT NULL,
	[CreatedAt] [datetime] NOT NULL,
	[Comment_id] [int] NOT NULL,
 CONSTRAINT [PK__CommentV__3214EC071367E606] PRIMARY KEY CLUSTERED 
(
	[Id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY],
 CONSTRAINT [UQ__CommentV__4CC00307164452B1] UNIQUE NONCLUSTERED 
(
	[CreatedAt] ASC,
	[Comment_id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY],
 CONSTRAINT [UQ_CommentV_SingleValue] UNIQUE NONCLUSTERED 
(
	[Comment_id] ASC,
	[IsBuried] ASC,
	[VotesDown] ASC,
	[VotesUp] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  StoredProcedure [dbo].[RecentlyCommentedStories]    Script Date: 09/07/2013 22:04:45 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
-- Author:		<Author,,Name>
-- Create date: <Create Date,,>
-- Description:	<Description,,>
-- =============================================
CREATE PROCEDURE [dbo].[RecentlyCommentedStories] 
	@maxCount int = 10
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

    -- Insert statements for procedure here
WITH OrderedStories AS
(
SELECT s.*,
ROW_NUMBER() OVER (ORDER BY c1.CreatedAt DESC, c1.Id DESC) AS RowNumber
 FROM Story s
join
Comment c1
on
s.Id = c1.Story_id
WHERE c1.Id = (
	SELECT MAX(c2.Id)
	FROM Comment c2
	WHERE c2.Story_id = s.Id
)
)
SELECT * FROM OrderedStories
WHERE RowNumber <= @maxCount

END
GO
/****** Object:  StoredProcedure [dbo].[RecentActivity]    Script Date: 09/07/2013 22:04:45 ******/
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
/****** Object:  Default [DF__Story__TotalUpda__33D4B598]    Script Date: 09/07/2013 22:04:35 ******/
ALTER TABLE [dbo].[Story] ADD  CONSTRAINT [DF__Story__TotalUpda__33D4B598]  DEFAULT ((1)) FOR [TotalUpdates]
GO
/****** Object:  Default [DF__Story__TotalChec__34C8D9D1]    Script Date: 09/07/2013 22:04:35 ******/
ALTER TABLE [dbo].[Story] ADD  CONSTRAINT [DF__Story__TotalChec__34C8D9D1]  DEFAULT ((1)) FOR [TotalChecks]
GO
/****** Object:  Default [DF__Story__LastModif__35BCFE0A]    Script Date: 09/07/2013 22:04:35 ******/
ALTER TABLE [dbo].[Story] ADD  CONSTRAINT [DF__Story__LastModif__35BCFE0A]  DEFAULT (getdate()) FOR [LastModifiedAt]
GO
/****** Object:  Default [DF__Story__Modificat__38996AB5]    Script Date: 09/07/2013 22:04:35 ******/
ALTER TABLE [dbo].[Story] ADD  CONSTRAINT [DF__Story__Modificat__38996AB5]  DEFAULT ((0)) FOR [ModificationAge]
GO
/****** Object:  Default [DF__Story__VoteCount__398D8EEE]    Script Date: 09/07/2013 22:04:35 ******/
ALTER TABLE [dbo].[Story] ADD  CONSTRAINT [DF__Story__VoteCount__398D8EEE]  DEFAULT ((0)) FOR [VoteCount]
GO
/****** Object:  ForeignKey [FK_ParentComment]    Script Date: 09/07/2013 22:04:35 ******/
ALTER TABLE [dbo].[Comment]  WITH CHECK ADD  CONSTRAINT [FK_ParentComment] FOREIGN KEY([ParentComment_id])
REFERENCES [dbo].[Comment] ([Id])
GO
ALTER TABLE [dbo].[Comment] CHECK CONSTRAINT [FK_ParentComment]
GO
/****** Object:  ForeignKey [FKE2466703598589CF]    Script Date: 09/07/2013 22:04:35 ******/
ALTER TABLE [dbo].[Comment]  WITH CHECK ADD  CONSTRAINT [FKE2466703598589CF] FOREIGN KEY([Story_id])
REFERENCES [dbo].[Story] ([Id])
GO
ALTER TABLE [dbo].[Comment] CHECK CONSTRAINT [FKE2466703598589CF]
GO
/****** Object:  ForeignKey [FK534A43B79DCA6C3]    Script Date: 09/07/2013 22:04:35 ******/
ALTER TABLE [dbo].[CommentVote]  WITH CHECK ADD  CONSTRAINT [FK534A43B79DCA6C3] FOREIGN KEY([Comment_id])
REFERENCES [dbo].[Comment] ([Id])
GO
ALTER TABLE [dbo].[CommentVote] CHECK CONSTRAINT [FK534A43B79DCA6C3]
GO
/****** Object:  ForeignKey [FKEEB22A59598589CF]    Script Date: 09/07/2013 22:04:36 ******/
ALTER TABLE [dbo].[StoryVote]  WITH CHECK ADD  CONSTRAINT [FKEEB22A59598589CF] FOREIGN KEY([Story_id])
REFERENCES [dbo].[Story] ([Id])
GO
ALTER TABLE [dbo].[StoryVote] CHECK CONSTRAINT [FKEEB22A59598589CF]
GO
