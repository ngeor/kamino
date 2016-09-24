// --------------------------------------------------------------------------------
// <copyright file="LeafConsumerFinishedMessage.cs" company="Nikolaos Georgiou">
//   Copyright (C) Nikolaos Georgiou 2010-2015
// </copyright>
// <author>Nikolaos Georgiou</author>
// * Date: 2015/10/17
// * Time: 18:27:29
// --------------------------------------------------------------------------------

namespace BuzzStats.Crawl
{
    public class LeafConsumerFinishedMessage
    {
        public LeafConsumerFinishedMessage()
        {
        }

        public override bool Equals(object obj)
        {
            return (obj as LeafConsumerFinishedMessage) != null;
        }

        public override int GetHashCode()
        {
            return GetType().GetHashCode();
        }
    }
}
