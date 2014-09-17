package pl.allegro.techblog.cassandra;


import info.archinnov.achilles.embedded.CassandraEmbeddedServerBuilder;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.support.AbstractTestExecutionListener;

public class CassandraTestExecutionListener extends AbstractTestExecutionListener {

    private static final String KEYSPACE_NAME = "cassandra_modeling_kata";

    private static final int CQL_PORT = 9143;

    @Override
    public void beforeTestClass(TestContext testContext) throws Exception {
        super.beforeTestClass(testContext);

        CassandraEmbeddedServerBuilder
                .noEntityPackages()
                .withClusterName("Test Cluster")
                .withKeyspaceName(KEYSPACE_NAME)
                .withCQLPort(CQL_PORT)
                .withDurableWrite(true)
                .cleanDataFilesAtStartup(true)
                .buildNativeSessionOnly();
    }
}
