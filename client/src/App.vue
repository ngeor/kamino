<template>
    <div id="app">
        <nav>
            <ul>
                <li>
                    <a
                        class="active"
                        href="/">Αρχική</a>
                </li>
                <li>
                    <a href="/stories">Άρθρα</a>
                </li>
            </ul>
        </nav>

        <div class="main">
            <p>Welcome to BuzzStats</p>

            <h2>Πρόσφατα Σχόλια</h2>
            <RecentComments />

            <h2>Πρόσφατη Δραστηριότητα</h2>
            <RecentActivities />

            <h2>Πρόσφατα Δημοφιλή Σχόλια</h2>
        </div>

        <footer>
            Copyright &copy; 2010-{{ 1900 + new Date().getYear() }} Nikolaos Georgiou
            |
            BuzzStats version: {{ version }}
        </footer>
    </div>
</template>

<script>
import RecentActivities from './RecentActivities.vue';
import RecentComments from './RecentComments.vue';

export default {
    name: 'App',
    components: {
        RecentActivities,
        RecentComments
    },

    /**
     * Returns the initial data.
     * @returns {object} The initial data.
     */
    data() {
        return { version: require('../package.json').version };
    }
};
</script>

<style scoped>

</style>
