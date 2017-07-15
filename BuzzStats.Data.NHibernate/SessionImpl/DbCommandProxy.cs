// --------------------------------------------------------------------------------
// <copyright file="DbCommandProxy.cs" company="Nikolaos Georgiou">
//      Copyright (C) Nikolaos Georgiou 2010-2013
// </copyright>
// <author>Nikolaos Georgiou</author>
// * Date: 2013/09/14
// * Time: 9:55 μμ
// --------------------------------------------------------------------------------

using System.Data;
using System.Data.Common;
using System.Runtime.Remoting.Messaging;
using System.Runtime.Remoting.Proxies;
using StackExchange.Profiling;
using StackExchange.Profiling.Data;

namespace BuzzStats.Data.NHibernate.SessionImpl
{
    internal class DbCommandProxy : RealProxy
    {
        private readonly DbCommand instance;
        private readonly IDbProfiler profiler;

        private DbCommandProxy(DbCommand instance)
            : base(typeof(DbCommand))
        {
            this.instance = instance;
            profiler = MiniProfiler.Current as IDbProfiler;
        }

        public static IDbCommand CreateProxy(IDbCommand instance)
        {
            DbCommandProxy proxy = new DbCommandProxy(instance as DbCommand);
            return proxy.GetTransparentProxy() as IDbCommand;
        }

        public override IMessage Invoke(IMessage msg)
        {
            IMethodCallMessage methodMessage = new MethodCallMessageWrapper((IMethodCallMessage) msg);

            SqlExecuteType executeType = GetExecuteType(methodMessage);

            if (executeType != SqlExecuteType.None)
            {
                profiler.ExecuteStart(instance, executeType);
            }

            object returnValue = methodMessage.MethodBase.Invoke(instance, methodMessage.Args);

            if (executeType == SqlExecuteType.Reader)
            {
                returnValue = new ProfiledDbDataReader((DbDataReader) returnValue, instance.Connection, profiler);
            }

            IMessage returnMessage = new ReturnMessage(
                returnValue, methodMessage.Args, methodMessage.ArgCount, methodMessage.LogicalCallContext,
                methodMessage);

            if (executeType != SqlExecuteType.None)
            {
                profiler.ExecuteFinish(instance, executeType, returnValue as DbDataReader);
            }

            return returnMessage;
        }

        private static SqlExecuteType GetExecuteType(IMethodCallMessage message)
        {
            switch (message.MethodName)
            {
                case "ExecuteNonQuery":
                    return SqlExecuteType.NonQuery;
                case "ExecuteReader":
                    return SqlExecuteType.Reader;
                case "ExecuteScalar":
                    return SqlExecuteType.Scalar;
                default:
                    return SqlExecuteType.None;
            }
        }
    }
}