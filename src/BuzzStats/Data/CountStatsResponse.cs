// --------------------------------------------------------------------------------
// <copyright file="CountStatsResponse.cs" company="Nikolaos Georgiou">
//      Copyright (C) Nikolaos Georgiou 2010-2013
// </copyright>
// <author>Nikolaos Georgiou</author>
// * Date: 2013/09/04
// * Time: 1:27 μμ
// --------------------------------------------------------------------------------

using System;
using System.Collections.Generic;

namespace BuzzStats.Data
{
    public class CountStatsResponse
    {
        /// <summary>
        /// Initializes a new instance of the <see cref="CountStatsResponse"/> class.
        /// </summary>
        /// <remarks>
        /// Needed for serialization.
        /// </remarks>
        public CountStatsResponse()
        {
        }

        public CountStatsResponse(CountStatsRequest request, IEnumerable<GraphPoint<DateTime, int>> graphPoints)
        {
            Start = request.Start.GetValueOrDefault();

            bool isFirst = true;
            List<int> data = new List<int>();
            foreach (var graphPoint in graphPoints)
            {
                if (isFirst)
                {
                    Start = graphPoint.X;
                    isFirst = false;
                }

                data.Add(graphPoint.Y);
            }

            Data = data.ToArray();
        }

        public int[] Data { get; set; }

        public DateTime Start { get; set; }
    }
}