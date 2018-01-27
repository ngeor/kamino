using System.Threading.Tasks;

namespace BuzzStats.Kafka
{
    public interface IProducer<in TKey, in TValue>
    {
        Task Post(TKey key, TValue value);
    }
}
