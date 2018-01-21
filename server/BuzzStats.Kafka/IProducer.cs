using System.Threading.Tasks;

namespace BuzzStats.Kafka
{
    public interface IProducer<TKey, TValue>
    {
        Task Post(TKey key, TValue value);
    }
}
