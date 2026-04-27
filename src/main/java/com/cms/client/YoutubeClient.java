package com.cms.client;

import com.cms.client.dto.YouTubeVideoResponse;

public interface YoutubeClient {

    YouTubeVideoResponse getVideoInfo(String videoId);
}
