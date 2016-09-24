// --------------------------------------------------------------------------------
// <copyright file="LeafConsumerStartingMessage.cs" company="Nikolaos Georgiou">
//   Copyright (C) Nikolaos Georgiou 2010-2015
// </copyright>
// <author>Nikolaos Georgiou</author>
// * Date: 2015/10/17
// * Time: 18:27:19
// --------------------------------------------------------------------------------

namespace BuzzStats.Crawl
{
    public class LeafConsumerStartingMessage
    {
        public override bool Equals(object obj)
        {
            return (obj as LeafConsumerStartingMessage) != null;
        }

        public override int GetHashCode()
        {
            return GetType().GetHashCode();
        }
    }
}
