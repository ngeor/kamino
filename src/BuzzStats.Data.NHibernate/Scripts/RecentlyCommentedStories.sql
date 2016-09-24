USE [BuzzStatsStaging]
GO

/****** Object:  StoredProcedure [dbo].[RecentlyCommentedStories]    Script Date: 07/24/2011 20:38:51 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[RecentlyCommentedStories]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[RecentlyCommentedStories]
GO

USE [BuzzStatsStaging]
GO

/****** Object:  StoredProcedure [dbo].[RecentlyCommentedStories]    Script Date: 07/24/2011 20:38:51 ******/
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


