using BuzzStats.Data;

namespace BuzzStats.Crawl
{
    public interface IWarmCache
    {
        RecentActivity[] GetRecentActivity();
        void UpdateRecentActivity();
    }
}