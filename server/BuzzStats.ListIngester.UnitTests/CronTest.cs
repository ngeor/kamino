using FluentAssertions;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using Moq;
using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.Threading;

namespace BuzzStats.ListIngester.UnitTests
{
    [TestClass]
    public class CronTest
    {
        const int timerEpsilon = 314; // allow some delay in msec for matching method duration

        private Cron cron;

        [TestCleanup]
        public void Cleanup()
        {
            cron?.Dispose();
            cron = null;
        }

        [TestMethod]
        [ExpectedException(typeof(ArgumentNullException))]
        public void MessagePublisherIsMandatory()
        {
            new Cron(null, TimeSpan.FromSeconds(1), TimeSpan.FromMinutes(1));
        }

        [TestMethod]
        public void PublishesMessageOnce()
        {
            var waitHandle = new ManualResetEventSlim();
            var messagePublisherMock = new Mock<IMessagePublisher>();
            messagePublisherMock.Setup(p => p.HandleMessage("Home"))
                .Callback(() =>
                {
                    waitHandle.Set();
                });

            var stopwatch = Stopwatch.StartNew();

            // act
            cron = new Cron(messagePublisherMock.Object, TimeSpan.FromSeconds(1), TimeSpan.FromMinutes(1));

            // post-act
            waitHandle.Wait(TimeSpan.FromSeconds(5));
            stopwatch.Stop();
            
            // assert
            messagePublisherMock.Verify(p => p.HandleMessage("Home"), Times.Once());
            stopwatch.Elapsed.Should().BeCloseTo(TimeSpan.FromSeconds(1), precision: timerEpsilon);
        }

        [TestMethod]
        public void PublishesMessageTwice()
        {
            var callCount = 0;
            var waitHandle = new ManualResetEventSlim();
            var messagePublisherMock = new Mock<IMessagePublisher>();
            messagePublisherMock.Setup(p => p.HandleMessage("Home"))
                .Callback(() =>
                {
                    callCount++;
                    if (callCount >= 2)
                    {
                        waitHandle.Set();
                    }
                });

            var stopwatch = Stopwatch.StartNew();

            // act
            cron = new Cron(messagePublisherMock.Object, TimeSpan.FromSeconds(1), TimeSpan.FromSeconds(1));

            // post-act
            waitHandle.Wait(TimeSpan.FromSeconds(5));
            stopwatch.Stop();

            // assert
            messagePublisherMock.Verify(p => p.HandleMessage("Home"), Times.Exactly(2));
            stopwatch.Elapsed.Should().BeCloseTo(TimeSpan.FromSeconds(2), precision: timerEpsilon);
        }

        [TestMethod]
        public void PublishesMessageRotating()
        {
            var waitHandle = new ManualResetEventSlim();
            var messagePublisherMock = new Mock<IMessagePublisher>();
            var messages = new List<string>();
            messagePublisherMock.Setup(p => p.HandleMessage(It.IsAny<string>()))
                .Callback<string>(msg =>
                {
                    messages.Add(msg);
                    if (messages.Count >= 3)
                    {
                        waitHandle.Set();
                    }
                });

            var stopwatch = Stopwatch.StartNew();

            // act
            cron = new Cron(messagePublisherMock.Object, TimeSpan.FromSeconds(1), TimeSpan.FromSeconds(1), "Home", "Upcoming");

            // post-act
            waitHandle.Wait(TimeSpan.FromSeconds(5));
            stopwatch.Stop();

            // assert
            messages.Should().Equal("Home", "Upcoming", "Home");
            stopwatch.Elapsed.Should().BeCloseTo(TimeSpan.FromSeconds(3), precision: timerEpsilon);
        }
    }
}
