package ${package};

import com.griddynamics.jagger.invoker.v2.JHttpQuery;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Andrey Badaev
 * Date: 10/11/16
 */
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
