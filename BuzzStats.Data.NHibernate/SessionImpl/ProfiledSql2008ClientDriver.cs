// --------------------------------------------------------------------------------
// <copyright file="ProfiledSql2008ClientDriver.cs" company="Nikolaos Georgiou">
//      Copyright (C) Nikolaos Georgiou 2010-2013
// </copyright>
// <author>Nikolaos Georgiou</author>
// * Date: 2013/09/14
// * Time: 9:54 μμ
// --------------------------------------------------------------------------------

using System.Data;
using NHibernate.Driver;
using StackExchange.Profiling;

namespace BuzzStats.Data.NHibernate.SessionImpl
{
    internal class ProfiledSql2008ClientDriver : Sql2008ClientDriver
    {
        public override IDbCommand CreateCommand()
        {
            IDbCommand command = base.CreateCommand();

            if (MiniProfiler.Current != null)
            {
                command = DbCommandProxy.CreateProxy(command);
            }

            return command;
        }
    }
}