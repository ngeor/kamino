// --------------------------------------------------------------------------------
// <copyright file="IApp.cs" company="Nikolaos Georgiou">
//   Copyright (C) Nikolaos Georgiou 2010-2015
// </copyright>
// <author>Nikolaos Georgiou</author>
// * Date: 2015/03/21
// * Time: 16:30:33
// --------------------------------------------------------------------------------

using System;
using System.Collections.Generic;
using System.Linq;
using System.Reflection;

namespace BuzzStats.Tasks
{
    public interface IApp
    {
        void Run(string[] args);
    }

    [AttributeUsage(AttributeTargets.Assembly)]
    public class AppContainerAttribute : Attribute
    {
    }
}
