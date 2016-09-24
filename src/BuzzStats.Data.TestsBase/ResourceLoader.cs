using System;
using System.IO;
using System.Linq;
using System.Text;
using NUnit.Framework;
using NGSoftware.Common.Collections;

namespace BuzzStats.Data.TestsBase
{
    static class ResourceLoader
    {
        public static string Load(string id)
        {
            var assembly = typeof (ResourceLoader).Assembly;
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
                    "Resource " + id + " not found! Available options are: " + resourceNames.ToArrayString(),
                    ex);
            }
        }

        public static string LoadExact(string resourceId)
        {
            Stream stream = typeof (ResourceLoader).Assembly.GetManifestResourceStream(resourceId);
            Assert.IsNotNull(stream, "Empty resource: " + resourceId);
            StreamReader sr = new StreamReader(stream);
            string result = sr.ReadToEnd();
            Assert.That(result, Is.Not.Null.Or.Empty, "Empty resource: " + resourceId);
            return result;
        }
    }
}
