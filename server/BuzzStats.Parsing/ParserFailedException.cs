using System;

namespace BuzzStats.Parsing
{
    [Serializable]
    public class ParserFailedException : Exception
    {
        public ParserFailedException() { }
        public ParserFailedException(string message) : base(message) { }
        public ParserFailedException(string message, Exception inner) : base(message, inner) { }
        protected ParserFailedException(
          System.Runtime.Serialization.SerializationInfo info,
          System.Runtime.Serialization.StreamingContext context) : base(info, context) { }
    }
}