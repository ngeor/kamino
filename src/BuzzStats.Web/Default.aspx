<%@ Page Language="C#" Inherits="BuzzStats.Web.Default" MasterPageFile="~/MasterPage.master" CodeBehind="~/Default.aspx.cs"
Title="" EnableViewState="false" %>

<%@ OutputCache Duration="300" VaryByParam="*" %>
<%@ Import Namespace="BuzzStats.Common" %>
<%@ Import Namespace="BuzzStats.Data" %>

<asp:Content runat="server" ContentPlaceHolderID="cpHead">
</asp:Content>
<asp:Content runat="server" ContentPlaceHolderID="cphBody">
    <div class="homepage">
        <section id="sectionRecentComments">
            <h1>Πρόσφατα Σχόλια<span id="progressRecentComments" class="auto-update-progress"></span></h1>

            <div class="recent-comments" data-bind="foreach: recentComments">
                <asp:Repeater ID="repRecentComments" runat="server" OnItemDataBound="repRecentComments_ItemDataBound">
                    <ItemTemplate>
                        <div class="per-story">
                            <h3 title="<%# Eval("Title") %>" data-bind="text: Title, attr: { title: Title }"><%# Eval("Title") %></h3>
                            <table>
                                <thead>
                                <tr>
                                    <th>Χρήστης</th>
                                    <th>Ημερομηνία</th>
                                    <th>
                                        <img src="img/thumbsup.png" alt="Χεράκια"/>
                                    </th>
                                </tr>
                                </thead>
                                <tbody data-bind="foreach: Comments">
                                <asp:Repeater ID="repComments" runat="server">
                                    <ItemTemplate>
                                        <tr>
                                            <td data-bind="text: Username"><%# Eval("Username") %></td>
                                            <td>
                                                <a data-bind="text: formatAge(Age), attr: { href: 'http://buzz.reality-tape.com/story.php?id=' + $parent.StoryId + '#wholecomment' + CommentId }" href="<%# Eval("StoryUrl") %>" target="_blank"><%# ((TimeSpan) Eval("Age")).ToAgoString() %></a>
                                            </td>
                                            <td data-bind="text: VotesUp"><%# Eval("VotesUp") %></td>
                                        </tr>
                                    </ItemTemplate>
                                </asp:Repeater>
                                </tbody>
                            </table>
                        </div>
                    </ItemTemplate>
                </asp:Repeater>
            </div>
        </section>

        <section id="sectionRecentActivity">
            <h1>
                <%= Resources.RecentActivity %> <span id="progressRecentActivity" class="auto-update-progress"></span></h1>

            <ol class="recent-activity">
                <asp:Repeater ID="repRecentActivity" runat="server">
                    <ItemTemplate>
                        <li>
                            <a href="<%# "User.aspx?u=" + Server.UrlEncode((string) Eval("Who")) %>" class="ico who"><%# Eval("Who") %></a>
                            <span class="ico what" title="<%# MyResources.What(Eval("What")) %>" data-what="<%# Eval("What") %>"><%# Eval("What") %></span>
                            <div>
                                <a class="title" href="<%# Eval("StoryUrl") %>"><%# Eval("StoryTitle") %></a>
                                <span class="age" title="<%# DiffBetweenCreatedAndDetected((RecentActivity) Container.DataItem) %>">
	                            <%# ((TimeSpan) Eval("Age")).ToAgoString() %>
                            </span>
                            </div>
                        </li>
                    </ItemTemplate>
                </asp:Repeater>
            </ol>
        </section>

        <section id="sectionRecentPopularComments">
            <h1>Πρόσφατα δημοφιλή σχόλια</h1>
            <table class="simple">
                <thead>
                <tr>
                    <th><%= Resources.VoteCount %></th>
                    <th><%= Resources.Date %></th>
                    <th><%= Resources.Username %></th>
                    <th><%= Resources.Story %></th>
                </tr>
                </thead>
                <tbody>
                <asp:Repeater ID="repRecentPopularComments" runat="server">
                    <ItemTemplate>
                        <tr>
                            <td><%# Eval("VotesUp") %></td>
                            <td><%# ((TimeSpan) Eval("Age")).ToAgoString() %></td>
                            <td><%# Eval("Username") %></td>
                            <td>
                                <a href="<%# Eval("StoryUrl") %>"><%# Eval("Story.Title") %></a>
                            </td>
                        </tr>
                    </ItemTemplate>
                </asp:Repeater>
                </tbody>
            </table>
        </section>
    </div>
</asp:Content>
