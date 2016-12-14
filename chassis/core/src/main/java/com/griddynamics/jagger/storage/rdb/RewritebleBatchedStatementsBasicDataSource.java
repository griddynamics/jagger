package com.griddynamics.jagger.storage.rdb;

import org.apache.commons.dbcp.BasicDataSource;

import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;

/**
 * @author Artem Zhdanov <azhdanov@griddynamics.com>
 * @since 08/01/2015
 */
public class RewritebleBatchedStatementsBasicDataSource extends BasicDataSource implements RewritebleBatchedStatementsDataSource {

    private static final String REWRITE_BATCHED_STATEMENTS = "rewriteBatchedStatements";
    private static final Logger logger = Logger.getLogger(RewritebleBatchedStatementsBasicDataSource.class.getName());

    @Override
    public boolean getRewriteBatchedStatements() {
        final String isRewrite = super.connectionProperties.getProperty(REWRITE_BATCHED_STATEMENTS);
        return isRewrite == null ? false : Boolean.valueOf(isRewrite);
    }

    @Override
    public void setRewriteBatchedStatements(final boolean rewriteBatchedStatements) {
        super.addConnectionProperty(REWRITE_BATCHED_STATEMENTS, String.valueOf(rewriteBatchedStatements));
    }

    @Override
    public <T> T unwrap(final Class<T> iface) throws SQLException {
        throw new UnsupportedOperationException("Is not implemented by org.apache.commons.dbcp.BasicDataSource so we will not implement it either");
    }

    @Override
    public boolean isWrapperFor(final Class<?> iface) throws SQLException {
        throw new UnsupportedOperationException("Is not implemented by org.apache.commons.dbcp.BasicDataSource so we will not implement it either");
    }


    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return logger;
    }
}
