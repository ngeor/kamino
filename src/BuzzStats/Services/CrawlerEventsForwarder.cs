// --------------------------------------------------------------------------------
// <copyright file="CrawlerEventsForwarder.cs" company="Nikolaos Georgiou">
//   Copyright (C) Nikolaos Georgiou 2010-2015
// </copyright>
// <author>Nikolaos Georgiou</author>
// * Date: 2015/01/24
// * Time: 18:45:03
// --------------------------------------------------------------------------------

using System;
using System.ServiceModel;
using log4net;
using NGSoftware.Common.Messaging;
using BuzzStats.BuzzStatsCrawlerEvents;
using BuzzStats.Crawl;
using BuzzStats.Persister;

namespace BuzzStats.Services
{
    public sealed class CrawlerEventsForwarder
    {
        private static readonly ILog Log = LogManager.GetLogger(typeof (CrawlerEventsForwarder));

        public CrawlerEventsForwarder(
            IMessageBus messageBus)
        {
            messageBus.Subscribe<StoryCheckedMessage>(OnStoryChecked);
        }

        void OnStoryChecked(StoryCheckedMessage message)
        {
            // TODO: introduce NullEventForwarder
            Log.DebugFormat("Forwarding event to client");
            try
            {
                using (var proxy = new CrawlerEventsClient())
                {
                    proxy.StoryChecked(new StoryCheckedEventArgs
                    {
                        HadChanges = message.Changes != UpdateResult.NoChanges,
                        SelectorName = message.LeafSource.SourceId,
                        StoryId = message.Story.StoryId
                    });
                }

                Log.DebugFormat("Event forwarded");
            }
            catch (EndpointNotFoundException ex)
            {
                Log.Warn("No endpoint listening", ex);
            }
            catch (Exception ex)
            {
                Log.Error(string.Format("Error forwarding event {0}", ex.Message));
            }
        }
    }
}
