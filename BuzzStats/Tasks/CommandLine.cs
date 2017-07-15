using System;
using System.Collections.Generic;
using System.Linq;

namespace BuzzStats.Tasks
{
    // TODO: Use Options.cs or something else
    public sealed class CommandLine
    {
        private CommandLine(string command, IEnumerable<string> flags)
        {
            Command = command;
            Flags = new FlagsImpl(flags);
        }

        public static CommandLine Parse(string[] args)
        {
            return new CommandLine(
                args.Any() ? args[0] : string.Empty,
                (args ?? Enumerable.Empty<string>()).Where(s => s.StartsWith("-")).Select(s => s.Substring(1))
            );
        }

        public string Command { get; private set; }

        public IFlags Flags { get; private set; }


        public interface IFlags : IEnumerable<string>
        {
            bool this[string flag] { get; }
        }

        class FlagsImpl : IFlags
        {
            private readonly string[] _flags;

            public FlagsImpl(IEnumerable<string> flags)
            {
                _flags = flags.ToArray();
                Array.Sort(_flags);
            }

            public bool this[string flag]
            {
                get { return Array.BinarySearch(_flags, flag, StringComparer.InvariantCultureIgnoreCase) >= 0; }
            }

            public IEnumerator<string> GetEnumerator()
            {
                return _flags.OfType<string>().GetEnumerator();
            }

            System.Collections.IEnumerator System.Collections.IEnumerable.GetEnumerator()
            {
                return _flags.GetEnumerator();
            }
        }
    }
}