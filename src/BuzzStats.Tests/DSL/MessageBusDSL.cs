// --------------------------------------------------------------------------------
// <copyright file="MessageBusDSL.cs" company="Nikolaos Georgiou">
//   Copyright (C) Nikolaos Georgiou 2010-2015
// </copyright>
// <author>Nikolaos Georgiou</author>
// * Date: 2015/10/17
// * Time: 18:50:52
// --------------------------------------------------------------------------------

using System;
using System.Linq.Expressions;
using Moq;
using NGSoftware.Common.Messaging;

namespace BuzzStats.Tests.DSL
{
    public static class MessageBusDSL
    {
        public static IMessageBus VerifySingleMessage<TMessage>(this IMessageBus messageBus)
        {
            var mock = Mock.Get<IMessageBus>(messageBus);
            mock.Verify(p => p.Publish<TMessage>(It.IsAny<TMessage>()), Times.Once());
            return messageBus;
        }

        public static IMessageBus VerifyNoMessage<TMessage>(this IMessageBus messageBus)
        {
            var mock = Mock.Get<IMessageBus>(messageBus);
            mock.Verify(p => p.Publish<TMessage>(It.IsAny<TMessage>()), Times.Never());
            return messageBus;
        }

        public static IMessageBus VerifySingleMessage<TMessage>(
            this IMessageBus messageBus,
            Expression<Func<TMessage, bool>> criteria,
            string failMessage = null)
        {
            var mock = Mock.Get<IMessageBus>(messageBus);
            mock.Verify(p => p.Publish<TMessage>(It.Is<TMessage>(criteria)), Times.Once(), failMessage);
            return messageBus;
        }

        public static IMessageBus SendUponSubscription<TMessage>(this IMessageBus messageBus, TMessage message)
        {
            var mock = Mock.Get<IMessageBus>(messageBus);
            mock.Setup(p => p.Subscribe<TMessage>(It.IsAny<Action<TMessage>>()))
                .Callback<Action<TMessage>>(h => h(message));
            return messageBus;
        }

        public static IMessageBus SendDefaultReplyUponSubscription<TMessage>(this IMessageBus messageBus)
            where TMessage : class, new()
        {
            var mock = Mock.Get<IMessageBus>(messageBus);
            mock.Setup(p => p.Subscribe<TMessage>(It.IsAny<Action<TMessage>>()))
                .Callback<Action<TMessage>>(h => h(new TMessage()));
            return messageBus;
        }

        public static IMessageBus OnReceive<TMessage>(this IMessageBus messageBus, Action<TMessage> handler)
        {
            var mock = Mock.Get<IMessageBus>(messageBus);
            mock.Setup(p => p.Publish<TMessage>(It.IsAny<TMessage>()))
                .Callback<TMessage>(message => handler(message));
            return messageBus;
        }
    }
}
