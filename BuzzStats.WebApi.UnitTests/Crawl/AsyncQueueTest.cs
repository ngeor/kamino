using System;
using BuzzStats.WebApi.Crawl;
using NUnit.Framework;

namespace BuzzStats.WebApi.UnitTests.Crawl
{
    [TestFixture]
    public class AsyncQueueTest
    {
        private AsyncQueue<string> _queue;

        [SetUp]
        public void SetUp()
        {
            _queue = new AsyncQueue<string>(TimeSpan.FromSeconds(2));
        }
        
        [Test]
        public void PushPop()
        {
            _queue.Push("hello");
            Assert.AreEqual("hello", _queue.Pop());
        }
        
        [Test]
        public void PopWithoutPush()
        {
            Assert.Throws<TimeoutException>(() => { _queue.Pop(); });
        }

        [Test]
        public void DoublePushDoublePop()
        {
            _queue.Push("hey");
            _queue.Push("bye");
            Assert.AreEqual("hey", _queue.Pop());
            Assert.AreEqual("bye", _queue.Pop());
        }

        [Test]
        public void TooManyPops()
        {
            _queue.Push("hello");
            Assert.AreEqual("hello", _queue.Pop());
            Assert.Throws<TimeoutException>(() => { _queue.Pop(); });
        }
    }
}