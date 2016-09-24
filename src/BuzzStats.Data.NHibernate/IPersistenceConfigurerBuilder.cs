//
//  IPersistenceConfigurerBuilder.cs
//
//  Author:
//       ngeor
//
//  Copyright (c) 2014 ngeor

using System.Configuration;
using FluentNHibernate.Cfg.Db;

namespace BuzzStats.Data.NHibernate
{
    internal interface IPersistenceConfigurerBuilder
    {
        IPersistenceConfigurer Create(ConnectionStringSettings connectionString);
    }
}
