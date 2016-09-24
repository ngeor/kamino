using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.UI;

namespace BuzzStats.Web.Admin
{
    public partial class CacheManager : Page
    {
        protected void Page_Load(object sender, EventArgs e)
        {
            if (!IsPostBack)
            {
                repItems.DataSource = new CacheEnumerable().OrderBy(kv => kv.Key);
                repItems.DataBind();
            }
        }

        protected void btnEmptyCache_Click(object sender, EventArgs e)
        {
            string[] keys = (new CacheEnumerable()).Select(kv => kv.Key).ToArray();
            foreach (string key in keys)
            {
                HttpRuntime.Cache.Remove(key);
            }
        }
    }

    class CacheEnumerable : IEnumerable<KeyValuePair<string, string>>
    {
        public IEnumerator<KeyValuePair<string, string>> GetEnumerator()
        {
            return new CacheEnumerator();
        }

        System.Collections.IEnumerator System.Collections.IEnumerable.GetEnumerator()
        {
            return GetEnumerator();
        }
    }

    class CacheEnumerator : IEnumerator<KeyValuePair<string, string>>
    {
        private System.Collections.IDictionaryEnumerator _backend;

        private System.Collections.IDictionaryEnumerator Backend
        {
            get { return _backend ?? (_backend = HttpRuntime.Cache.GetEnumerator()); }
        }

        private KeyValuePair<string, string> Convert(object cacheItem)
        {
            System.Collections.DictionaryEntry de = (System.Collections.DictionaryEntry) cacheItem;
            return new KeyValuePair<string, string>((string) de.Key, de.Value == null ? "null" : de.Value.GetType().Name);
        }

        public KeyValuePair<string, string> Current
        {
            get { return Convert(Backend.Current); }
        }

        public void Dispose()
        {
            if (_backend != null && _backend is IDisposable)
            {
                ((IDisposable) _backend).Dispose();
            }
        }

        object System.Collections.IEnumerator.Current
        {
            get { return Current; }
        }

        public bool MoveNext()
        {
            return Backend.MoveNext();
        }

        public void Reset()
        {
            Backend.Reset();
        }
    }
}
