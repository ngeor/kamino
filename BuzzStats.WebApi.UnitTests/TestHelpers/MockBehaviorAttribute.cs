using System;
using Moq;

namespace BuzzStats.WebApi.UnitTests.TestHelpers
{
    [AttributeUsage(AttributeTargets.Field)]
    public class MockBehaviorAttribute : Attribute
    {
        public MockBehaviorAttribute(MockBehavior behavior)
        {
            Behavior = behavior;
        }

        public MockBehavior Behavior { get; set; }
    }
}