<%@ Page Language="C#" MasterPageFile="~/Admin/Admin.master" AutoEventWireup="true" CodeBehind="CacheManager.aspx.cs" Inherits="BuzzStats.Web.Admin.CacheManager"
EnableViewState="false" %>

<asp:Content ContentPlaceHolderID="cphBody" runat="server">
    <table>
        <asp:Repeater ID="repItems" runat="server">
            <ItemTemplate>
                <tr>
                    <td><%# Eval("Key") %></td>
                    <td><%# Eval("Value") %></td>
                </tr>
            </ItemTemplate>
        </asp:Repeater>
    </table>
    <asp:Button id="btnEmptyCache" runat="server" Text="Empty Cache" OnClick="btnEmptyCache_Click"/>
</asp:Content>
