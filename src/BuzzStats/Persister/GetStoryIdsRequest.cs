// --------------------------------------------------------------------------------
// <copyright file="GetStoryIdsRequest.cs" company="Nikolaos Georgiou">
//   Copyright (C) Nikolaos Georgiou 2010-2015
// </copyright>
// <author>Nikolaos Georgiou</author>
// * Date: 2015/05/30
// * Time: 07:58:13
// --------------------------------------------------------------------------------

using System.Runtime.Serialization;
using NGSoftware.Common;

namespace BuzzStats.Persister
{
    [DataContract]
    public class GetStoryIdsRequest
    {
        [DataMember]
        public int Count { get; set; }

        [DataMember]
        public TimeSpanRange? LastCheckedAt { get; set; }
    }
}
