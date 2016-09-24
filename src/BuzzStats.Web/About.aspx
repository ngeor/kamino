<%@ Page Language="C#" AutoEventWireup="true" CodeBehind="About.aspx.cs" Inherits="BuzzStats.Web.About"
MasterPageFile="~/MasterPage.master" Title="Για το BuzzStats" EnableViewState="false" %>

<%@ Register TagPrefix="my" TagName="StoryList" Src="~/UserControls/StoryList.ascx" %>
<%@ OutputCache Duration="300" VaryByParam="*" %>
<asp:Content ContentPlaceHolderID="cphBody" ID="cphBodyContent" runat="server">
    <h1>Για το BuzzStats</h1>
    <p>
        Το BuzzStats είναι μία εφαρμογή που συλλέγει δεδομένα από το <a href="http://buzz.reality-tape.com"
                                                                        target="_blank">
            Buzz
        </a> με σκοπό την εξαγωγή και παρουσίαση στατιστικών στοιχείων.
    </p>
    <h2>Μέθοδος</h2>
    <p>
        Τα δεδομένα συλλέγονται από την δημόσια διαθέσιμη html του Buzz. Οι πληροφορίες
        που περιέχονται στον html κώδικα των σελίδων του Buzz αναλύονται και τα στοιχεία
        αποθηκεύονται για την εξαγωγή στατιστικών στοιχείων. Η εύστοχη επιλογή των σελίδων
        που αναλύονται κάθε φορά καθορίζει και το πόσο γρήγορα αντιλαμβάνεται το BuzzStats
        τις δραστηριότητες των χρηστών του Buzz.
    </p>
    <h2>Τεχνολογίες</h2>
    <p>
        Αυτή τη στιγμή το BuzzStats χρησιμοποιεί ASP.NET 4.0, HTML 5, Fluent NHibernate
        και SQL Server Express. Στο παρελθόν έχει χρησιμοποιηθεί SQLite, ASP.NET 3.5/Mono,
        SilverLight.
    </p>
    <p>
        Crawler Service Status:
        <asp:Label runat="server" ID="lblServiceStatus"></asp:Label>.
        Uptime:
        <asp:Label runat="server" ID="lblUpTime"></asp:Label>
    </p>

    <table>
        <thead>
        <tr>
            <th></th>
            <th>Ελάχιστη τιμή</th>
            <th>Μέγιστη τιμή</th>
        </tr>
        </thead>
        <tbody>
        <tr>
            <td>Ημερομηνία ελέγχου</td>
            <td>
                <asp:Label runat="server" ID="lblMinLastCheckedAt"/>
            </td>
            <td>
                <asp:Label runat="server" ID="lblMaxLastCheckedAt"/>
            </td>
        </tr>
        <tr>
            <td>Πλήθος ελέγχων</td>
            <td>
                <asp:Label runat="server" ID="lblMinTotalChecks"/>
            </td>
            <td>
                <asp:Label runat="server" ID="lblMaxTotalChecks"/>
            </td>
        </tr>
        </tbody>
    </table>

    <div>
        <p>Oldest Checked Story</p>
        <my:StoryList runat="server" ID="oldestCheckedStories"/>
    </div>
</asp:Content>
