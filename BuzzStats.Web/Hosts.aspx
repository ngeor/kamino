<%@ Page Title="Ιστότοποι" Language="C#" MasterPageFile="~/MasterPage.master" AutoEventWireup="true"
CodeBehind="Hosts.aspx.cs" Inherits="BuzzStats.Web.Hosts" EnableViewState="false" %>
<%@ OutputCache Duration="300" VaryByParam="*" %>
<asp:Content ID="Content1" runat="server" ContentPlaceHolderID="cpHead">
</asp:Content>
<asp:Content ID="Content2" ContentPlaceHolderID="cphBody" runat="server">
    <h1>
        Δημοφιλείς πηγές άρθρων <span id='progressPopularSites'></span></h1>
    <table id="tblPopularSites" class="number-table">
        <thead>
        <tr>
            <th>
                Site
            </th>
            <th>
                Σύνολο ψήφων
            </th>
            <th>
                Σύνολο άρθρων
            </th>
            <th>
                Σύνολο ψήφων / Σύνολο άρθρων
            </th>
        </tr>
        </thead>
        <tbody data-bind="foreach: popularStories">
        <tr>
            <td class="js-host" data-bind="text: Host"></td>
            <td data-bind="text: VoteCount"></td>
            <td data-bind="text: StoryCount"></td>
            <td data-bind="text: Math.round(100*VoteCount/StoryCount)/100"></td>
        </tr>
        </tbody>
    </table>
    <h1>
        Συχνότερες πηγές άρθρων <span id='progressFrequentSites'></span></h1>
    <table id="tblFrequentSites" class="number-table">
        <thead>
        <tr>
            <th>
                Site
            </th>
            <th>
                Σύνολο ψήφων
            </th>
            <th>
                Σύνολο άρθρων
            </th>
            <th>
                Σύνολο ψήφων / Σύνολο άρθρων
            </th>
        </tr>
        </thead>
        <tbody data-bind="foreach: frequentStories">
        <tr>
            <td data-bind="text: Host"></td>
            <td data-bind="text: VoteCount"></td>
            <td data-bind="text: StoryCount"></td>
            <td data-bind="text: Math.round(100 * VoteCount / StoryCount) / 100"></td>
        </tr>
        </tbody>
    </table>
    <h1>
        Αποτελεσματικότητα πηγών άρθρων
    </h1>
    <div>
        Με τουλάχιστον
        <input id="txtMinimumStories" type="number"/>
        άρθρα
        <input type="button" value="Ανανέωση" id="btnRefreshSitesScore"/>
        <span id='progressSitesScore'></span>
    </div>
    <table id="tblSitesScore" class="number-table">
        <thead>
        <tr>
            <th>
                Site
            </th>
            <th>
                Σύνολο ψήφων
            </th>
            <th>
                Σύνολο άρθρων
            </th>
            <th>
                Σύνολο ψήφων / Σύνολο άρθρων
            </th>
        </tr>
        </thead>
        <tbody data-bind="foreach: scoreStories">
        <tr>
            <td data-bind="text: Host"></td>
            <td data-bind="text: VoteCount"></td>
            <td data-bind="text: StoryCount"></td>
            <td data-bind="text: Math.round(100 * VoteCount / StoryCount) / 100"></td>
        </tr>
        </tbody>
    </table>
</asp:Content>
