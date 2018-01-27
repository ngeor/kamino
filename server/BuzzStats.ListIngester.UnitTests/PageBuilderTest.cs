using FluentAssertions;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using System;
using System.Collections.Generic;
using System.Text;

namespace BuzzStats.ListIngester.UnitTests
{
    [TestClass]
    public class PageBuilderTest
    {
        [TestMethod]
        public void BuildSinglePage()
        {
            var result = PageBuilder.Build(1);
            result.Should().Equal("Home", "Upcoming", "EnglishUpcoming", "Tech");
        }

        [TestMethod]
        public void BuildTwoPages()
        {
            var result = PageBuilder.Build(2);
            result.Should().Equal("Home", "Upcoming", "EnglishUpcoming", "Tech",
                "Home 2", "Upcoming 2", "EnglishUpcoming 2", "Tech 2");
        }
    }
}
