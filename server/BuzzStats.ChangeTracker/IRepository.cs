using BuzzStats.Parsing.DTOs;
using System.Threading.Tasks;

namespace BuzzStats.ChangeTracker
{
    public interface IRepository
    {
        Task<Story> Load(int storyId);
        Task Save(Story story);
    }
}
