<%@ Control Language="C#" AutoEventWireup="true" CodeBehind="StorySearch.ascx.cs" Inherits="BuzzStats.Web.UserControls.StorySearch" %>
<div>
    <p>Αναζήτηση</p>
    <label>Τίτλος</label>
    <asp:TextBox ID="txtTitle" runat="server"/>
    <asp:Button ID="btnSearch" runat="server" Text="Αναζήτηση" OnClick="btnSearch_Click"/>
</div>
