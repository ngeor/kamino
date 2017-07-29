import React from 'react';
import Comment from './Comment';

export default class extends React.Component {
    render() {
        return <div className="block">
            <div>{this.props.story.title}</div>
            <table>
                <thead>
                    <tr>
                        <th>User</th>
                        <th>Votes</th>
                    </tr>
                </thead>
                <tbody>
                    {this.props.story.comments.map((c) =>
                        <Comment key={c.commentId} comment={c} />
                    )}
                </tbody>
            </table>
        </div>;
    }
}
