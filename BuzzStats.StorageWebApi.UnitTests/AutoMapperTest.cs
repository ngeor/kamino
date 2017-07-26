using AutoMapper;
using BuzzStats.StorageWebApi.DTOs;
using BuzzStats.StorageWebApi.Entities;
using NUnit.Framework;
using StructureMap;

namespace BuzzStats.StorageWebApi.UnitTests
{
    [TestFixture]
    public class AutoMapperTest
    {
        private IMapper _mapper;
        
        [SetUp]
        public void ValidateAutoMapper()
        {
            IContainer container = new StructureMapContainerBuilder().Create();
            _mapper = container.GetInstance<IMapper>();
            _mapper.ConfigurationProvider.AssertConfigurationIsValid();
        }

        [Test]
        public void MapCommentEntityToCommentWithStory()
        {
            CommentEntity commentEntity = new CommentEntity
            {
                CommentId = 42,
                Story = new StoryEntity
                {
                    StoryId = 10
                }
            };
            
            CommentWithStory commentWithStory = _mapper.Map<CommentWithStory>(commentEntity);
            Assert.IsNotNull(commentWithStory);
            Assert.AreEqual(42, commentWithStory.CommentId);
            Assert.AreEqual(10, commentWithStory.StoryId);
        }
        
        [Test]
        public void MapCommentEntityToCommentWithStory_WithNullStory()
        {
            CommentEntity commentEntity = new CommentEntity
            {
                CommentId = 42
            };
            
            CommentWithStory commentWithStory = _mapper.Map<CommentWithStory>(commentEntity);
            Assert.IsNotNull(commentWithStory);
            Assert.AreEqual(42, commentWithStory.CommentId);
            Assert.AreEqual(0, commentWithStory.StoryId);
        }
    }
}