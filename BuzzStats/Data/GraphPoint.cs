// --------------------------------------------------------------------------------
// <copyright file="GraphPoint.cs" company="Nikolaos Georgiou">
//      Copyright (C) Nikolaos Georgiou 2010-2013
// </copyright>
// <author>Nikolaos Georgiou</author>
// * Date: 2013/09/04
// * Time: 1:27 μμ
// --------------------------------------------------------------------------------

namespace BuzzStats.Data
{
    /// <summary>
    /// Graph point.
    /// </summary>
    /// <typeparam name="TX">
    /// </typeparam>
    /// <typeparam name="TY">
    /// </typeparam>
    public struct GraphPoint<TX, TY>
    {
        /// <summary>
        /// The x coordinate.
        /// </summary>
        public TX X;

        /// <summary>
        /// The y coordinate.
        /// </summary>
        public TY Y;
    }
}