//
//  IWebPageHandler.cs
//
//  Author:
//       ngeor
//
//  Copyright (c) 2014 ngeor

namespace BuzzStats.Crawl
{
    public interface IWebPageHandler
    {
        void Process(string url, string contents);
    }
}
