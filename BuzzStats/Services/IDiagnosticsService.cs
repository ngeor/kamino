// --------------------------------------------------------------------------------
// <copyright file="IDiagnosticsService.cs" company="Nikolaos Georgiou">
//   Copyright (C) Nikolaos Georgiou 2010-2015
// </copyright>
// <author>Nikolaos Georgiou</author>
// * Date: 2015/12/18
// * Time: 14:10:00
// --------------------------------------------------------------------------------

using System;

namespace BuzzStats.Services
{
    public interface IDiagnosticsService
    {
        TimeSpan UpTime { get; }
        string Echo(string message);
    }
}