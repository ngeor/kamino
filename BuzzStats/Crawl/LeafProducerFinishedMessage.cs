// --------------------------------------------------------------------------------
// <copyright file="LeafProducerFinishedMessage.cs" company="Nikolaos Georgiou">
//   Copyright (C) Nikolaos Georgiou 2010-2015
// </copyright>
// <author>Nikolaos Georgiou</author>
// * Date: 2015/10/17
// * Time: 18:04:45
// --------------------------------------------------------------------------------

using System;
using System.Collections.Generic;

namespace BuzzStats.Crawl
{
    public class LeafProducerFinishedMessage
    {
        public LeafProducerFinishedMessage(IEnumerable<ILeaf> leaves)
        {
            if (leaves == null)
            {
                throw new ArgumentNullException("leaves");
            }

            Leaves = leaves;
        }

        public IEnumerable<ILeaf> Leaves { get; private set; }
    }
}