package pl.allegro.techblog.cassandra;

import pl.allegro.techblog.cassandra.config.ApplicationConfig;
import pl.allegro.techblog.cassandra.config.CassandraConfig;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@ContextConfiguration(classes = {CassandraConfig.class, ApplicationConfig.class})
@TestExecutionListeners(listeners = {CassandraTestExecutionListener.class, DependencyInjectionTestExecutionListener.class})
@ActiveProfiles("int")
public @interface CassandraIntegrationTest {
}
