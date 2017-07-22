using System;
using System.Collections.Generic;
using System.Threading;
using log4net;
using Microsoft.Owin.Hosting;

namespace BuzzStats.WebApi
{
    internal class Program
    {
        private static readonly ILog Log = LogManager.GetLogger(typeof(Program));
        
        public static void Main(string[] args)
        {
            ManualResetEventSlim done = new ManualResetEventSlim(false);
            const string baseAddress = "http://localhost:9000/";

            Console.CancelKeyPress += (sender, eventArgs) => done.Set();
            
            // Start OWIN host 
            using (WebApp.Start<Startup>(url: baseAddress))
            {
                Log.Info("Server listening at port 9000");
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