//
//  IWebPageRepository.cs
//
//  Author:
//       ngeor
//
//  Copyright (c) 2014 ngeor

using System.Collections.Generic;

namespace BuzzStats.Data
{
    public interface IWebPageRepository
    {
        WebPageData Load(string url);
        ICollection<WebPageData> LoadAll();
        WebPageData Save(WebPageData webPage);
    }
}