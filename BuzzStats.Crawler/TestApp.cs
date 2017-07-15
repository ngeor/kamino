// --------------------------------------------------------------------------------
// <copyright file="TestApp.cs" company="Nikolaos Georgiou">
//   Copyright (C) Nikolaos Georgiou 2010-2015
// </copyright>
// <author>Nikolaos Georgiou</author>
// * Date: 2015/03/21
// * Time: 16:30:12
// --------------------------------------------------------------------------------

using System;
using System.Threading;
using BuzzStats.Tasks;

namespace BuzzStats.Crawler
{
    [CommandLine("", "just a test app")]
    class TestApp : IApp
    {
        public void Run(string[] args)
        {
            foreach (var arg in args)
            {
                Console.WriteLine(arg);
            }

            int counter = 2;

            Console.CancelKeyPress += (object sender, ConsoleCancelEventArgs e) =>
            {
                Console.WriteLine("Press Ctrl+C again to exit. {0}", counter);
                counter--;
                e.Cancel = true;
            };

            while (counter > 0)
            {
                Console.WriteLine("hello world");
                Thread.Sleep(1000);
            }
        }
    }
}