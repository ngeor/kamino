// --------------------------------------------------------------------------------
// <copyright file="StoryHandler.cs" company="Nikolaos Georgiou">
//   Copyright (C) Nikolaos Georgiou 2010-2015
// </copyright>
// <author>Nikolaos Georgiou</author>
// * Date: 2015/10/17
// * Time: 06:19:17
// --------------------------------------------------------------------------------

using System;
using System.Reflection;
using log4net;
using NGSoftware.Common.Messaging;
using BuzzStats.Persister;

namespace BuzzStats.Crawl
{
    public class StoryHandler
    {
        private static readonly ILog Log = LogManager.GetLogger(
            MethodBase.GetCurrentMethod().DeclaringType);

        public StoryHandler(IMessageBus messageBus, IPersister persister)
        {
            if (messageBus == null)
            {
                throw new ArgumentNullException("messageBus");
            }

            if (persister == null)
            {
                throw new ArgumentNullException("persister");
            }

            this.MessageBus = messageBus;
            this.Persister = persister;
            MessageBus.Subscribe<StoryDownloadedMessage>(OnStoryDownloaded);
        }

        private IMessageBus MessageBus { get; set; }

        private IPersister Persister { get; set; }

        void OnStoryDownloaded(StoryDownloadedMessage message)
        {
            if (message == null)
            {
                throw new ArgumentNullException("message");
            }

            if (message.Story == null)
            {
                throw new ArgumentException("null story", "message");
            }

            Log.DebugFormat("Persisting story {0}", message.Story.StoryId);
            PersisterResult result = Persister.Save(message.Story);
            Log.DebugFormat("Result {0}", result);

            if (result.Story == null)
            {
                throw new NullReferenceException("Persister returned null story for " + message.Story.StoryId);
            }

            MessageBus.Publish(new StoryCheckedMessage(result.Story, message.LeafSource, result.Changes));
        }
    }
}