using System;
using Microsoft.Owin.Hosting;

namespace BuzzStats.StorageWebApi
{
    internal class Program
    {
        public static void Main(string[] args)
        {
            const string baseAddress = "http://localhost:9003/";

            // Start OWIN host 
            using (WebApp.Start<Startup>(url: baseAddress))
            {
                Console.WriteLine("Server listening at port 9003");
                Console.ReadLine();
            }
        }
    }
}