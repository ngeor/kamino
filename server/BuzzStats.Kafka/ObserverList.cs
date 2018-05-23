using System;
using System.Collections.Generic;

namespace BuzzStats.Kafka
{
    class ObserverList<T>
    {
        private readonly List<IObserver<T>> observers = new List<IObserver<T>>();

        public IDisposable Add(IObserver<T> observer)
        {
            lock (observers)
            {
                observers.Add(observer);
                return new Subscription(this, observer);
            }
        }
        
        public IEnumerable<IObserver<T>> ToEnumerable()
        {
            lock (observers)
            {
                return observers.ToArray();
            }
        }

        class Subscription : IDisposable
        {
            private readonly ObserverList<T> list;
            private readonly IObserver<T> observer;

            public Subscription(ObserverList<T> list, IObserver<T> observer)
            {
                this.list = list;
                this.observer = observer;
            }

            public void Dispose()
            {
                list.observers.Remove(observer);
            }
        }
    }
}
