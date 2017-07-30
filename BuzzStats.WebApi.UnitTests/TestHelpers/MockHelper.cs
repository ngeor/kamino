using System;
using System.Linq;
using System.Reflection;
using Moq;

namespace BuzzStats.WebApi.UnitTests.TestHelpers
{
    /// <summary>
    /// Helpers for Moq.
    /// </summary>
    public static class MockHelper
    {
        /// <summary>
        /// Creates and injects mocks in fields on the given test fixture.
        /// </summary>
        public static void InjectMocks(object testFixture, MockBehavior defaultMockBehavior = MockBehavior.Default)
        {
            var fieldInfos = testFixture.GetType().GetFields(BindingFlags.NonPublic | BindingFlags.Instance);
            var mockFieldInfos =
                fieldInfos.Where(f => f.FieldType.IsGenericType && f.FieldType.BaseType == typeof(Mock));
            foreach (var fieldInfo in mockFieldInfos)
            {
                // get constructor new Mock<T>(MockBehavior)
                var constructorInfo = fieldInfo.FieldType.GetConstructor(new[]
                {
                    typeof(MockBehavior)
                });

                // check what behavior to use
                var mockBehaviorAttribute = fieldInfo.GetCustomAttribute<MockBehaviorAttribute>();
                var mockBehavior = mockBehaviorAttribute?.Behavior ?? defaultMockBehavior;

                // inject field
                fieldInfo.SetValue(testFixture, constructorInfo.Invoke(new object[]
                {
                    mockBehavior
                }));
            }
        }

        /// <summary>
        /// Creates the unit under test using the fields of the given fixture for dependencies.
        /// </summary>
        public static T Create<T>(object testFixture)
        {
            var constructors = typeof(T).GetConstructors();
            if (constructors.Length != 1)
            {
                throw new NotSupportedException($"Type {typeof(T)} must have exactly one constructor");
            }

            var constructor = constructors[0];
            var parameterInfos = constructor.GetParameters();
            return (T)constructor.Invoke(parameterInfos.Select(p => ResolveParameter(testFixture, p)).ToArray());
        }

        private static object ResolveParameter(object testFixture, ParameterInfo parameterInfo)
        {
            var parameterType = parameterInfo.ParameterType;
            var fieldInfos = testFixture.GetType().GetFields(BindingFlags.NonPublic | BindingFlags.Instance);
            var mockFieldInfos = fieldInfos
                .Where(f => f.FieldType.IsGenericType && f.FieldType.BaseType == typeof(Mock)
                            && f.FieldType.GenericTypeArguments[0] == parameterType)
                .ToArray();

            if (mockFieldInfos.Length != 1)
            {
                throw new NotSupportedException($"Parameter {parameterInfo.Name} did not have exactly one matching field");
            }

            var mockInstance = (Mock)mockFieldInfos[0].GetValue(testFixture);
            return mockInstance?.Object;
        }
    }
}