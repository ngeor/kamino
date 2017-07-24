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
                user: 'user 1'
            }
        ]
    },
    {
        storyId: 2,
        title: 'story 2',
        comments: [
            {
                commentId: 20,
                user: 'user 2'
            },
            {
                commentId: 21,
                user: 'user 3'
            }
        ]
    }
];

function useStaticData() {
    return location.protocol === 'file:';
}

function loadRecentComments(callback) {
    if (useStaticData()) {
        callback(sampleData);
        return;
    }

    loadJson('/api/recent-comments', callback);
}

document.addEventListener('DOMContentLoaded', function() {
    loadRecentComments(function(data) {
        ReactDOM.render(
            <RecentComments data={data}/>,
            document.getElementById('root')
        );
    });
});
