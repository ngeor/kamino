<template>
    <tr>
        <td>{{ comment.username }}</td>
        <td :title="comment.createdAt">{{ toAgo(comment.createdAt) }}</td>
        <td>{{ comment.votesUp }}</td>
    </tr>
</template>

<script>
import { toAgo } from './date';

const commentProp = {
    type: Object,
    default: function() {
        return {
            username: 'user'
        };
    }
};

export default {
    name: 'Comment',
    props: { comment: commentProp },
    methods: { toAgo }
};
</script>

<style scoped>

</style>
