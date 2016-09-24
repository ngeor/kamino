<%@ Page Title="Άρθρα" Language="C#" MasterPageFile="~/MasterPage.master" AutoEventWireup="true"
CodeBehind="Stories.aspx.cs" Inherits="BuzzStats.Web.Stories" EnableViewState="false" %>
<%@ Register TagPrefix="my" TagName="StoryList" Src="~/UserControls/StoryList.ascx" %>
<%@ Register TagPrefix="my" TagName="StorySearch" Src="~/UserControls/StorySearch.ascx" %>
<%@ Register TagPrefix="my" Assembly="BuzzStats" Namespace="BuzzStats.Web.Mvp" %>
<%@ OutputCache Duration="300" VaryByParam="*" %>
<asp:Content ContentPlaceHolderID="cphBody" runat="server">
    <h1>Άρθρα</h1>

    <p>
        Συνολικά στο BuzzStats υπάρχουν
        <my:StoryCountControl runat="server"/>
        άρθρα και
        <asp:Literal ID="litCommentCount" runat="server"/>
        σχόλια
    </p>

    <my:StorySearch ID="storySearch" runat="server" Visible="false"/>

    <section class="story-list">
        <h2>Άρθρα με πρόσφατη δραστηριότητα</h2>
        <my:StoryList runat="server" ID="recentlyModifiedStories"/>
    </section>

    <section class="story-list">
        <h2>Πιο πρόσφατα άρθρα</h2>
        <my:StoryList runat="server" ID="recentlyCreatedStories"/>
    </section>

    <section class="story-list last">
        <h2>Άρθρα με πρόσφατα σχόλια</h2>
        <my:StoryList runat="server" ID="recentlyCommentedStories"/>
    </section>

</asp:Content>
