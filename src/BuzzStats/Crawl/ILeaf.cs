// --------------------------------------------------------------------------------
// <copyright file="ILeaf.cs" company="Nikolaos Georgiou">
//   Copyright (C) Nikolaos Georgiou 2010-2015
// </copyright>
// <author>Nikolaos Georgiou</author>
// * Date: 2015/10/17
// * Time: 16:48:22
// --------------------------------------------------------------------------------

using NGSoftware.Common.Messaging;
using BuzzStats.Downloader;

namespace BuzzStats.Crawl
{
    public interface ILeaf : ISource
    {
        int StoryId { get; }
        void Update(IDownloaderService downloader, IMessageBus messageBus);
    }
}