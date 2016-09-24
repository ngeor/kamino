using System.Runtime.Serialization;

namespace BuzzStats.Data
{
    /// <summary>
    /// Contains the parameters of the Recent Activity request.
    /// </summary>
    /// <seealso cref="IApiService.GetRecentActivity"/>.
    [DataContract]
    public class RecentActivityRequest
    {
        /// <summary>
        /// Gets or sets the maximum number of rows to return.
        /// </summary>
        [DataMember]
        public int MaxCount { get; set; }

        /// <summary>
        /// Gets or sets the user to fetch recent activity for.
        /// </summary>
        [DataMember]
        public string Username { get; set; }

        public override string ToString()
        {
            return string.Format("{0} MaxCount={1} Username={2}", GetType().Name, MaxCount, Username);
        }
    }
}
