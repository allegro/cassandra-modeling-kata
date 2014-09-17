package pl.allegro.techblog.cassandra.config;

import com.google.common.eventbus.EventBus;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.inject.Inject;

@Configuration
public class ApplicationConfig {

    @Bean
    public EventBus eventBus() {
        return new EventBus();
    }

    @Bean
    public EventBusPostProcessor eventBusPostProcessor() {
        return new EventBusPostProcessor();
    }

    public static class EventBusPostProcessor implements BeanPostProcessor {

        @Inject
        private EventBus eventBus;


        @Override
        public Object postProcessBeforeInitialization(Object bean, String beanName) {
            return bean;
        }

        @Override
        public Object postProcessAfterInitialization(Object bean, String beanName) {
            eventBus.register(bean);

            return bean;
        }
    }
}
