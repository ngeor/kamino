using Confluent.Kafka;

namespace BuzzStats.Kafka
{
    public class ConsumerMessageException : ConsumerException
    {
        public ConsumerMessageException(Message errorMessage)
        {
            ErrorMessage = errorMessage;
        }

        public Message ErrorMessage { get; }

        public override string ToString()
        {
            return $"ConsumerMessageException: ${ErrorMessage}";
        }
    }
}
