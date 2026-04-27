package com.cms.client.impl;

import com.cms.client.YoutubeClient;
import com.cms.client.dto.YouTubeVideoResponse;
import org.springframework.web.client.RestTemplate;

public class YoutubeClientImpl implements YoutubeClient {

    private final String apiKey;
    private final RestTemplate restTemplate;

    public YoutubeClientImpl(String apiKey, RestTemplate restTemplate) {
        this.apiKey = apiKey;
        this.restTemplate = restTemplate;
    }

    @Override
    public YouTubeVideoResponse getVideoInfo(String videoId) {
        String url = "https://www.googleapis.com/youtube/v3/videos"
                + "?id=" + videoId
                + "&part=snippet,status"
                + "&key=" + apiKey;

        return restTemplate.getForObject(url, YouTubeVideoResponse.class);
    }
}
