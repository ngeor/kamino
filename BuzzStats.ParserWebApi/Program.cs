using System;
using System.Threading;
using log4net;
using Microsoft.Owin.Hosting;

namespace BuzzStats.ParserWebApi
{
    internal class Program
    {
        private static readonly ILog Log = LogManager.GetLogger(typeof(Program));
        
        public static void Main(string[] args)
        {
            ManualResetEventSlim done = new ManualResetEventSlim(false);
            
            // TODO make this class generic enough and unit testable
            // TODO needs *:9002 for all IP addresses (e.g. public in docker)
            const string baseAddress = "http://localhost:9002/";

            Console.CancelKeyPress += (sender, eventArgs) => done.Set();
            
            // Start OWIN host 
            using (WebApp.Start<Startup>(url: baseAddress))
            {
                Log.Info("Server listening at port 9002");
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