// --------------------------------------------------------------------------------
// <copyright file="LeafProducer.cs" company="Nikolaos Georgiou">
//   Copyright (C) Nikolaos Georgiou 2010-2015
// </copyright>
// <author>Nikolaos Georgiou</author>
// * Date: 2015/10/17
// * Time: 17:46:35
// --------------------------------------------------------------------------------

using System;
using System.Collections.Generic;
using System.Linq;
using log4net;
using NGSoftware.Common.Messaging;

namespace BuzzStats.Crawl
{
    public class LeafProducer : ILeafProducer, ILeafProducerMonitor
    {
        private static readonly ILog Log = LogManager.GetLogger(typeof (LeafProducer));
        private readonly HashSet<ILeaf> _leafQueue = new HashSet<ILeaf>();
        private readonly IMessageBus _messageBus;
        private readonly ISource _seedSource;

        public LeafProducer(
            IMessageBus messageBus,
            ISource seedSource)
        {
            if (messageBus == null)
            {
                throw new ArgumentNullException("messageBus");
            }

            _messageBus = messageBus;

            if (seedSource == null)
            {
                throw new ArgumentNullException("seedSource");
            }

            _seedSource = seedSource;
        }

        public ILeaf[] GetCollectedLeaves()
        {
            return _leafQueue.ToArray();
        }

        public void Start()
        {
            _messageBus.Publish(new LeafProducerStartingMessage());
            _leafQueue.Clear();
            Collect();
            _messageBus.Publish(new LeafProducerFinishedMessage(_leafQueue));
        }

        private void Collect()
        {
            LinkedList<ISource> sources = new LinkedList<ISource>();
            Add(sources, _seedSource);

            while (sources.Count > 0)
            {
                ISource source = sources.First.Value;
                sources.RemoveFirst();
                ILeaf leaf = source as ILeaf;
                if (leaf != null)
                {
                    if (_leafQueue.Add(leaf))
                    {
                        Log.DebugFormat("Enqueued leaf {0}", leaf);
                        _messageBus.Publish(new LeafProducerFoundLeafMessage(leaf));
                    }
                    else
                    {
                        Log.DebugFormat("Skipping leaf {0}", leaf);
                    }
                }
                else
                {
                    Log.DebugFormat("Getting children of {0}", source);
                    foreach (var child in source.GetChildren())
                    {
                        Add(sources, child);
                    }
                }
            }
        }

        private void Add(LinkedList<ISource> sources, ISource source)
        {
            if (source is ILeaf)
            {
                sources.AddFirst(source);
            }
            else
            {
                sources.AddLast(source);
            }
        }
    }
}
