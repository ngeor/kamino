package com.github.ngeor.changelog;

import static org.assertj.core.api.Assertions.assertThat;

import com.github.ngeor.git.Commit;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;

class ReleaseGrouperTest {
    @Test
    void testEmpty() {
        List<List<Commit>> groups = fromCommits(Stream.empty());
        assertThat(groups).isEmpty();
    }

    @Test
    void testOneWithoutTag() {
        Commit a = new Commit(null, null, null, "1");
        Stream<Commit> commits = Stream.of(a);
        List<List<Commit>> groups = fromCommits(commits);
        assertThat(groups).containsExactly(Collections.singletonList(a));
    }

    @Test
    void testTwoWithoutTag() {
        Commit a = new Commit(null, null, null, "1");
        Commit b = new Commit(null, null, null, "2");
        Stream<Commit> commits = Stream.of(b, a);
        List<List<Commit>> groups = fromCommits(commits);
        assertThat(groups).containsExactly(List.of(a, b));
    }

    @Test
    void testThreeWithoutTag() {
        Commit a = new Commit(null, null, null, "1");
        Commit b = new Commit(null, null, null, "2");
        Commit c = new Commit(null, null, null, "3");
        Stream<Commit> commits = Stream.of(c, b, a);
        List<List<Commit>> groups = fromCommits(commits);
        assertThat(groups).containsExactly(List.of(a, b, c));
    }

    @Test
    void testOneWithTag() {
        Commit a = new Commit(null, null, "v1", "1");
        Stream<Commit> commits = Stream.of(a);
        List<List<Commit>> groups = fromCommits(commits);
        assertThat(groups).containsExactly(Collections.singletonList(a));
    }

    @Test
    void testTwoWithTagOnFirst() {
        Commit a = new Commit(null, null, "v1", "1");
        Commit b = new Commit(null, null, null, "2");
        Stream<Commit> commits = Stream.of(b, a);
        List<List<Commit>> groups = fromCommits(commits);
        assertThat(groups).containsExactly(List.of(b), List.of(a));
    }

    @Test
    void testTwoWithTagOnSecond() {
        Commit a = new Commit(null, null, null, "1");
        Commit b = new Commit(null, null, "v2", "2");
        Stream<Commit> commits = Stream.of(b, a);
        List<List<Commit>> groups = fromCommits(commits);
        assertThat(groups).containsExactly(List.of(a, b));
    }

    @Test
    void testTwoWithTagOnBoth() {
        Commit a = new Commit(null, null, "v1", "1");
        Commit b = new Commit(null, null, "v2", "2");
        Stream<Commit> commits = Stream.of(b, a);
        List<List<Commit>> groups = fromCommits(commits);
        assertThat(groups).containsExactly(List.of(b), List.of(a));
    }

    @Test
    void testThreeWithTagOnFirst() {
        Commit a = new Commit(null, null, "v1", "1");
        Commit b = new Commit(null, null, null, "2");
        Commit c = new Commit(null, null, null, "3");
        Stream<Commit> commits = Stream.of(c, b, a);
        List<List<Commit>> groups = fromCommits(commits);
        assertThat(groups).containsExactly(List.of(b, c), List.of(a));
    }

    private List<List<Commit>> fromCommits(Stream<Commit> commits) {
        return new ReleaseGrouper(null).fromCommits(commits);
    }
}
