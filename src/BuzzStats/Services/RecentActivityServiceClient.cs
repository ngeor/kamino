// --------------------------------------------------------------------------------
// <copyright file="RecentActivityServiceClient.cs" company="Nikolaos Georgiou">
//   Copyright (C) Nikolaos Georgiou 2010-2015
// </copyright>
// <author>Nikolaos Georgiou</author>
// * Date: 2015/12/18
// * Time: 15:15:19
// --------------------------------------------------------------------------------

using System;
using System.Net.Http;
using System.Net.Http.Headers;
using Newtonsoft.Json;
using BuzzStats.Configuration;
using BuzzStats.Data;

namespace BuzzStats.Services
{
    public class RecentActivityServiceClient : IRecentActivityService
    {
        #region IRecentActivityService implementation

        public RecentActivity[] GetRecentActivity()
        {
            using (HttpClient client = new HttpClient())
            {
                client.BaseAddress = new Uri(
                    BuzzStatsConfigurationSection.Current.Crawler.WebServicesPrefix + "/recentActivity/");

                // Add an Accept header for JSON format.
                client.DefaultRequestHeaders.Accept.Add(new MediaTypeWithQualityHeaderValue("application/json"));

                // Parse the response body. Blocking!
                HttpResponseMessage response = client.GetAsync(string.Empty).Result; // Blocking call!
                string jsonAsString = response.Content.ReadAsStringAsync().Result;
                return JsonConvert.DeserializeObject<RecentActivity[]>(jsonAsString);
            }
        }

        #endregion
    }
}
