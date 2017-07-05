namespace BuzzStats.Crawl
{
    public interface ILeafSource : ISource
    {
        /// <summary>
        /// Gets the identifier of this source.
        /// </summary>
        string SourceId { get; }
    }
}