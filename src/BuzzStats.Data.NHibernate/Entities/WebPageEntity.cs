//
//  WebPageEntity.cs
//
//  Author:
//       ngeor
//
//  Copyright (c) 2014 ngeor

namespace BuzzStats.Data.NHibernate.Entities
{
    public class WebPageEntity
    {
        public virtual string Url { get; set; }

        public virtual string Plugin { get; set; }
    }
}
