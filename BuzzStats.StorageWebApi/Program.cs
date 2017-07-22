using System;
using System.Threading;
using log4net;
using Microsoft.Owin.Hosting;
using NGSoftware.Common.Configuration;

namespace BuzzStats.StorageWebApi
{
    public sealed class Program
    {
        private static readonly ILog Log = LogManager.GetLogger(typeof(Program));

        private static string BaseAddress()
        {
            IAppSettings appSettings = AppSettingsFactory.DefaultWithEnvironmentOverride();
            return appSettings["StorageWebApiUrl"];
        }

        public static IDisposable Start()
        {
            return WebApp.Start<Startup>(BaseAddress());
        }
        
        public static void Main(string[] args)
        {
            ManualResetEventSlim done = new ManualResetEventSlim(false);

            Console.CancelKeyPress += (sender, eventArgs) => done.Set();
            
            // Start OWIN host 
            using (Start())
            {
                Log.InfoFormat("Server listening at {0}", BaseAddress());
                if (!Console.IsInputRedirected)
                {
                    Console.ReadLine();
                    done.Set();
                }
                
                done.Wait();
                Log.Info("Server exiting");
            }
        }
    }
}