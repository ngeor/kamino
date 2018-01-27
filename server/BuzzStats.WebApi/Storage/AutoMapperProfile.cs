using AutoMapper;
using BuzzStats.DTOs;
using BuzzStats.WebApi.DTOs;
using BuzzStats.WebApi.Storage.Entities;

namespace BuzzStats.WebApi.Storage
{
    public class AutoMapperProfile : Profile
    {
        public AutoMapperProfile()
        {
            CreateMap<CommentEntity, CommentWithStory>()
                .ForMember(d => d.StoryId, opt => opt.MapFrom(s => s.Story.StoryId))
                .ForMember(d => d.Title, opt => opt.MapFrom(s => s.Story.Title));

            CreateMap<Story, StoryEntity>()
                .ForMember(d => d.Id, opt => opt.Ignore());

            CreateMap<CommentWithStory, RecentComment>();

            CreateMap<RecentActivityEntity, RecentActivity>()
                .ForMember(d => d.StoryId, opt => opt.MapFrom(s => s.Story.StoryId))
                .ForMember(d => d.Title, opt => opt.MapFrom(s => s.Story.Title));
        }
    }
}