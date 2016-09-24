// --------------------------------------------------------------------------------
// <copyright file="StoryDownloadedMessage.cs" company="Nikolaos Georgiou">
//   Copyright (C) Nikolaos Georgiou 2010-2015
// </copyright>
// <author>Nikolaos Georgiou</author>
// * Date: 2015/10/16
// * Time: 06:16:13
// --------------------------------------------------------------------------------

using System;
using BuzzStats.Parsing;

namespace BuzzStats.Crawl
{
    public class StoryDownloadedMessage
    {
        public StoryDownloadedMessage(Story story, ILeafSource leafSource)
        {
            if (story == null)
            {
                throw new ArgumentNullException("story");
            }

            if (leafSource == null)
            {
                throw new ArgumentNullException("leafSource");
            }

            this.Story = story;
            LeafSource = leafSource;
        }

        public Story Story { get; private set; }

        public ILeafSource LeafSource { get; private set; }

        public override bool Equals(object obj)
        {
            if (obj == null)
                return false;
            if (ReferenceEquals(this, obj))
                return true;
            if (obj.GetType() != typeof (StoryDownloadedMessage))
                return false;
            StoryDownloadedMessage other = (StoryDownloadedMessage) obj;
            return Story.Equals(other.Story) && LeafSource.Equals(other.LeafSource);
        }

        public override int GetHashCode()
        {
            unchecked
            {
                return Story.GetHashCode() ^ LeafSource.GetHashCode();
            }
        }
    }
}
