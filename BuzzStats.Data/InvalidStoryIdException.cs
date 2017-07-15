// --------------------------------------------------------------------------------
// <copyright file="InvalidStoryIdException.cs" company="Nikolaos Georgiou">
//      Copyright (C) Nikolaos Georgiou 2010-2013
// </copyright>
// <author>Nikolaos Georgiou</author>
// * Date: 2013/10/04
// * Time: 10:03 μμ
// --------------------------------------------------------------------------------

using System;

namespace BuzzStats.Data
{
    public class InvalidStoryIdException : Exception
    {
        public InvalidStoryIdException()
        {
        }

        public InvalidStoryIdException(string message, Exception inner) : base(message, inner)
        {
        }
    }
}