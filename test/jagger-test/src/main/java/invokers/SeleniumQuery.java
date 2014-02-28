package invokers;

public class SeleniumQuery {

    private String query;

    // what to look for while waiting
    private String attend;

    private HowToLookFor wayToLookFor;

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public String getAttend() {
        return attend;
    }

    public void setAttend(String attend) {
        this.attend = attend;
    }

    public HowToLookFor getWayToLookFor() {
        return wayToLookFor;
    }

    public void setWayToLookFor(HowToLookFor wayToLookFor) {
        this.wayToLookFor = wayToLookFor;
    }

    static enum HowToLookFor {
        BY_TAG,
        BY_CLASS
    }



}
