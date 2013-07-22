#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.distributor;

import com.google.common.collect.ImmutableList;
import com.griddynamics.jagger.invoker.QueryPoolLoadBalancer;
import com.griddynamics.jagger.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: nmusienko
 * Date: 04.03.13
 * Time: 14:47
 * To change this template use File | Settings | File Templates.
 */
public class RandomQueryDistributor<Q, E>  extends QueryPoolLoadBalancer<Q, E> {
    private static Logger log=LoggerFactory.getLogger(RandomQueryDistributor.class);

    public RandomQueryDistributor(){
        super();
    }

    public RandomQueryDistributor(Iterable<Q> queryProvider, Iterable<E> endpointProvider){
        super(queryProvider, endpointProvider);
    }

    @Override
    public Iterator<Pair<Q, E>> provide() {
        final List queries=ImmutableList.copyOf(queryProvider.iterator());
        final List endpoints=ImmutableList.copyOf(endpointProvider.iterator());
        final Random rand=new Random(System.currentTimeMillis());

        return new Iterator<Pair<Q, E>>() {
            @Override
            public boolean hasNext() {
                return true;
            }

            @Override
            public Pair<Q, E> next() {
                E endpoint = (E) endpoints.get(Math.abs(rand.nextInt(endpointSize())));
                Q query = (Q) queries.get(Math.abs(rand.nextInt(querySize())));
                return Pair.of(query, endpoint);
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException("Read only iterator");
            }

            @Override
            public String toString() {
                return "RandomQueryDistributor iterator";
            }
        };
    }

    @Override
    public String toString() {
        return "RandomQueryDistributor";
    }
}
