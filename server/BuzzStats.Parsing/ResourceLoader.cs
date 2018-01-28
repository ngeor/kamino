using System;
using System.IO;
using System.Linq;
using System.Reflection;
using System.Text;

namespace BuzzStats.Parsing.UnitTests
{
    internal static class ResourceLoader
    {
        public static string Load(Assembly assembly, string id)
        {
            string[] resourceNames = assembly.GetManifestResourceNames();

            try
            {
                string resourceId = resourceNames.Single(n => n.EndsWith("." + id + ".txt"));
                Stream stream = assembly.GetManifestResourceStream(resourceId);
                byte[] bt = new byte[stream.Length];
                stream.Read(bt, 0, bt.Length);
                return Encoding.UTF8.GetString(bt);
            }
            catch (Exception ex)
            {
                throw new ArgumentOutOfRangeException(
                    "Resource " + id + " not found! Available options are: " + string.Join(", ", resourceNames),
                    ex);
            }
        }
    }
}