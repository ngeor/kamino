using System;
using System.Globalization;
using System.Web.UI;
using NGSoftware.Common;

namespace BuzzStats.Web.UserControls
{
    public partial class DateRangePicker : UserControl
    {
        public const string DateFormat = "yyyy-MM-dd";

        public string CssClass
        {
            get { return container.CssClass; }
            set { container.CssClass = value; }
        }

        public DateRange Value
        {
            get { return DateRange.Create(Parse(txtFrom.Text), Parse(txtTo.Text)); }
            set
            {
                txtFrom.Text = Format(value.StartDate);
                txtTo.Text = Format(value.StopDate);
            }
        }

        private static DateTime? Parse(string text)
        {
            return string.IsNullOrWhiteSpace(text)
                ? (DateTime?) null
                : DateTime.ParseExact(text, DateFormat, CultureInfo.InvariantCulture);
        }

        private static string Format(DateTime? date)
        {
            return date.HasValue ? date.Value.ToString(DateFormat) : string.Empty;
        }
    }
}