// --------------------------------------------------------------------------------
// <copyright file="LeafProducerStartingMessage.cs" company="Nikolaos Georgiou">
//   Copyright (C) Nikolaos Georgiou 2010-2015
// </copyright>
// <author>Nikolaos Georgiou</author>
// * Date: 2015/10/17
// * Time: 18:01:09
// --------------------------------------------------------------------------------

namespace BuzzStats.Crawl
{
    public class LeafProducerStartingMessage
    {
        public LeafProducerStartingMessage()
        {
        }

        public override bool Equals(object obj)
        {
            return (obj as LeafProducerStartingMessage) != null;
        }

        public override int GetHashCode()
        {
            return GetType().GetHashCode();
        }
    }
}
