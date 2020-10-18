package com.github.ngeor.bitbucket;

import org.junit.jupiter.api.Test;

import com.github.ngeor.bitbucket.models.PaginatedRepositories;
import com.github.ngeor.bitbucket.models.Repository;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link PageCollector}.
 */
class PageCollectorTest {
  @Test
  void collect() {
    PaginatedRepositories firstPage =
        (PaginatedRepositories) new PaginatedRepositories()
            .addValuesItem(new Repository().uuid("abc"))
            .next("http://page-2");
    PaginatedRepositories secondPage =
        (PaginatedRepositories) new PaginatedRepositories()
            .addValuesItem(new Repository().uuid("def"))
            .next("");

    // act
    Iterable<PaginatedRepositories> paginatedRepositories =
        PageCollector.collectAll(new Paginator<>() {
          @Override
          public PaginatedRepositories first() {
            return firstPage;
          }

          @Override
          public PaginatedRepositories next(
              PaginatedRepositories previousPage) {
            String nextUrl = previousPage.getNext();
            if ("http://page-2".equals(nextUrl)) {
              return secondPage;
            } else {
              return null;
            }
          }
        });

    // assert
    assertThat(paginatedRepositories).containsExactly(firstPage, secondPage);
  }
}
