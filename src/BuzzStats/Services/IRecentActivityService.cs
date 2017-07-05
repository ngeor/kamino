// --------------------------------------------------------------------------------
// <copyright file="IRecentActivityService.cs" company="Nikolaos Georgiou">
//   Copyright (C) Nikolaos Georgiou 2010-2015
// </copyright>
// <author>Nikolaos Georgiou</author>
// * Date: 2015/12/18
// * Time: 15:15:17
// --------------------------------------------------------------------------------

using BuzzStats.Data;

namespace BuzzStats.Services
{
    public interface IRecentActivityService
    {
        RecentActivity[] GetRecentActivity();
    }
}