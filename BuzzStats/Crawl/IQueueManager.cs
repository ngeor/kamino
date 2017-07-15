namespace BuzzStats.Crawl
{
    public interface IQueueManager
    {
        bool InfiniteLoop { get; set; }
        void Start();
    }
}