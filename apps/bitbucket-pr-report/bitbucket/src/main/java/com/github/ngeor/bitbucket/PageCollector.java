package com.github.ngeor.bitbucket;

import java.util.Iterator;

import com.github.ngeor.bitbucket.models.BasePaginated;

/**
 * Utility class to collect all pages of a paginated result.
 */
final class PageCollector {
  private PageCollector() {}

  /**
   * Collects all pages.
   */
  static <E extends BasePaginated> Iterable<E>
  collectAll(Paginator<E> paginator) {
    return () -> new Iterator<E>() {
      private boolean isFirst = true;
      private E result;

      @Override
      public boolean hasNext() {
        if (isFirst) {
          result = paginator.first();
          isFirst = false;
        } else if (result != null) {
          result = paginator.next(result);
        }
        return result != null;
      }

      @Override
      public E next() {
        return result;
      }
    };
  }
}

/**
 * A paginator that provides first and next pages.
 * @param <E> the type of the paginated results.
 */
interface Paginator<E> {
  E first();

  E next(E previousPage);
}
