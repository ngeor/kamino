// --------------------------------------------------------------------------------
// <copyright file="CacheFactory.cs" company="Nikolaos Georgiou">
//      Copyright (C) Nikolaos Georgiou 2010-2013
// </copyright>
// <author>Nikolaos Georgiou</author>
// * Date: 2013/09/30
// * Time: 8:26 πμ
// --------------------------------------------------------------------------------

using NGSoftware.Common.Cache;
using NGSoftware.Common.Factories;

namespace BuzzStats.Boot.Web
{
    public class CacheFactory : IFactory<ICache>
    {
        public ICache Create()
        {
            return new NullCache();
        }
    }
}