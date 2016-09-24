using System;
using Moq;
using NUnit.Framework;
using BuzzStats.Data;
using BuzzStats.Parsing;
using BuzzStats.Persister;

namespace BuzzStats.Tests.Persister
{
    [TestFixture]
    public class TransactionalPersisterTest
    {
        enum State
        {
            Initial,
            BeginTransaction,
            SetDbSession,
            Save,
            Commit,
            ClearDbSession,
            Dispose
        }

        [Test]
        public void TestCommit()
        {
            State state = State.Initial;

            Mock<IDbSession> mockDbSession = new Mock<IDbSession>(MockBehavior.Strict);
            mockDbSession.Setup(x => x.BeginTransaction())
                .Callback(() =>
                {
                    Assert.AreEqual(State.Initial, state);
                    state = State.BeginTransaction;
                });

            mockDbSession.Setup(x => x.Commit())
                .Callback(() =>
                {
                    Assert.AreEqual(State.ClearDbSession, state);
                    state = State.Commit;
                });

            mockDbSession.Setup(x => x.Dispose())
                .Callback(() =>
                {
                    Assert.AreEqual(State.Commit, state);
                    state = State.Dispose;
                });

            Story story = Mock.Of<Story>();
            PersisterResult expected = new PersisterResult(Mock.Of<StoryData>(), UpdateResult.Created);

            Mock<IDbPersister> mockBackend = new Mock<IDbPersister>(MockBehavior.Strict);
            mockBackend
                .SetupSet(x => x.DbSession = mockDbSession.Object)
                .Callback(() =>
                {
                    Assert.AreEqual(State.BeginTransaction, state);
                    state = State.SetDbSession;
                });

            mockBackend
                .SetupSet(x => x.DbSession = null)
                .Callback(() =>
                {
                    Assert.AreEqual(State.Save, state);
                    state = State.ClearDbSession;
                });

            mockBackend
                .Setup(x => x.Save(story))
                .Returns(expected)
                .Callback(() =>
                {
                    Assert.AreEqual(State.SetDbSession, state);
                    state = State.Save;
                });

            TransactionalPersister persister = new TransactionalPersister(
                Mock.Of<IDbContext>(c => c.OpenSession() == mockDbSession.Object),
                mockBackend.Object);
            PersisterResult result = persister.Save(story);
            Assert.AreEqual(expected, result);
            mockDbSession.VerifyAll();
            Assert.AreEqual(State.Dispose, state);
        }

        [Test]
        public void TransactionIsRolledbackOnExceptionDuringSave()
        {
            Mock<IDbSession> mockDbSession = new Mock<IDbSession>(MockBehavior.Strict);
            mockDbSession.Setup(x => x.BeginTransaction());
            mockDbSession.Setup(x => x.Rollback());
            mockDbSession.Setup(x => x.Dispose());

            Story story = Mock.Of<Story>();

            Mock<IDbPersister> mockBackend = new Mock<IDbPersister>();
            mockBackend
                .Setup(x => x.Save(story))
                .Throws<InvalidOperationException>();

            TransactionalPersister persister = new TransactionalPersister(
                Mock.Of<IDbContext>(c => c.OpenSession() == mockDbSession.Object),
                mockBackend.Object);
            Assert.Throws<InvalidOperationException>(() => persister.Save(story));

            mockDbSession.VerifyAll();
        }
    }
}
