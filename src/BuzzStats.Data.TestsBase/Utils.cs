using System.Data.Common;

namespace BuzzStats.Data.TestsBase
{
    public static class Utils
    {
        public static string LoadResource(string filename)
        {
            string resourceId = "BuzzStats.Data.TestsBase.TestData." + filename;
            return ResourceLoader.LoadExact(resourceId);
        }

        public static void PrepareDatabase(this DbConnection connection, string resourceFilename)
        {
            connection.ExecuteNonQuery(LoadResource(resourceFilename));
        }
    }
}