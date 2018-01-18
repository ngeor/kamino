using System;
using System.Collections.Generic;
using System.Threading;

namespace BuzzStats.WebApi.Crawl
{
    public class AsyncQueue<T> : IAsyncQueue<T>
    {
        private readonly Queue<T> _queue = new Queue<T>();
        private readonly ManualResetEvent _waitHandle = new ManualResetEvent(false);
        private readonly TimeSpan _timeout;

        public AsyncQueue(TimeSpan timeout)
        {
            _timeout = timeout;
        }

        public void Push(T item)
        {
            lock (_queue)
            {
                _queue.Enqueue(item);
                _waitHandle.Set();
            }
        }

        public T Pop()
        {
            T result;
            if (!_waitHandle.WaitOne(_timeout))
            {
                throw new TimeoutException();
            }

            lock (_queue)
            {
                result = _queue.Dequeue();
                if (_queue.Count == 0)
                {
                    _waitHandle.Reset();
                }
            }

            return result;
        }
    }
}