import React from 'react';
import {toAgo} from './date';

function recentActivityToText(recentActivity) {
    if (!recentActivity) {
        return 'N/A';
    }

    if (recentActivity.storyVoteUsername) {
        return 'Story ' + recentActivity.title + ' voted by user ' + recentActivity.storyVoteUsername;
    }

    if (recentActivity.commentUsername) {
        return 'New comment on story ' + recentActivity.title + ' by user ' + recentActivity.commentUsername +
            ' (' + toAgo(recentActivity.createdAt) + ')';
    }

    return 'New story ' + recentActivity.title + ' by user ' + recentActivity.storyUsername +
        ' (' + toAgo(recentActivity.createdAt) + ')';
}

export default class extends React.Component {
    render() {
        return <ol className="recent-activity">
            {this.props.data.map(d =>
                <li>{recentActivityToText(d)}</li>
            )}
        </ol>;
    }
}
