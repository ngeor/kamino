// --------------------------------------------------------------------------------
// <copyright file="DbConnectionExtensions.cs" company="Nikolaos Georgiou">
//   Copyright (C) Nikolaos Georgiou 2010-2015
// </copyright>
// <author>Nikolaos Georgiou</author>
// * Date: 2015/10/17
// * Time: 15:09:44
// --------------------------------------------------------------------------------

using System.Data;

namespace BuzzStats.Data
{
    public static class DbConnectionExtensions
    {
        public static object ExecuteScalar(this IDbConnection connection, string sql)
        {
            var cmd = connection.CreateCommand();
            cmd.CommandText = sql;
            return cmd.ExecuteScalar();
        }

        public static int ExecuteNonQuery(this IDbConnection connection, string sql)
        {
            using (var cmd = connection.CreateCommand())
            {
                cmd.CommandText = sql;
                return cmd.ExecuteNonQuery();
            }
        }
    }
}