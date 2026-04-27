package com.cms.services.impl;

import com.cms.client.YoutubeClient;
import com.cms.client.dto.YouTubeVideoResponse;
import com.cms.model.testimonial.Media;
import com.cms.services.YoutubeService;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class YouTubeServiceImpl implements YoutubeService {

    private static final Pattern YOUTUBE_URL_PATTERN =
            Pattern.compile("(?:youtube\\.com/watch\\?v=|youtu\\.be/)([a-zA-Z0-9_-]{11})");

    private final YoutubeClient youtubeClient;

    public YouTubeServiceImpl(YoutubeClient youtubeClient) {
        this.youtubeClient = youtubeClient;
    }

    @Override
    public Media fromUrl(String youtubeUrl) {
        String videoId = extractVideoId(youtubeUrl);
        if (videoId == null) {
            throw new IllegalArgumentException("URL de YouTube invalida: " + youtubeUrl);
        }

        YouTubeVideoResponse response = youtubeClient.getVideoInfo(videoId);

        if (response == null || response.isEmpty()) {
            throw new IllegalArgumentException("El video no existe o no esta disponible");
        }

        YouTubeVideoResponse.Item item = response.items().getFirst();

        if (!"public".equals(item.status().privacyStatus())) {
            throw new IllegalArgumentException("El video no es publico");
        }

        return Media.builder()
                .videoId(videoId)
                .videoUrl("https://www.youtube.com/embed/" + videoId)
                .videoTitle(item.snippet().title())
                .thumbnailUrl(item.snippet().thumbnails().high().url())
                .channelName(item.snippet().channelTitle())
                .build();
    }

    private String extractVideoId(String url) {
        Matcher matcher = YOUTUBE_URL_PATTERN.matcher(url);
        return matcher.find() ? matcher.group(1) : null;
    }
}
