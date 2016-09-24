// --------------------------------------------------------------------------------
// <copyright file="AggregateSourceProviderTest.cs" company="Nikolaos Georgiou">
//   Copyright (C) Nikolaos Georgiou 2010-2015
// </copyright>
// <author>Nikolaos Georgiou</author>
// * Date: 2015/10/17
// * Time: 09:54:40
// --------------------------------------------------------------------------------

using System.Linq;
using Moq;
using NUnit.Framework;
using BuzzStats.Crawl;

namespace BuzzStats.Tests.Crawl
{
    [TestFixture]
    public class AggregateSourceTest
    {
        [Test]
        public void ShouldAggregateAllBackendResults()
        {
            // arrange
            ISource source1 = Mock.Of<ISource>();
            ISource[] expected1 = new ISource[] {source1};
            ISource backend1 = Mock.Of<ISource>(s => s.GetChildren() == expected1);

            ISource source2 = Mock.Of<ISource>();
            ISource[] expected2 = new ISource[] {source2};
            ISource backend2 = Mock.Of<ISource>(s => s.GetChildren() == expected2);

            // act
            AggregateSource provider = new AggregateSource(backend1, backend2);
            ISource[] result = provider.GetChildren().ToArray();

            // assert
            CollectionAssert.AreEqual(new[] {backend1, backend2}, result);
        }

        [Test]
        public void ShouldAggregateAllBackendResultsAndKeepDuplicates()
        {
            // arrange
            ISource source1 = Mock.Of<ISource>();
            ISource[] expected1 = new ISource[] {source1};
            ISource backend1 = Mock.Of<ISource>(s => s.GetChildren() == expected1);

            ISource source2 = Mock.Of<ISource>();
            ISource[] expected2 = new ISource[] {source2, source1};
            ISource backend2 = Mock.Of<ISource>(s => s.GetChildren() == expected2);

            // act
            AggregateSource provider = new AggregateSource(backend1, backend2);
            ISource[] result = provider.GetChildren().ToArray();

            // assert
            CollectionAssert.AreEqual(new[] {backend1, backend2}, result);
        }
    }
}
