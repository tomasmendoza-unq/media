package com.cms.configuration;

import com.cloudinary.Cloudinary;
import com.cms.client.YoutubeClient;
import com.cms.client.impl.YoutubeClientImpl;
import com.cms.persistence.mongo.MediaMongoDAO;
import com.cms.persistence.repository.MediaRepository;
import com.cms.persistence.repository.impl.MediaRepositoryImpl;
import com.cms.persistence.repository.mapper.MediaMapper;
import com.cms.persistence.repository.mapper.impl.MediaMapperImpl;
import com.cms.services.ImageService;
import com.cms.services.MediaService;
import com.cms.services.YoutubeService;
import com.cms.services.impl.ImageServiceImpl;
import com.cms.services.impl.MediaServiceImpl;
import com.cms.services.impl.YouTubeServiceImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.Conditional;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

@AutoConfiguration
public class MediaAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    @Conditional(CloudinaryUrlConfiguredCondition.class)
    public Cloudinary cloudinary(@Value("${cloudinary.url:}") String cloudinaryUrl) {
        return new Cloudinary(cloudinaryUrl);
    }

    @Bean
    @ConditionalOnMissingBean
    @Conditional(YoutubeApiKeyConfiguredCondition.class)
    public YoutubeClient youtubeClient(@Value("${youtube.api.key:}") String apiKey) {
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
    @ConditionalOnMissingBean
    public MediaMapper mediaMapper() {
        return new MediaMapperImpl();
    }

    @Bean
    @ConditionalOnClass(MongoRepository.class)
    @ConditionalOnBean(MediaMongoDAO.class)
    @ConditionalOnMissingBean
    public MediaRepository mediaRepository(MediaMongoDAO mediaMongoDAO, MediaMapper mediaMapper) {
        return new MediaRepositoryImpl(mediaMongoDAO, mediaMapper);
    }

    @Bean
    @ConditionalOnBean({MediaRepository.class, ImageService.class, YoutubeService.class})
    @ConditionalOnMissingBean
    public MediaService mediaService(ImageService imageService,
                                     YoutubeService youtubeService,
                                     MediaRepository mediaRepository) {
        return new MediaServiceImpl(imageService, youtubeService, mediaRepository);
    }

    private static boolean hasTextProperty(ConditionContext context, String propertyName) {
        try {
            return StringUtils.hasText(context.getEnvironment().getProperty(propertyName));
        } catch (IllegalArgumentException exception) {
            return false;
        }
    }

    static class CloudinaryUrlConfiguredCondition implements Condition {

        @Override
        public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
            return hasTextProperty(context, "cloudinary.url");
        }
    }

    static class YoutubeApiKeyConfiguredCondition implements Condition {

        @Override
        public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
            return hasTextProperty(context, "youtube.api.key");
        }
    }
}
