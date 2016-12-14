package com.griddynamics.jagger.storage.rdb;

import javax.sql.DataSource;

/**
 * @author Artem Zhdanov <azhdanov@griddynamics.com>
 * @since 08/01/2015
 */
public interface RewritebleBatchedStatementsDataSource extends DataSource {

    public boolean getRewriteBatchedStatements();

    public void setRewriteBatchedStatements(boolean rewriteBatchedStatements);
}
