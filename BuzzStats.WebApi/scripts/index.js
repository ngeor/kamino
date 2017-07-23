function hello() {
    console.log('hello, world!');
}

function loadJson(url, callback) {
    var request = new XMLHttpRequest();
    request.open('GET', url, true);
    request.onload = function() {
        if (request.status >= 200 && request.status < 400) {
            var data = JSON.parse(request.responseText);
            callback(null, data);
        } else {
            callback(new Error(request.status));
        }
    };

    request.onerror = function() {
        callback(new Error(request.responseText));
    };

    request.send();
}

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

import React from 'react';
import ReactDOM from 'react-dom';

class Comment extends React.Component {
    render() {
        return <div>{this.props.comment.user}</div>;
    }
}
class StoryBlock extends React.Component {
    render() {
        return <div className="block">
            {this.props.story.title}
            {this.props.story.comments.map((c) =>
                <Comment key={c.commentId} comment={c} />
            )}
        </div>;
    }
}

class RecentComments extends React.Component {
    render() {
        return <div className="comments">
            Recent Comments
            {this.props.data.map((d) =>
                <StoryBlock key={d.storyId} story={d}/>
            )}
        </div>;
    }
}

document.addEventListener('DOMContentLoaded', function() {
    hello();
    ReactDOM.render(
        <RecentComments data={sampleData}/>,
        document.getElementById('root')
    );
});
