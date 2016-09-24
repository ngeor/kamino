//
//  HostUtils.cs
//
//  Author:
//       ngeor
//
//  Copyright (c) 2014 ngeor

using System;
using System.Reflection;
using log4net;

namespace BuzzStats.Persister
{
    static class HostUtils
    {
        private static readonly ILog Log = LogManager.GetLogger(MethodBase.GetCurrentMethod().DeclaringType);

        public static string GetHost(string url, int storyId)
        {
            string result;
            try
            {
                result = new Uri(url).Host;
            }
            catch (Exception)
            {
                Log.ErrorFormat("Error extracting host from {0} for story {1}", url, storyId);
                result = string.Empty;
            }

            return result;
        }
    }
}
