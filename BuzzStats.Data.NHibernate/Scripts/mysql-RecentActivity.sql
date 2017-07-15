CREATE VIEW `RecentActivity` AS

select StoryId, NULL As CommentId, Title, Username, CreatedAt, DetectedAt, 1 as What FROM Story

UNION

(
select s.StoryId, NULL AS CommentId, s.Title, sv.Username, sv.CreatedAt, sv.CreatedAt AS DetectedAt, 2 AS What FROM
StoryVote sv
JOIN
Story s
ON sv.Story_id = s.Id
WHERE
sv.Username <> s.Username
)

UNION

(
SELECT s.StoryId, c.CommentId, s.Title, c.Username, c.CreatedAt, c.DetectedAt, 4 AS What FROM
`Comment` c
JOIN
Story s
ON
c.Story_id = s.Id
)

