<%@ Page Title="Χρήστες" Language="C#" MasterPageFile="~/MasterPage.master" AutoEventWireup="true"
CodeBehind="Users.aspx.cs" Inherits="BuzzStats.Web.Users" %>

<%@ Register TagName="DateRangePicker" TagPrefix="my" Src="~/UserControls/DateRangePicker.ascx" %>
<%@ OutputCache Duration="300" VaryByParam="*" %>
<asp:Content ContentPlaceHolderID="cphBody" runat="server">
    <asp:ObjectDataSource ID="odsUserStats" runat="server" SelectMethod="Select"
                          OnSelecting="odsUserStats_Selecting"
                          TypeName="BuzzStats.Web.DataSources.UserStatsDataSource" SortParameterName="sortExpression">
        <SelectParameters>
            <asp:Parameter Name="startDate" Type="DateTime"/>
            <asp:Parameter Name="endDate" Type="DateTime"/>
        </SelectParameters>
    </asp:ObjectDataSource>
    <div class="clearfix">
        <h1 class="left">Κατάταξη χρηστών</h1>
        <div class="right">
            <my:DateRangePicker runat="server" ID="dateRangePicker" CssClass="date-range-picker"/>
            <asp:Button ID="btnRefresh" runat="server" Text="Ανανέωση" CausesValidation="false" OnClick="dateRangePicker_Changed"/>
        </div>
    </div>

    <asp:GridView ClientIDMode="Static" ID="gvUsers" runat="server" AutoGenerateColumns="false"
                  OnRowCommand="gvUsers_RowCommand" DataSourceID="odsUserStats" AllowSorting="true"
                  OnRowDataBound="gvUsers_RowDataBound">
        <Columns>
            <asp:TemplateField HeaderText="Χρήστης" SortExpression="Username">
                <ItemTemplate>
                    <%# string.IsNullOrEmpty(Convert.ToString(Eval("Username"))) ? "Μέσος Όρος" : Eval("Username") %>
                </ItemTemplate>
            </asp:TemplateField>
            <asp:BoundField SortExpression="StoryCount" DataField="StoryCount" HeaderText="Σύνολο άρθρων που υπέβαλε" DataFormatString="{0:0.##}"/>
            <asp:BoundField SortExpression="CommentCount" DataField="CommentCount" HeaderText="Σύνολο σχολίων" DataFormatString="{0:0.##}"/>
            <asp:BoundField SortExpression="VotesUp" DataField="VotesUp" HeaderText="Θετικές ψήφοι" DataFormatString="{0:0.##}"/>
            <asp:BoundField SortExpression="VotesDiffByCommentCount" DataField="VotesDiffByCommentCount"
                            HeaderText="Θετικές ψήφοι / σύνολο σχολίων" DataFormatString="{0:0.##}"/>
            <asp:BoundField SortExpression="CommentedStoriesCount" DataField="CommentedStoriesCount"
                            HeaderText="Σύνολο άρθρων που σχολίασε" DataFormatString="{0:0.##}"/>
            <asp:BoundField SortExpression="BuriedCommentCount" DataField="BuriedCommentCount"
                            HeaderText="Σύνολο θαμμένων σχολίων" DataFormatString="{0:0.##}"/>
        </Columns>
    </asp:GridView>

    <p>
        Οι στήλες <q>Σύνολο σχολίων</q> και <q>Θετικές ψήφοι / σύνολο σχολίων</q>
        δεν συμπεριλαμβάνουν τα θαμμένα σχόλια.
    </p>
</asp:Content>
