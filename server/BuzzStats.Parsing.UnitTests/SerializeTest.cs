using BuzzStats.DTOs;
using FluentAssertions;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using Newtonsoft.Json;
using System;

namespace BuzzStats.Parsing.UnitTests
{
    [TestClass]
    public class SerializeTest
    {
        [TestMethod]
        public void CanSerializeStory()
        {
            var story = new Story
            {
                StoryId = 42,
                Title = "hello",
                CreatedAt = new DateTime(2018, 1, 26)
            };

            var result = JsonConvert.SerializeObject(story);

            result.Should().StartWith("{").And.EndWith("}");
        }
    }
}
