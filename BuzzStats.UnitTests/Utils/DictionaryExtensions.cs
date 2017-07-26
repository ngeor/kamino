// --------------------------------------------------------------------------------
// <copyright file="DictionaryExtensions.cs" company="Nikolaos Georgiou">
//   Copyright (C) Nikolaos Georgiou 2010-2015
// </copyright>
// <author>Nikolaos Georgiou</author>
// * Date: 2015/10/16
// * Time: 06:06:24
// --------------------------------------------------------------------------------

using System.Collections.Generic;

namespace BuzzStats.UnitTests.Utils
{
    public static class DictionaryExtensions
    {
        public static V Ensure<K, V>(this Dictionary<K, V> dictionary, K key) where V : class, new()
        {
            V result;
            if (!dictionary.ContainsKey(key))
            {
                result = new V();
                dictionary.Add(key, result);
            }
            else
            {
                result = dictionary[key];
            }

            return result;
        }
    }
}