// --------------------------------------------------------------------------------
// <copyright file="CommandLineAttribute.cs" company="Nikolaos Georgiou">
//   Copyright (C) Nikolaos Georgiou 2010-2015
// </copyright>
// <author>Nikolaos Georgiou</author>
// * Date: 2015/03/21
// * Time: 16:30:36
// --------------------------------------------------------------------------------

using System;
using System.Linq;

namespace BuzzStats.Tasks
{
    [AttributeUsage(AttributeTargets.Class, Inherited = false, AllowMultiple = false)]
    public sealed class CommandLineAttribute : Attribute
    {
        public CommandLineAttribute()
        {
        }

        public CommandLineAttribute(string flags, string help)
        {
            Flags = flags;
            Help = help;
        }

        public string Flags { get; private set; }

        public string Help { get; private set; }

        public static CommandLineAttribute GetAttribute(Type type)
        {
            if (type == null)
            {
                return null;
            }

            var attr = type.GetCustomAttributes(typeof(CommandLineAttribute), false);
            return attr != null ? attr.FirstOrDefault() as CommandLineAttribute : null;
        }
    }
}