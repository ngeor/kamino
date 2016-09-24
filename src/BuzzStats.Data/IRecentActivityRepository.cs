//
//  IRecentActivityRepository.cs
//
//  Author:
//       ngeor
//
//  Copyright (c) 2014 ngeor

namespace BuzzStats.Data
{
    public interface IRecentActivityRepository
    {
        RecentActivity[] Get(RecentActivityRequest request);
    }
}
