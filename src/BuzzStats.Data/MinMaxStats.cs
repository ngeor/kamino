// --------------------------------------------------------------------------------
// <copyright file="MinMaxStats.cs" company="Nikolaos Georgiou">
//   Copyright (C) Nikolaos Georgiou 2010-2014
// </copyright>
// <author>Nikolaos Georgiou</author>
// * Date: 2014/08/09
// * Time: 18:50:52
// --------------------------------------------------------------------------------

using System;
using System.Runtime.Serialization;
using NGSoftware.Common;

namespace BuzzStats.Data
{
    [DataContract]
    public struct MinMaxStats
    {
        [DataMember]
        public MinMaxValue<DateTime> LastCheckedAt { get; set; }

        [DataMember]
        public MinMaxValue<int> TotalChecks { get; set; }
    }
}
