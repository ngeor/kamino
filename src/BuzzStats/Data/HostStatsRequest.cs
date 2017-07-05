// --------------------------------------------------------------------------------
// <copyright file="HostStatsRequest.cs" company="Nikolaos Georgiou">
//      Copyright (C) Nikolaos Georgiou 2010-2013
// </copyright>
// <author>Nikolaos Georgiou</author>
// * Date: 2013/10/04
// * Time: 10:03 μμ
// --------------------------------------------------------------------------------

namespace BuzzStats.Data
{
    /// <summary>
    /// Request for <see cref="IApiService.GetHostStats"/> method.
    /// </summary>
    public class HostStatsRequest : CountStatsRequest
    {
        public int MaxResults { get; set; }
        public int MinStoryCount { get; set; }
        public string SortExpression { get; set; }
        public int StartIndex { get; set; }
    }
}