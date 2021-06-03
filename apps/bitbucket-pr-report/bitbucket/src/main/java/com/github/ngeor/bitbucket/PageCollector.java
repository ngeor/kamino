package com.github.ngeor.bitbucket;

import com.github.ngeor.bitbucket.models.BasePaginated;
import java.util.Iterator;

/**
 * Utility class to collect all pages of a paginated result.
 */
final class PageCollector {
    private PageCollector() {
    }

    /**
     * Collects all pages.
     */
    static <E extends BasePaginated> Iterable<E> collectAll(Paginator<E> paginator) {
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

