// --------------------------------------------------------------------------------
// <copyright file="QueueManager.cs" company="Nikolaos Georgiou">
//   Copyright (C) Nikolaos Georgiou 2010-2015
// </copyright>
// <author>Nikolaos Georgiou</author>
// * Date: 2015/10/17
// * Time: 07:09:27
// --------------------------------------------------------------------------------

using System;
using log4net;
using NGSoftware.Common.Messaging;
using BuzzStats.Downloader;
using BuzzStats.Persister;

namespace BuzzStats.Crawl
{
    public class QueueManager : IQueueManager
    {
        private static readonly ILog Log = LogManager.GetLogger(typeof (QueueManager));
        private readonly LeafProducer _leafProducer;
        private int _loopCount;

        public QueueManager(
            IMessageBus messageBus,
            IDownloaderService downloader,
            IPersister persister,
            ISource seedSource)
        {
            Log.Debug("Constructor");
            if (messageBus == null)
            {
                throw new ArgumentNullException("messageBus");
            }

            if (downloader == null)
            {
                throw new ArgumentNullException("downloader");
            }

            if (persister == null)
            {
                throw new ArgumentNullException("persister");
            }

            if (seedSource == null)
            {
                throw new ArgumentNullException("seedSource");
            }

            _leafProducer = new LeafProducer(messageBus, seedSource);
            messageBus.Subscribe<LeafConsumerFinishedMessage>(OnLeafConsumerFinished);
            LeafConsumer lc = new LeafConsumer(messageBus, downloader);
            StoryHandler sh = new StoryHandler(messageBus, persister);
        }

        public int AllowedLoops { get; set; }

        public bool InfiniteLoop { get; set; }

        public void Start()
        {
            while (InfiniteLoop || _loopCount < AllowedLoops)
            {
                try
                {
                    Tick();
                }
                catch (LeafConsumerFinishedException)
                {
                }
            }
        }

        private void Tick()
        {
            _loopCount++;
            Log.DebugFormat("Starting loop {0}", _loopCount);
            _leafProducer.Start();
        }

        void OnLeafConsumerFinished(LeafConsumerFinishedMessage obj)
        {
            // to be able to break out of the message stack
            // without causing stack overflow
            // and without using threads
            throw new LeafConsumerFinishedException();
        }

        class LeafConsumerFinishedException : Exception
        {
        }
    }
}
