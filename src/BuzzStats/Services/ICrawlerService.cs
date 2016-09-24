// --------------------------------------------------------------------------------
// <copyright file="ICrawlerService.cs" company="Nikolaos Georgiou">
//      Copyright (C) Nikolaos Georgiou 2010-2013
// </copyright>
// <author>Nikolaos Georgiou</author>
// * Date: 2013/09/04
// * Time: 1:27 μμ
// --------------------------------------------------------------------------------

using System;

namespace BuzzStats.Services
{
    public interface ICrawlerService
    {
        TimeSpan GetUpTime();
    }
}
