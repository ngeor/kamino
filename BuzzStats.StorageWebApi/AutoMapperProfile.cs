using AutoMapper;
using BuzzStats.StorageWebApi.DTOs;
using BuzzStats.StorageWebApi.Entities;

namespace BuzzStats.StorageWebApi
{
    public class AutoMapperProfile : Profile
    {
        public AutoMapperProfile()
        {
            var map = CreateMap<CommentEntity, CommentWithStory>();
            map.ForMember(d => d.StoryId, opt => opt.MapFrom(s => s.Story.StoryId));
        }
    }
}