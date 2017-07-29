using AutoMapper;
using BuzzStats.CrawlerService.DTOs;
using BuzzStats.StorageWebApi.Entities;
using BuzzStats.WebApi.DTOs;

namespace BuzzStats.StorageWebApi
{
    public class AutoMapperProfile : Profile
    {
        public AutoMapperProfile()
        {
            CreateMap<CommentEntity, CommentWithStory>()
                .ForMember(d => d.StoryId, opt => opt.MapFrom(s => s.Story.StoryId));

            CreateMap<Story, StoryEntity>()
                .ForMember(d => d.Id, opt => opt.Ignore());
        }
    }
}