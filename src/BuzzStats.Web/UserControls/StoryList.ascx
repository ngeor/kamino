<%@ Control Language="C#" AutoEventWireup="true" CodeBehind="StoryList.ascx.cs" Inherits="BuzzStats.Web.UserControls.StoryList" %>
<%@ Import Namespace="BuzzStats.Common" %>
<ul>
    <asp:Repeater ID="repStories" runat="server">
        <ItemTemplate>
            <li itemscope="itemscope">
                <a href="<%# StoryUrl((int) Eval("StoryId")) %>" target="_blank" itemprop="url"><%# Server.HtmlEncode((string) Eval("Title")) %></a>
                <span class="created-by" itemprop="createdBy" title="Προστέθηκε στο Buzz από το χρήστη <%# Eval("Username") %>"><%# Eval("Username") %></span>
                <span class="vote-count" itemprop="voteCount" title="Έχει <%# Eval("VoteCount") %> ψήφους"><%# Eval("VoteCount") %></span>
                <span class="created-at" itemprop="createdAt" title="Ημερομηνία που προστέθηκε στο Buzz"><%# ((DateTime) Eval("CreatedAt")).ToAgoString() %></span>
            </li>
        </ItemTemplate>
    </asp:Repeater>
</ul>
