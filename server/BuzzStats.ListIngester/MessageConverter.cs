using BuzzStats.Parsing;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace BuzzStats.ListIngester
{
    public class MessageConverter : IMessageConverter
    {
        public MessageConverter(IParserClient parserClient)
        {
            ParserClient = parserClient;
        }

        private IParserClient ParserClient { get; }

        private async Task<IEnumerable<string>> ParseListing(StoryListing storyListing, int page)
        {
            var listings = await ParserClient.ListingAsync(storyListing, page);
            var result = listings
                .Select(listing => $"{listing.StoryId}")
                .ToArray();
            return result;
        }

        public async Task<IEnumerable<string>> ConvertAsync(string msg)
        {
            string[] parts = msg.Split(' ');
            if (parts.Length <= 0 || !Enum.TryParse(parts[0], out StoryListing storyListing))
            {
                storyListing = StoryListing.Home;
            }

            if (parts.Length <= 1 || !int.TryParse(parts[1], out int page))
            {
                page = 0;
            }

            return await ParseListing(storyListing, page);
        }
    }
}
