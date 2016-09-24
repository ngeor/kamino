// --------------------------------------------------------------------------------
// <copyright file="JsonSerializer.cs" company="Nikolaos Georgiou">
//   Copyright (C) Nikolaos Georgiou 2010-2015
// </copyright>
// <author>Nikolaos Georgiou</author>
// * Date: 2015/12/18
// * Time: 19:33:11
// --------------------------------------------------------------------------------

using Newtonsoft.Json;
using NGSoftware.Common.WebServices;

namespace BuzzStats.Crawl
{
    class JsonSerializer : ISerializer
    {
        public string SerializeObject(object value)
        {
            return JsonConvert.SerializeObject(value);
        }
    }
}
