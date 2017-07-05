//
//  WebPageRepository.cs
//
//  Author:
//       ngeor
//
//  Copyright (c) 2014 ngeor

using System;
using System.Collections.Generic;
using System.Linq;
using NHibernate;
using NHibernate.Linq;
using BuzzStats.Data.NHibernate.Entities;

namespace BuzzStats.Data.NHibernate
{
    internal sealed class WebPageRepository : CoreDataClient, IWebPageRepository
    {
        public WebPageRepository(ISession session) : base(session)
        {
        }

        #region IWebPageRepository implementation

        public WebPageData Load(string url)
        {
            return Session.Query<WebPageEntity>()
                .Where(w => w.Url == url)
                .ToArray()
                .FirstOrDefault()
                .ToData();
        }

        public ICollection<WebPageData> LoadAll()
        {
            return Session.Query<WebPageEntity>().ToList().Select(w => w.ToData()).ToList();
        }

        public WebPageData Save(WebPageData webPage)
        {
            if (webPage == null)
            {
                throw new ArgumentNullException("webPage");
            }

            if (string.IsNullOrWhiteSpace(webPage.Url))
            {
                throw new ArgumentException("webPage.Url cannot be empty");
            }

            Session.SaveOrUpdate(webPage.ToEntity());
            return webPage;
        }

        #endregion
    }
}