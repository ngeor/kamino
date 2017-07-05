// --------------------------------------------------------------------------------
// <copyright file="DecoratorSourceProviderTest.cs" company="Nikolaos Georgiou">
//   Copyright (C) Nikolaos Georgiou 2010-2015
// </copyright>
// <author>Nikolaos Georgiou</author>
// * Date: 2015/10/17
// * Time: 09:48:37
// --------------------------------------------------------------------------------

using System.Linq;
using Moq;
using NUnit.Framework;
using BuzzStats.Crawl;

namespace BuzzStats.Tests.Crawl
{
    [TestFixture]
    public class DecoratorSourceProviderTest
    {
        [Test]
        public void ShouldCallDecorated()
        {
            // arrange
            ISource[] expected = new ISource[] {Mock.Of<ISource>()};
            ISource backend = Mock.Of<ISource>(s => s.GetChildren() == expected);

            // act
            DecoratorSource provider = new DecoratorSource(backend);
            ISource[] result = provider.GetChildren().ToArray();

            // assert
            CollectionAssert.AreEqual(expected, result);
        }
    }
}