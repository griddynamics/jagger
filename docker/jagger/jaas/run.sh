   # Check if all important parameters exist
if [ -z "$JAAS_HTTP_PORT" ] && [ -z "$JAAS_DB_DRIVER" ] && [ -z "$JAAS_DB_URL" ] && [ -z "$JAAS_DB_USER" ] && [ -z "$JAAS_DB_PASS" ] && [ -z "$JAAS_HIBERNATE_DIALECT"]; then
    echo "env is empty, please specify parameters"
else
    # Command "sleep 10" needed for initialisation of database. Watch JFG-947
    sleep 10
    java -Djaas.hibernate.dialect=${JAAS_HIBERNATE_DIALECT} -Djaas.db.driver=${JAAS_DB_DRIVER} -Djaas.db.url=${JAAS_DB_URL} -Djaas.db.user=${JAAS_DB_USER} -Djaas.db.pass=${JAAS_DB_PASS} -Djagger.db.default.url=${JAGGER_DB_DEFAULT_URL} -Djagger.db.default.desc=${JAGGER_DB_DEFAULT_DESC} -Djagger.db.default.user=${JAGGER_DB_DEFAULT_USER} -Djagger.db.default.pass=${JAGGER_DB_DEFAULT_PASSWORD} -Djagger.db.default.jdbcDriver=${JAGGER_DB_DEFAULT_DRIVER} -Djagger.db.default.hibernateDialect=${JAGGER_DB_DEFAULT_HIBERNATE_DIALECT} -jar /com/griddynamics/jagger/jaas.jar  --server.port=${JAAS_HTTP_PORT}
fi
