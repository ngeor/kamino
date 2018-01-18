<template>
    <div class="comments">
        <StoryBlock
            v-for="recentComment in recentComments"
            :key="recentComment.id"
            :story="recentComment" />
    </div>
</template>

<script>
import ajax from './ajax';
import StoryBlock from './StoryBlock.vue';

export default {
    name: 'RecentComments',
    components: { StoryBlock },

    /**
     * Returns the initial data.
     * @returns {object} The initial data.
     */
    data() {
        return { recentComments: [] };
    },

    /**
     * Initializes the component.
     */
    created() {
        ajax('http://localhost:9000/api/recentcomments').then(data => {
            this.recentComments = data;
        }).catch(reason => alert(reason));
    }
};
</script>

<style scoped>

</style>
