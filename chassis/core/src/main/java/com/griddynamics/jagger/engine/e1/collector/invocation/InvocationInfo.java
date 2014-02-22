package com.griddynamics.jagger.engine.e1.collector.invocation;

/** Class, which contains some information about invocation execution
 * @author Gribov Kirill
 * @n
 * @par Details:
 * @details
 * @n
 * */
public class InvocationInfo<Q, R, E>  {
    private Q query;
    private R result;
    private E endpoint;
    private long duration;

    public InvocationInfo(Q query, E endpoint) {
        this.query = query;
        this.endpoint = endpoint;
    }

    /** Returns query of current invocation*/
    public Q getQuery() {
        return query;
    }

    public void setQuery(Q query) {
        this.query = query;
    }

    /** Returns the result of invocation*/
    public R getResult() {
        return result;
    }

    public void setResult(R result) {
        this.result = result;
    }

    /** Returns endpoint of current invocation*/
    public E getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(E endpoint) {
        this.endpoint = endpoint;
    }

    /** Returns invocation duration*/
    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }
}
