package com.cms.persistence.mongo;

import com.cms.persistence.mongo.entity.MediaMongo;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface MediaMongoDAO extends MongoRepository<MediaMongo, String> {

    Optional<MediaMongo> findByImagePublicId(String imagePublicId);

    Optional<MediaMongo> findByVideoId(String videoId);
}
