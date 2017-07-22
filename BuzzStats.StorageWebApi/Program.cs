using System;
using System.Threading;
using log4net;
using Microsoft.Owin.Hosting;

namespace BuzzStats.StorageWebApi
{
    public sealed class Program
    {
        private static readonly ILog Log = LogManager.GetLogger(typeof(Program));

        public static IDisposable Start()
        {
            const string baseAddress = "http://localhost:9003/";
            return WebApp.Start<Startup>(baseAddress);
        }
        
        public static void Main(string[] args)
        {
            ManualResetEventSlim done = new ManualResetEventSlim(false);

            Console.CancelKeyPress += (sender, eventArgs) => done.Set();
            
            // Start OWIN host 
            using (Start())
            {
                Log.Info("Server listening at port 9003");
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