package com.mgrud.github.proxy.gitproxycore.infrastructure.thread;

import java.util.Collection;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.stream.Collectors;

public class AsynchronousQueryUtil {

    private AsynchronousQueryUtil() {
    }

    public static <R> Collection<R> getFutureQueriesResult(Collection<CompletableFuture<Collection<R>>> futureDPEsQueries, String exMsg) {
        try {
            return futureDPEsQueries.stream()
                    .map(CompletableFuture::join)
                    .flatMap(Collection::stream)
                    .collect(Collectors.toList());
        } catch (CompletionException | CancellationException ex) {
            throw new IllegalStateException(exMsg, ex);
        }
    }
}
