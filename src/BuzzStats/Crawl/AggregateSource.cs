// --------------------------------------------------------------------------------
// <copyright file="AggregateSourceProvider.cs" company="Nikolaos Georgiou">
//   Copyright (C) Nikolaos Georgiou 2010-2015
// </copyright>
// <author>Nikolaos Georgiou</author>
// * Date: 2015/10/17
// * Time: 09:54:19
// --------------------------------------------------------------------------------

using System;
using System.Collections.Generic;

namespace BuzzStats.Crawl
{
    public class AggregateSource : ISource
    {
        private readonly ISource[] _decorated;

        public AggregateSource(params ISource[] decorated)
        {
            if (decorated == null)
            {
                throw new ArgumentNullException("decorated");
            }

            _decorated = decorated;
        }

        public IEnumerable<ISource> GetChildren()
        {
            return _decorated;
        }
    }
}
