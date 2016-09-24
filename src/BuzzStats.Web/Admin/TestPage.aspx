<%@ Page Title="" Language="C#" MasterPageFile="~/Admin/Admin.master" AutoEventWireup="true" CodeBehind="TestPage.aspx.cs" Inherits="BuzzStats.Web.Admin.TestPage" EnableViewState="true" %>
<asp:Content ContentPlaceHolderID="cphBody" runat="server">
    <fieldset>
        <asp:Label runat="server" ID="lblError"></asp:Label>
    </fieldset>

    <table>
        <thead>
        <tr>
            <th>Story Id</th>
            <th>Title</th>
            <th>Username</th>
            <th>Vote Count</th>
            <th>Created At</th>
            <th>Last Checked At</th>
        </tr>
        </thead>
        <tbody data-bind="foreach: stories">
        <tr>
            <td data-bind="text: StoryId">story id</td>
            <td data-bind="text: Title">story title</td>
            <td data-bind="text: Username"></td>
            <td data-bind="text: VoteCount"></td>
            <td data-bind="text: formatJsonDateAsAge(CreatedAt)"></td>
            <td data-bind="text: formatJsonDateAsAge(LastCheckedAt)"></td>
        </tr>
        </tbody>
    </table>

    <div id="msg">
        <span data-bind="text: latestCheckedStory.SelectorName">Selector</span>
        checked story
        <span data-bind="text: latestCheckedStory.StoryId">42</span>
        at
        <span data-bind="text: latestCheckedStory.timestamp">2012-12-12</span>
        <span data-bind="visible: latestCheckedStory.HadChanges">and it had changes!!!</span>
    </div>

    <div>
        <asp:Button ID="btnThrow" runat="server" Text="Throw an exception" OnClick="btnThrow_Click"/>
    </div>
</asp:Content>
<asp:Content ContentPlaceHolderID="cphScripts" runat="server">
    <script type="text/javascript">
        <!--
        $(function() {

            var model = (function() {
                var model = {
                    stories: ko.observableArray(),
                    latestCheckedStory: {
                        StoryId: ko.observable(),
                        SelectorName: ko.observable(),
                        HadChanges: ko.observable(false),
                        timestamp: ko.observable()
                    }
                };

                ko.applyBindings(model);
                return model;
            })();

            var idx = 0;
            var urlPrefix = window.location.href.indexOf('/BuzzStats/') > 0 ? '/BuzzStats' : '';

            function longPollingAjax(options, ajaxCall) {
                var LONGPOLLING_INTERVAL = 5000;
                var newOptions = clone(options || {});

                function timeoutHandler() {
                    return ajaxCall(newOptions);
                }

                merge(newOptions, {
                    success: function(data) {
                        options.success(data);
                        setTimeout(timeoutHandler, LONGPOLLING_INTERVAL);
                    },

                    error: function(jqxhr, error) {
                        options.error(jqxhr, error);
                        setTimeout(timeoutHandler, LONGPOLLING_INTERVAL);
                    }
                });

                return timeoutHandler();
            }

            longPollingAjax({
                url: urlPrefix + '/api/CrawlerEventsJs.ashx',
                type: 'POST',
                data: {
                    idx: idx
                },
                success: function(data) {
                    if ('message' in data) {
                        var msg = data.message;
                        model.latestCheckedStory.StoryId(msg.StoryId);
                        model.latestCheckedStory.SelectorName(msg.SelectorName);
                        model.latestCheckedStory.HadChanges(msg.HadChanges);
                        model.latestCheckedStory.timestamp(data.timestamp);
                    } else {
                        console.warn('no data');
                    }

                    idx = data.idx;
                },
                error: function() {
                    console.warn('timeout?');
                }
            }, $.ajax);

            longPollingAjax({
                success: function(data) {
                    model.stories(data.d);
                },
                error: function() {
                    console.warn('Error loading stories.');
                }
            }, function(options) {
                return readService2.getStorySummaries(0, 10, "LastCheckedAt", options);
            });


        });
        //-->
    </script>
</asp:Content>
