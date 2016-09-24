// --------------------------------------------------------------------------------
// <copyright file="KnownDatabaseProvider.cs" company="Nikolaos Georgiou">
//   Copyright (C) Nikolaos Georgiou 2010-2015
// </copyright>
// <author>Nikolaos Georgiou</author>
// * Date: 2015/10/17
// * Time: 14:26:12
// --------------------------------------------------------------------------------

using System;

namespace BuzzStats.Data
{
    [Flags]
    public enum KnownDatabaseProvider
    {
        SQLite = 1,

        MySql = 2,

        MSSQL = 4
    }
}
