<%@ Control Language="C#" AutoEventWireup="true" CodeBehind="DateRangePicker.ascx.cs" Inherits="BuzzStats.Web.UserControls.DateRangePicker" %>
<asp:Panel runat="server" ID="container">
    <asp:Label ID="lblFrom" runat="server" AssociatedControlID="txtFrom" Text="από"/>

    <asp:TextBox runat="server" ID="txtFrom" CssClass="js-date-range-picker"/>
    <asp:CompareValidator ID="cvFrom" runat="server" ControlToValidate="txtFrom" Operator="DataTypeCheck"
                          EnableClientScript="false"
                          Type="Date" Text="*"/>

    <asp:Label ID="lblTo" runat="server" AssociatedControlID="txtTo" Text="έως"/>
    <asp:TextBox runat="server" ID="txtTo" CssClass="js-date-range-picker"/>
    <asp:CompareValidator ID="cvTo" runat="server" ControlToValidate="txtTo" Operator="DataTypeCheck"
                          EnableClientScript="false"
                          Type="Date" Text="*"/>
    <asp:CompareValidator ID="cvFromLessThanTo" runat="server" ControlToValidate="txtTo"
                          EnableClientScript="false"
                          ControlToCompare="txtFrom" Text="*" Operator="GreaterThanEqual"/>
</asp:Panel>
