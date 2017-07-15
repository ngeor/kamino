// --------------------------------------------------------------------------------
// <copyright file="Stories.aspx.cs" company="Nikolaos Georgiou">
//   Copyright (C) Nikolaos Georgiou 2010-2014
// </copyright>
// <author>Nikolaos Georgiou</author>
// * Date: 2014/08/09
// * Time: 19:26:41
// --------------------------------------------------------------------------------

using System;
using System.Collections.Generic;
using BuzzStats.Data;
using BuzzStats.Web.Mvp;
using BuzzStats.Web.UserControls;

namespace BuzzStats.Web
{
    [Presenter(typeof(StoriesPagePresenter))]
    public partial class Stories : MvpPage, IStoriesPageView
    {
        public void SetCommentCount(int commentCount)
        {
            litCommentCount.Text = commentCount.ToString("N0");
        }

        public void SetStories(StorySortField storySortField, ICollection<StorySummary> stories)
        {
            StoryList control;
            switch (storySortField)
            {
                case StorySortField.CreatedAt:
                    control = recentlyCreatedStories;
                    break;
                case StorySortField.LastModifiedAt:
                    control = recentlyModifiedStories;
                    break;
                case StorySortField.LastCommentedAt:
                    control = recentlyCommentedStories;
                    break;
                default:
                    throw new NotSupportedException(string.Format("Unsupported story list: " + storySortField));
            }

            control.DataSource = stories;
            control.DataBind();
        }

        public void SetStoryCount(int nonRemovedStoryCount, int removedStoryCount)
        {
            // TODO: implement this
        }
    }
}