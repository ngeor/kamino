namespace BuzzStats.WebApi.Crawl
{
    public interface IAsyncQueue<T>
    {
        void Push(T item);
        T Pop();
    }
}