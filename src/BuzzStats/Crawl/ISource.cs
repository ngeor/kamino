// --------------------------------------------------------------------------------
// <copyright file="ISource.cs" company="Nikolaos Georgiou">
//   Copyright (C) Nikolaos Georgiou 2010-2015
// </copyright>
// <author>Nikolaos Georgiou</author>
// * Date: 2015/10/17
// * Time: 04:51:13
// --------------------------------------------------------------------------------

using System.Collections.Generic;

namespace BuzzStats.Crawl
{
    public interface ISource
    {
        IEnumerable<ISource> GetChildren();
    }
}
