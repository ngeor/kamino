using System;
using Microsoft.Owin.Hosting;

namespace BuzzStats.MockServer
{
    internal class Program
    {
        public static void Main(string[] args)
        {
            const string baseAddress = "http://localhost:9900/";

            // Start OWIN host 
            using (WebApp.Start<Startup>(url: baseAddress))
            {
                Console.WriteLine("Server listening at port 9900");
                Console.ReadLine();
            }
        }
    }
}