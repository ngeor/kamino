using System;
using System.Collections.Generic;
using System.Linq;
using System.Web.UI;
using System.Web.UI.WebControls;
using NGSoftware.Common;
using BuzzStats.Data;

namespace BuzzStats.Web
{
    class Edge<TValue, TWeight>
    {
        public Node<TValue, TWeight> From { get; set; }
        public Node<TValue, TWeight> To { get; set; }
        public TWeight Weight { get; set; }

        public bool MatchDirected(TValue a, TValue b)
        {
            return From.Value.Equals(a) && To.Value.Equals(b);
        }

        public bool MatchNonDirected(TValue a, TValue b)
        {
            return MatchDirected(a, b) || MatchDirected(b, a);
        }

        public bool MatchAny(TValue value)
        {
            return From.Value.Equals(value) || To.Value.Equals(value);
        }
    }

    class EdgeView<TValue, TWeight>
    {
        public TValue Value { get; set; }
        public TWeight Weight { get; set; }
    }

    class Node<TValue, TWeight>
    {
        private readonly NonDirectedGraph<TValue, TWeight> _owner;

        public Node(NonDirectedGraph<TValue, TWeight> owner, TValue value)
        {
            _owner = owner;
            Value = value;
        }

        public TValue Value { get; }

        public IEnumerable<EdgeView<TValue, TWeight>> GetEdges()
        {
            return _owner.GetEdges(Value)
                .Select(e => new EdgeView<TValue, TWeight>
                {
                    Weight = e.Weight,
                    Value = e.From.Value.Equals(Value) ? e.To.Value : e.From.Value
                });
        }
    }

    class NonDirectedGraph<TValue, TWeight>
    {
        private readonly List<Node<TValue, TWeight>> nodes = new List<Node<TValue, TWeight>>();
        private readonly List<Edge<TValue, TWeight>> edges = new List<Edge<TValue, TWeight>>();

        public IEnumerable<Node<TValue, TWeight>> GetNodes()
        {
            return nodes.ToArray();
        }

        public Edge<TValue, TWeight> GetEdge(TValue left, TValue right)
        {
            return edges.SingleOrDefault(e => e.MatchNonDirected(left, right));
        }

        public IEnumerable<Edge<TValue, TWeight>> GetEdges(TValue value)
        {
            return edges.Where(e => e.MatchAny(value));
        }

        public IEnumerable<Edge<TValue, TWeight>> GetEdges()
        {
            return edges.ToArray();
        }

        public void AddEdge(TValue left, TValue right, TWeight weight)
        {
            if (GetEdge(left, right) != null)
            {
                throw new InvalidOperationException("Edge already exists");
            }

            Edge<TValue, TWeight> edge = new Edge<TValue, TWeight>
            {
                From = EnsureNode(left),
                To = EnsureNode(right),
                Weight = weight
            };

            edges.Add(edge);
        }

        public Node<TValue, TWeight> EnsureNode(TValue value)
        {
            Node<TValue, TWeight> result = nodes.SingleOrDefault(n => n.Value.Equals(value));
            if (result == null)
            {
                nodes.Add(result = new Node<TValue, TWeight>(this, value));
            }

            return result;
        }

        public void AddFriends(TValue[] friends, Func<TWeight, TWeight> increaseWeight, TWeight initialWeight)
        {
            for (int i = 0; i < friends.Length; i++)
            {
                EnsureNode(friends[i]);

                for (int j = i + 1; j < friends.Length; j++)
                {
                    EnsureNode(friends[j]);

                    var edge = GetEdge(friends[i], friends[j]);
                    if (edge != null)
                    {
                        edge.Weight = increaseWeight(edge.Weight);
                    }
                    else
                    {
                        AddEdge(friends[i], friends[j], initialWeight);
                    }
                }
            }
        }
    }

    public partial class Users : Page
    {
        protected void Page_Load(object sender, EventArgs e)
        {
            if (!IsPostBack)
            {
                // initialize from-to datetime textboxes with default values
                dateRangePicker.Value =
                    DateRange.Create(DateTime.UtcNow.Subtract(TimeSpan.FromDays(7)), DateTime.UtcNow);
            }
        }

        protected void gvUsers_RowDataBound(object sender, GridViewRowEventArgs e)
        {
            UserStats userStats = e.Row.DataItem as UserStats;
            if (userStats != null && string.IsNullOrEmpty(userStats.Username))
            {
                e.Row.CssClass = "average";
            }
        }

        protected void repFriends_ItemDataBound(object sender, RepeaterItemEventArgs e)
        {
            var node = e.Item.DataItem as Node<string, int>;
            if (node != null)
            {
                Repeater repLinks = e.Item.FindControl("repLinks") as Repeater;
                if (repLinks != null)
                {
                    repLinks.DataSource = node.GetEdges();
                    repLinks.DataBind();
                }
            }
        }

        protected void gvUsers_RowCommand(object sender, GridViewCommandEventArgs e)
        {
        }

        protected void odsUserStats_Selecting(object sender, ObjectDataSourceSelectingEventArgs e)
        {
            try
            {
                DateRange dateRange = dateRangePicker.Value;
                e.InputParameters["startDate"] = dateRange.StartDate;
                e.InputParameters["endDate"] = dateRange.StopDate;
            }
            catch (FormatException)
            {
                e.Cancel = true;
            }
        }

        protected void dateRangePicker_Changed(object sender, EventArgs e)
        {
            gvUsers.DataBind();
        }
    }
}