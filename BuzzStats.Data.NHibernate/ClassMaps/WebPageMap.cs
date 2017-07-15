//
//  WebPageMap.cs
//
//  Author:
//       ngeor
//
//  Copyright (c) 2014 ngeor

using FluentNHibernate.Mapping;
using BuzzStats.Data.NHibernate.Entities;

namespace BuzzStats.Data.NHibernate.ClassMaps
{
    public class WebPageMap : ClassMap<WebPageEntity>
    {
        public WebPageMap()
        {
            Table("WebPage");
            Id(x => x.Url);
            Map(x => x.Plugin).Not.Nullable();
        }
    }
}