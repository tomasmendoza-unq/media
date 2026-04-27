package com.cms.configuration;

import com.cloudinary.Cloudinary;
import com.cms.client.YoutubeClient;
import com.cms.client.impl.YoutubeClientImpl;
import com.cms.persistence.repository.MediaRepository;
import com.cms.services.ImageService;
import com.cms.services.MediaService;
import com.cms.services.YoutubeService;
import com.cms.services.impl.ImageServiceImpl;
import com.cms.services.impl.MediaServiceImpl;
import com.cms.services.impl.YouTubeServiceImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@AutoConfiguration
public class MediaAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty("cloudinary.url")
    public Cloudinary cloudinary(@Value("${cloudinary.url}") String cloudinaryUrl) {
        return new Cloudinary(cloudinaryUrl);
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty("youtube.api.key")
    public YoutubeClient youtubeClient(@Value("${youtube.api.key}") String apiKey) {
        return new YoutubeClientImpl(apiKey, new RestTemplate());
    }

    @Bean
    @ConditionalOnBean(Cloudinary.class)
    @ConditionalOnMissingBean
    public ImageService imageService(Cloudinary cloudinary) {
        return new ImageServiceImpl(cloudinary);
    }

    @Bean
    @ConditionalOnBean(YoutubeClient.class)
    @ConditionalOnMissingBean
    public YoutubeService youtubeService(YoutubeClient youtubeClient) {
        return new YouTubeServiceImpl(youtubeClient);
    }

    @Bean
    @ConditionalOnBean({MediaRepository.class, ImageService.class, YoutubeService.class})
    @ConditionalOnMissingBean
    public MediaService mediaService(ImageService imageService,
                                     YoutubeService youtubeService,
                                     MediaRepository mediaRepository) {
        return new MediaServiceImpl(imageService, youtubeService, mediaRepository);
    }
}
