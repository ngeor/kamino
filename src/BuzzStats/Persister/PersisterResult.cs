//
//  PersisterResult.cs
//
//  Author:
//       ngeor
//
//  Copyright (c) 2014 ngeor

using System;
using BuzzStats.Data;

namespace BuzzStats.Persister
{
    public struct PersisterResult : IEquatable<PersisterResult>
    {
        private UpdateResult _changes;
        private StoryData _story;

        public PersisterResult(StoryData story, UpdateResult updateResult)
        {
            _story = story;
            _changes = updateResult;
        }

        public UpdateResult Changes { get { return _changes; } }

        public StoryData Story { get { return _story; } }

        public override string ToString()
        {
            return string.Format("[PersisterResult: Story={0}, Changes={1}]", Story, Changes);
        }

        public override int GetHashCode()
        {
            unchecked
            {
                int result = Changes.GetHashCode();
                result = result*11 + (Story != null ? Story.GetHashCode() : 0);
                return result;
            }
        }

        public override bool Equals(object obj)
        {
            if (obj == null)
            {
                return false;
            }

            if (obj.GetType() != GetType())
            {
                return false;
            }

            return Equals((PersisterResult) obj);
        }

        public bool Equals(PersisterResult that)
        {
            return Changes == that.Changes
                && Equals(Story, that.Story);
        }

        public static bool operator ==(PersisterResult left, PersisterResult right)
        {
            return left.Equals(right);
        }

        public static bool operator !=(PersisterResult left, PersisterResult right)
        {
            return !left.Equals(right);
        }
    }
}
