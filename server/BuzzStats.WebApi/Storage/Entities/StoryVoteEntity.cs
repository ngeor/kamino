namespace BuzzStats.WebApi.Storage.Entities
{
    public class StoryVoteEntity
    {
        public virtual int Id { get; set; }

        public virtual StoryEntity Story { get; set; }

        public virtual string Username { get; set; }

        public override string ToString()
        {
            return string.Format(
                "{0} Id={1} Username={2} Story=[{3}]",
                GetType().Name,
                Id,
                Username,
                Story);
        }
    }
}