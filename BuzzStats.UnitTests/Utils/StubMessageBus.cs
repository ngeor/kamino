// --------------------------------------------------------------------------------
// <copyright file="StubMessageBus.cs" company="Nikolaos Georgiou">
//   Copyright (C) Nikolaos Georgiou 2010-2015
// </copyright>
// <author>Nikolaos Georgiou</author>
// * Date: 2015/10/16
// * Time: 05:37:48
// --------------------------------------------------------------------------------

using System;
using System.Collections.Generic;
using NGSoftware.Common.Messaging;

namespace BuzzStats.UnitTests.Utils
{
    public class StubMessageBus : IMessageBus
    {
        private readonly Dictionary<Type, List<object>> _messages = new Dictionary<Type, List<object>>();
        private readonly Dictionary<Type, List<Delegate>> _handlers = new Dictionary<Type, List<Delegate>>();

        public void Publish<T>(T message)
        {
            Type t = typeof(T);
            _messages.Ensure(t).Add(message);
            foreach (var handler in _handlers.Ensure(t))
            {
                (handler as Action<T>)(message);
            }
        }

        public bool ContainsAny<T>()
        {
            var list = _messages.Ensure(typeof(T));
            return list.Count > 0;
        }

        public bool Contains<T>(T message)
        {
            var list = _messages.Ensure(typeof(T));
            return list.Contains(message);
        }

        public bool ContainsExclusively<T>(T message)
        {
            var list = _messages.Ensure(typeof(T));
            return list.Contains(message) && list.Count == 1;
        }

        public void Subscribe<T>(Action<T> handler)
        {
            Type t = typeof(T);
            _handlers.Ensure(t).Add(handler as Delegate);
        }

        public void Unsubscribe<T>(Action<T> handler)
        {
            throw new NotImplementedException();
        }
    }
}