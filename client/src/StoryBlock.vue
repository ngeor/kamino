<template>
    <div class="block">
        <div>{{ story.title }}</div>
        <table>
            <thead>
                <tr>
                    <th>User</th>
                    <th>Created At</th>
                    <th>Votes</th>
                </tr>
            </thead>
            <tbody>
                <Comment
                    v-for="comment in story.comments"
                    :key="comment.id"
                    :comment="comment" />
            </tbody>
        </table>
    </div>
</template>

<script>

import Comment from './Comment.vue';

export default {
    name: 'StoryBlock',
    components: {
        Comment
    },
    props: {
        story: {
            type: Object,
            default: () => {
                return {
                    title: 'title'
                };
            }
        }
    }
};
</script>

<style scoped>

</style>
