package com.github.ngeor.bitbucket;

/**
 * A paginator that provides first and next pages.
 *
 * @param <E> the type of the paginated results.
 */
interface Paginator<E> {
    E first();

    E next(E previousPage);
}
