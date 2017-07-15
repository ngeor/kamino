<%@ Page Title="" Language="C#" MasterPageFile="~/Admin/Admin.master" AutoEventWireup="true" CodeBehind="PollHistory.aspx.cs" Inherits="BuzzStats.Web.Admin.PollHistory" %>
<asp:Content ID="Content1" ContentPlaceHolderID="cphHead" runat="server">
</asp:Content>
<asp:Content ID="Content2" ContentPlaceHolderID="cphBody" runat="server">
    <div><%: string.Format(Resources.N_Records, TotalCount) %></div>
    <asp:GridView runat="server" ID="gvHistory" AutoGenerateColumns="True">

    </asp:GridView>
</asp:Content>
<asp:Content ID="Content3" ContentPlaceHolderID="cphScripts" runat="server">
</asp:Content>
