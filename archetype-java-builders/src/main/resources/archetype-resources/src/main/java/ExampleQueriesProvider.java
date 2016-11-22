package ${package};

import com.griddynamics.jagger.invoker.v2.JHttpQuery;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Simple example of query provider
 *
 */
// begin: following section is used for docu generation - Query provider
public class ExampleQueriesProvider  implements Iterable {
    @Override
    public Iterator iterator() {
        List<JHttpQuery> queries = new ArrayList<>();
        queries.add(new JHttpQuery()
                .get()
                .path("index.html"));

        queries.add(new JHttpQuery()
                .get()
                .responseBodyType(String.class)
                .path("screenshots.html"));

        queries.add(new JHttpQuery()
                .get()
                .responseBodyType(String.class)
                .path("download.html"));
        
        return queries.iterator();
    }
}
// end: following section is used for docu generation - Query provider

