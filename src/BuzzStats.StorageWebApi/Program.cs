using System;
using Microsoft.Owin.Hosting;

namespace BuzzStats.StorageWebApi
{
    public sealed class Program
    {
        public static IDisposable Start()
        {
            const string baseAddress = "http://localhost:9003/";
            return WebApp.Start<Startup>(baseAddress);
        }
        
        public static void Main(string[] args)
        {
            // Start OWIN host 
            using (Start())
            {
                Console.WriteLine("Server listening at port 9003");
                Console.ReadLine();
            }
        }
    }
}