// --------------------------------------------------------------------------------
// <copyright file="DiagnosticsServiceClient.cs" company="Nikolaos Georgiou">
//   Copyright (C) Nikolaos Georgiou 2010-2015
// </copyright>
// <author>Nikolaos Georgiou</author>
// * Date: 2015/12/18
// * Time: 14:10:03
// --------------------------------------------------------------------------------

using System;
using System.Net.Http;
using System.Net.Http.Headers;
using Newtonsoft.Json;
using BuzzStats.Configuration;

namespace BuzzStats.Services
{
    public class DiagnosticsServiceClient : IDiagnosticsService
    {
        #region IDiagnosticsService implementation

        public string Echo(string message)
        {
            return Act<string>("echo/" + message);
        }

        public TimeSpan UpTime
        {
            get { return Act<TimeSpan>("uptime"); }
        }

        #endregion

        private T Act<T>(string path)
        {
            using (HttpClient client = new HttpClient())
            {
                client.BaseAddress =
                    new Uri(BuzzStatsConfigurationSection.Current.Crawler.WebServicesPrefix + "/diagnostics/");

                // Add an Accept header for JSON format.
                client.DefaultRequestHeaders.Accept.Add(new MediaTypeWithQualityHeaderValue("application/json"));

                // Parse the response body. Blocking!
                HttpResponseMessage response = client.GetAsync(path).Result; // Blocking call!
                string jsonAsString = response.Content.ReadAsStringAsync().Result;
                return JsonConvert.DeserializeObject<T>(jsonAsString);
            }
        }
    }
}
