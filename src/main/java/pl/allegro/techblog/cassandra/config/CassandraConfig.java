package pl.allegro.techblog.cassandra.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.data.cassandra.config.SchemaAction;
import org.springframework.data.cassandra.config.java.AbstractCassandraConfiguration;
import org.springframework.data.cassandra.repository.config.EnableCassandraRepositories;

@Configuration
@PropertySource({"classpath:cassandra.properties", "classpath:application.properties"})
@ComponentScan(basePackages = "pl.allegro.techblog.cassandra.domain")
@EnableCassandraRepositories(basePackages = {"pl.allegro.techblog.cassandra.domain",
        "pl.allegro.techblog.cassandra.infrastructure.persistence"})
public class CassandraConfig extends AbstractCassandraConfiguration {

    @Autowired
    private Environment env;

    @Override
    protected String getContactPoints() {
        return env.getProperty("cassandra.contactpoints");
    }

    @Override
    protected int getPort() {
        return Integer.parseInt(env.getProperty("cassandra.port"));
    }

    @Override
    protected String getKeyspaceName() {
        return env.getProperty("cassandra.keyspace");
    }

    @Override
    public SchemaAction getSchemaAction() {
        return SchemaAction.CREATE;
    }

    @Override
    public String[] getEntityBasePackages() {
        return new String[]{"pl.allegro.techblog.cassandra.domain"};
    }
}
