// --------------------------------------------------------------------------------
// <copyright file="RecentActivityModel.cs" company="Nikolaos Georgiou">
//   Copyright (C) Nikolaos Georgiou 2010-2015
// </copyright>
// <author>Nikolaos Georgiou</author>
// * Date: 2015/11/24
// * Time: 14:25:05
// --------------------------------------------------------------------------------

using BuzzStats.Data;

namespace BuzzStats.Web.Mvp
{
    public class RecentActivityModel : RecentActivity
    {
        public RecentActivityModel()
        {
        }

        public RecentActivityModel(RecentActivity other, string url) : base(other)
        {
            StoryUrl = url;
        }

        public string StoryUrl { get; set; }

        public override bool Equals(object obj)
        {
            if (obj == null)
                return false;
            if (ReferenceEquals(this, obj))
                return true;
            if (obj.GetType() != typeof(RecentActivityModel))
                return false;
            RecentActivityModel other = (RecentActivityModel) obj;
            return base.Equals(other) && StoryUrl == other.StoryUrl;
        }


        public override int GetHashCode()
        {
            unchecked
            {
                return base.GetHashCode() ^ (StoryUrl != null ? StoryUrl.GetHashCode() : 0);
            }
        }

        public override string ToString()
        {
            return string.Format("[RecentActivityModel: StoryUrl={0} {1}]", StoryUrl, base.ToString());
        }
    }
}