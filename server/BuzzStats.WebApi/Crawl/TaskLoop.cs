using System;
using System.Threading.Tasks;

namespace BuzzStats.WebApi.Crawl
{
    public class TaskLoop
    {
        private readonly Func<Task> _action;
        public TaskLoop(Func<Task> action)
        {
            _action = action;
        }
        
        public async Task RunForEver()
        {
            while (true)
            {
                await _action();
                await Task.Delay(TimeSpan.FromSeconds(1));
            }
        }

        public static void RunForEver(Func<Task> action)
        {
            TaskLoop taskLoop = new TaskLoop(action);
            Task.Run(() => taskLoop.RunForEver());
        }
    }
}