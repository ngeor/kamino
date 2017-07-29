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
                user: 'user 1',
                votesUp: 1
            }
        ]
    },
    {
        storyId: 2,
        title: 'story 2',
        comments: [
            {
                commentId: 20,
                user: 'user 2',
                votesUp: 2
            },
            {
                commentId: 21,
                user: 'user 3',
                votesUp: 1
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
