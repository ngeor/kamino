// --------------------------------------------------------------------------------
// <copyright file="Version.cs" company="Nikolaos Georgiou">
//   Copyright (C) Nikolaos Georgiou 2010-2014
// </copyright>
// <author>Nikolaos Georgiou</author>
// * Date: 2014/08/09
// * Time: 20:55:45
// --------------------------------------------------------------------------------

namespace BuzzStats
{
    public static class Version
    {
        public static string Get()
        {
            var assembly = typeof(Version).Assembly;
            return assembly.GetName().Version.ToString();
        }
    }
}