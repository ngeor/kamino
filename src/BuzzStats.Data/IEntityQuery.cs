using System.Collections.Generic;

namespace BuzzStats.Data
{
    public interface IEntityQuery<T>
    {
        IEnumerable<T> AsEnumerable();
        IEnumerable<int> AsEnumerableOfIds();
        int Count();
    }
}
