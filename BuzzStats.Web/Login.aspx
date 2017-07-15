<%@ Page Language="C#" AutoEventWireup="true" CodeBehind="Login.aspx.cs" Inherits="BuzzStats.Web.Login" %>

<!DOCTYPE html>

<html xmlns="http://www.w3.org/1999/xhtml">
<head runat="server">
    <title></title>
</head>
<body>
<form id="form1" runat="server">
    <asp:Label ForeColor="Red" runat="server" Visible="false" ID="lblLoginFailed">Login failed!</asp:Label>
    <ol>
        <li>
            <asp:Label AssociatedControlID="txtUsername" runat="server" Text="Username"/>
            <asp:TextBox ID="txtUsername" runat="server"/>
            <asp:RequiredFieldValidator ControlToValidate="txtUsername" runat="server">*</asp:RequiredFieldValidator>
        </li>
        <li>
            <asp:Label AssociatedControlID="txtPassword" runat="server" Text="Username"/>
            <asp:TextBox ID="txtPassword" runat="server" TextMode="Password"/>
            <asp:RequiredFieldValidator ControlToValidate="txtPassword" runat="server">*</asp:RequiredFieldValidator>
        </li>
        <li>
            <asp:Button ID="btnSubmit" runat="server" OnClick="btnSubmit_Click" Text="Login"/>
        </li>
    </ol>
</form>
</body>
</html>
