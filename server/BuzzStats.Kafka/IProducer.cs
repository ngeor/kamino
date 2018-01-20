using System.Threading.Tasks;

namespace BuzzStats.Kafka
{
    public interface IProducer
    {
        Task Post(string message);
    }
}
