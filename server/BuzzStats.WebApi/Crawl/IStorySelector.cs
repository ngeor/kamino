using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using BuzzStats.Parsing;
using BuzzStats.Parsing.DTOs;
using BuzzStats.WebApi.DTOs;
using BuzzStats.WebApi.Storage;

namespace BuzzStats.WebApi.Crawl
{
    public interface IStorySelector
    {
        Task<IList<StoryListingSummary>> Select();
    }

    /// <summary>
    /// Selects stories from a listing page that do not already exist in the database.
    /// </summary>
    class IngestingStorySelector : IStorySelector
    {
        private readonly IParserClient _parserClient;
        private readonly IStorageClient _storageClient;

        public StoryListing StoryListing { get; set; }
        public int Page { get; set; }

        public async Task<IList<StoryListingSummary>> Select()
        {
            var storyListingSummaries = (await _parserClient.Listing(StoryListing, Page)).ToArray();
            throw new NotImplementedException();
        }
    }
}
