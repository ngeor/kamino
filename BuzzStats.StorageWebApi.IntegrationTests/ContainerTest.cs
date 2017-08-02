using BuzzStats.WebApi.IoC;
using NUnit.Framework;
using StructureMap;

namespace BuzzStats.StorageWebApi.IntegrationTests
{
    [TestFixture]
    public class ContainerTest
    {
        [Test]
        public void ValidateConfiguration()
        {
            IContainer container = new StructureMapContainerBuilder().Create();
            container.AssertConfigurationIsValid();
        }
    }
}
