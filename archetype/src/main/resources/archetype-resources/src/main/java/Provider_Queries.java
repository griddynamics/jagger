package ${package};

import com.griddynamics.jagger.invoker.v2.JHttpQuery;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * @author asokol
 *         created 11/9/16
 */
public class Provider_Queries implements Iterable {
    @Override
    public Iterator iterator() {
        List<JHttpQuery> queries = new LinkedList<JHttpQuery>();
        queries.add(new JHttpQuery()
                .get()
                .path("/files/archive/spec/2.11/"));
        queries.add(new JHttpQuery()
                .get()
                .responseBodyType(String.class)
                .path("files", "archive", "spec", "2.11"));
        return queries.iterator();
    }
}