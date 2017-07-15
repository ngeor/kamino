<%@ Page Title="Γραφήματα" Language="C#" MasterPageFile="~/MasterPage.master"
AutoEventWireup="true" CodeBehind="Charts.aspx.cs" Inherits="BuzzStats.Web.Charts"
EnableViewState="false" %>
<%@ Register TagName="DateRangePicker" TagPrefix="my" Src="~/UserControls/DateRangePicker.ascx" %>
<%@ OutputCache Duration="300" VaryByParam="*" %>
<asp:Content ID="Content1" ContentPlaceHolderID="cphBody" runat="server">
    <h1>
        Χρονική ανάλυση
    </h1>
    <div id="chartForm">
        Σε χρονικό διάστημα:
        <my:DateRangePicker runat="server" ID="dateRangePicker" CssClass="date-range-picker"/>
        <label for="selIntervalLength">ανά</label>
        <select id="selIntervalLength" name="selIntervalLength">
            <option value="">---</option>
            <option value="Day" selected="selected">ημέρα</option>
            <option value="Week">εβδομάδα</option>
            <option value="Month">μήνα</option>
            <option value="Year">έτος</option>
        </select>
        <asp:Button ID="btnRefreshCharts" runat="server" ClientIDMode="Static" Text="Ανανέωση" CausesValidation="false"/>
        <img src="img/progress.gif" alt="Loading..." style="display: none"/>
    </div>
    <noscript>
        Τα γραφήματα εμφανίζονται μόνο σε browser που υποστηρίζει JavaScript.
    </noscript>
    <div id="chartContainer" style="width: 800px; height: 400px">
    </div>
</asp:Content>
