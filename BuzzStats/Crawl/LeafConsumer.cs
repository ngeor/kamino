// --------------------------------------------------------------------------------
// <copyright file="LeafConsumer.cs" company="Nikolaos Georgiou">
//   Copyright (C) Nikolaos Georgiou 2010-2015
// </copyright>
// <author>Nikolaos Georgiou</author>
// * Date: 2015/10/17
// * Time: 18:27:10
// --------------------------------------------------------------------------------

using System;
using NGSoftware.Common.Messaging;
using BuzzStats.Downloader;

namespace BuzzStats.Crawl
{
    public class LeafConsumer
    {
        private readonly IMessageBus _messageBus;
        private readonly IDownloaderService _downloader;

        public LeafConsumer(IMessageBus messageBus, IDownloaderService downloader)
        {
            if (messageBus == null)
            {
                throw new ArgumentNullException("messageBus");
            }

            _messageBus = messageBus;

            if (downloader == null)
            {
                throw new ArgumentNullException("downloader");
            }

            _downloader = downloader;

            _messageBus.Subscribe<LeafProducerFinishedMessage>(OnProducerFinished);
        }

        void OnProducerFinished(LeafProducerFinishedMessage message)
        {
            _messageBus.Publish(new LeafConsumerStartingMessage());

            foreach (var leaf in message.Leaves)
            {
                leaf.Update(_downloader, _messageBus);
            }

            _messageBus.Publish(new LeafConsumerFinishedMessage());
        }
    }
}