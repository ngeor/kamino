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
                <Comment v-for="comment in story.comments"
                    :comment="comment" />
            </tbody>
        </table>
    </div>
</template>

<script>

import Comment from './Comment.vue';

const storyProp = {
    type: Object,
    default: function() {
        return {
            title: 'title'
        }
    }
};

export default {
    name: 'StoryBlock',
    components: { Comment },
    props: { story: storyProp }
};
</script>

<style scoped>

</style>
