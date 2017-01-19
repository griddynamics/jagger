package com.griddynamics.jagger.invoker.batch;

import com.griddynamics.jagger.invoker.InvocationException;
import com.griddynamics.jagger.invoker.Invoker;

/** Responsible for batch invocation on specified endpoint and query batches
 * @author Anton Antonenko
 * @n
 * @par Details:
 * @details Create requests to some endpoints with specified queries.
 * Number of queries and endpoints in batches must be equal (if queryBatch is not null).
 * The result of invocation can be collected by metrics and validators.
 * Note that Invoker is used in multi thread environment, so realize thread-safe implementation @n
 * @n
 * To view all invokers implementations click here @ref Main_Invokers_group
 *
 * @param <Q> - Query type
 * @param <R> - Result type
 * @param <E> - Endpoint type
 *
 * @ingroup Main_Invokers_Base_group */
public interface BatchInvoker<Q, R, E> extends Invoker<QueryBatch<Q>, ResponseBatch<R>, EndpointBatch<E>> {

    ResponseBatch<R> invoke(QueryBatch<Q> queryBatch, EndpointBatch<E> endpointBatch) throws InvocationException;
}
