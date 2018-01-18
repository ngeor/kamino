using Moq;
using NHibernate;
using NHibernate.Criterion;

namespace BuzzStats.WebApi.UnitTests.Storage.TestHelpers
{
    public static class CriteriaHelpers
    {
        /// <summary>
        /// Sets up an expected criteria restriction.
        /// The restriction specifies that the given field must equal the given value.
        /// </summary>
        public static void SetupEq(this Mock<ICriteria> mockCriteria, string fieldName, object value)
        {
            // TODO create separate repo/project/NuGet package
            mockCriteria.Setup(c => c.Add(It.Is<ICriterion>(crit => crit.ToString() == Restrictions.Eq(fieldName, value).ToString())))
                .Returns(mockCriteria.Object);
        }

        public static void SetupOrderDesc(this Mock<ICriteria> mockCriteria, string fieldName)
        {
            mockCriteria.Setup(c => c.AddOrder(It.Is<Order>(o => o.ToString() == Order.Desc(fieldName).ToString())))
                .Returns(mockCriteria.Object);
        }
    }
}