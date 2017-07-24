import React from 'react';
import Comment from './Comment';

export default class extends React.Component {
    render() {
        return <div className="block">
            {this.props.story.title}
            {this.props.story.comments.map((c) =>
                <Comment key={c.commentId} comment={c} />
            )}
        </div>;
    }
}
