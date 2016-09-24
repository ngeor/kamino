//
//  IPersister.cs
//
//  Author:
//       ngeor
//
//  Copyright (c) 2014 ngeor

using BuzzStats.Parsing;

namespace BuzzStats.Persister
{
    public interface IPersister
    {
        PersisterResult MarkAsUnmodified(int storyId);
        PersisterResult Save(Story story);
    }
}
