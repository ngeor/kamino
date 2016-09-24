// --------------------------------------------------------------------------------
// <copyright file="IConfiguration.cs" company="Nikolaos Georgiou">
//   Copyright (C) Nikolaos Georgiou 2010-2015
// </copyright>
// <author>Nikolaos Georgiou</author>
// * Date: 2015/10/17
// * Time: 07:23:27
// --------------------------------------------------------------------------------

namespace BuzzStats.Crawl
{
    public interface IConfiguration
    {
        string[] ListingSources { get; }

        bool SkipIngesters { get; }

        bool SkipPollers { get; }
    }
}
