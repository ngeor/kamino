using log4net;
using System;
using System.Linq;
using System.Threading;

namespace BuzzStats.ListIngester
{
    public class Cron : IDisposable
    {
        private static readonly ILog Log = LogManager.GetLogger(typeof(Cron));
        private readonly string[] inputMessages;
        private readonly IMessagePublisher messagePublisher;
        private Timer timer;
        private int messageIndex;

        public Cron(IMessagePublisher messagePublisher, TimeSpan dueTime, TimeSpan period, params string[] inputMessages)
        {
            this.messagePublisher = messagePublisher ?? throw new ArgumentNullException(nameof(messagePublisher));
            this.inputMessages = inputMessages.Any() ? inputMessages : new[] { "Home" };
            timer = new Timer(HandleTimer, null, dueTime, period);
        }

        private void HandleTimer(object state)
        {
            if (timer == null)
            {
                // we've been disposed and this is a late event
                return;
            }

            Log.Info("Triggering update");
            try
            {
                messagePublisher.HandleMessage(inputMessages[messageIndex]);
            } catch(Exception ex)
            {
                Log.Error(ex.Message, ex);
            }
            finally
            {
                messageIndex = (messageIndex + 1) % inputMessages.Length;
            }
        }

        #region IDisposable Support
        private bool disposedValue = false; // To detect redundant calls

        protected virtual void Dispose(bool disposing)
        {
            if (!disposedValue)
            {
                if (disposing)
                {
                    timer?.Dispose();
                }

                timer = null;
                disposedValue = true;
            }
        }

        // This code added to correctly implement the disposable pattern.
        public void Dispose()
        {
            // Do not change this code. Put cleanup code in Dispose(bool disposing) above.
            Dispose(true);
            GC.SuppressFinalize(this);
        }
        #endregion
    }
}
