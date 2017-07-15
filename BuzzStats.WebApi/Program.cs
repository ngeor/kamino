using System;
using System.Collections.Generic;
using Microsoft.Owin.Hosting;

namespace BuzzStats.WebApi
{
    internal class Program
    {
        public static void Main(string[] args)
        {
            const string baseAddress = "http://localhost:9000/";

            // Start OWIN host 
            using (WebApp.Start<Startup>(url: baseAddress))
            {
                Console.WriteLine("Server listening at port 9000");
                Console.ReadLine();
            }
        }
    }
}