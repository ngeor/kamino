using System;
using System.Threading;
using log4net;
using Microsoft.Owin.Hosting;
using NGSoftware.Common.Configuration;

namespace BuzzStats.ParserWebApi
{
    internal class Program
    {
        private static readonly ILog Log = LogManager.GetLogger(typeof(Program));
        
        public static void Main(string[] args)
        {
            ManualResetEventSlim done = new ManualResetEventSlim(false);
            IAppSettings appSettings = AppSettingsFactory.DefaultWithEnvironmentOverride();

            // TODO make this class generic enough and unit testable
            string baseAddress = appSettings["ParserWebApiUrl"];

            Console.CancelKeyPress += (sender, eventArgs) => done.Set();
            
            // Start OWIN host 
            using (WebApp.Start<Startup>(url: baseAddress))
            {
                Log.InfoFormat("Server listening at {0}", baseAddress);
                if (!Console.IsInputRedirected)
                {
                    Console.WriteLine("Press Enter to exit");
                    Console.ReadLine();
                    done.Set();
                }
                else
                {
                    Log.Info("App is running");
                }
                
                done.Wait();
                Log.Info("Server exiting");
            }
        }
    }
}