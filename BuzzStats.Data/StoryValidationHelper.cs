using System;

namespace BuzzStats.Data
{
    public static class StoryValidationHelper
    {
        public static void ValidatePreCreate(this StoryData story)
        {
            if (story == null)
            {
                throw new ArgumentNullException("story");
            }

            if (story.StoryId <= 0)
            {
                throw new InvalidStoryIdException();
            }

            if (string.IsNullOrWhiteSpace(story.Title))
            {
                throw new ArgumentException("Title is missing");
            }

            if (string.IsNullOrWhiteSpace(story.Username))
            {
                throw new ArgumentException("Username is missing");
            }
        }

        public static void ValidatePreUpdate(this StoryData story)
        {
            story.ValidatePreCreate();
        }
    }
}