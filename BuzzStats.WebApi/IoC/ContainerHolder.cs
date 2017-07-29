using System;
using StructureMap;

namespace BuzzStats.WebApi.IoC
{
    public static class ContainerHolder
    {
        private static readonly Lazy<IContainer> ContainerLazy = new Lazy<IContainer>(
            () => new StructureMapContainerBuilder().Create());

        public static IContainer Container => ContainerLazy.Value;
    }
}