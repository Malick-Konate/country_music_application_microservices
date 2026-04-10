package com.konate.music_application.podcastsubdomain.DataLayer;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.repository.init.Jackson2RepositoryPopulatorFactoryBean;

@Configuration
public class PodcastDataPopulatorConfig {

    @Bean
    public Jackson2RepositoryPopulatorFactoryBean getRepositoryPopulator() {
        Jackson2RepositoryPopulatorFactoryBean factory = new Jackson2RepositoryPopulatorFactoryBean();


        factory.setResources(new Resource[]{
                new ClassPathResource("podcasts.json"),
                new ClassPathResource("episodes.json")
        });

        return factory;
    }
}