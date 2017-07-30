import React from 'react';
import ReactDOM from 'react-dom';
import RecentComments from './RecentComments';
import {loadJson} from './ajax';

const sampleData = [
    {
        storyId: 1,
        title: 'story 1',
        comments: [
            {
                commentId: 10,
                username: 'user 1',
                votesUp: 1,
                createdAt: '2017-04-21T18:29:56'
            }
        ]
    },
    {
        storyId: 2,
        title: 'story 2',
        comments: [
            {
                commentId: 20,
                username: 'user 2',
                votesUp: 2,
                createdAt: '2017-04-20T08:19:26'
            },
            {
                commentId: 21,
                username: 'user 3',
                votesUp: 1,
                createdAt: '2017-03-10T12:00:01'
            }
        ]
    }
];

function useStaticData() {
    return location.protocol === 'file:';
}

function loadRecentComments(callback) {
    if (useStaticData()) {
        callback(null, sampleData);
        return;
    }

    loadJson('/api/recentcomments', callback);
}

document.addEventListener('DOMContentLoaded', function() {
    loadRecentComments(function(err, data) {
        ReactDOM.render(
            <RecentComments data={data}/>,
            document.getElementById('root')
        );
    });
});
