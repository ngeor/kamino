// --------------------------------------------------------------------------------
// <copyright file="DecoratorSourceProvider.cs" company="Nikolaos Georgiou">
//   Copyright (C) Nikolaos Georgiou 2010-2015
// </copyright>
// <author>Nikolaos Georgiou</author>
// * Date: 2015/10/17
// * Time: 09:49:07
// --------------------------------------------------------------------------------

using System;
using System.Collections.Generic;

namespace BuzzStats.Crawl
{
    public class DecoratorSource : ISource
    {
        private readonly ISource _decorated;

        public DecoratorSource(ISource decorated)
        {
            if (decorated == null)
            {
                throw new ArgumentNullException("decorated");
            }

            _decorated = decorated;
        }

        public virtual IEnumerable<ISource> GetChildren()
        {
            return _decorated.GetChildren();
        }
    }
}