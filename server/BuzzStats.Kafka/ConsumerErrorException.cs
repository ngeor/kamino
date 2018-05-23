using Confluent.Kafka;

namespace BuzzStats.Kafka
{
    public class ConsumerErrorException : ConsumerException
    {
        public ConsumerErrorException(Error error)
        {            
            Error = error;
        }

        public override string Message => Error.ToString();

        public Error Error { get; }
    }
}
