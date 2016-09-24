<%@ Page Language="C#" AutoEventWireup="true" CodeBehind="User.aspx.cs" Inherits="BuzzStats.Web.User" MasterPageFile="~/MasterPage.master" %>
<%@ OutputCache Duration="300" VaryByParam="*" %>
<%@ Import Namespace="BuzzStats.Common" %>
<asp:Content runat="server" ContentPlaceHolderID="cphBody">
    Recent activity of user <asp:Label ID="lblUser" runat="server"/>
    <h1>
        <%= Resources.RecentActivity %> <span id="progressRecentActivity" class="auto-update-progress"></span></h1>

    <ol id="divRecentActivity">
        <asp:Repeater ID="repRecentActivity" runat="server">
            <ItemTemplate>
                <li>
                    <a href="<%# "User.aspx?u=" + Server.UrlEncode((string) Eval("Who")) %>" class="ico who"><%# Eval("Who") %></a>
                    <span class="ico what" title="<%# Resources.ResourceManager.GetString("What" + Eval("What")) %>" data-what="<%# Eval("What") %>"><%# Eval("What") %></span>
                    <div>
                        <a class="title" href="<%# StoryUrl(Container.DataItem) %>"><%# Eval("StoryTitle") %></a>
                        <span class="age"><%# ((TimeSpan) Eval("Age")).ToAgoString() %></span>
                    </div>
                </li>
            </ItemTemplate>
        </asp:Repeater>
    </ol>
</asp:Content>
