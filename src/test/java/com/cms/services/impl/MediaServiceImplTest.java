package com.cms.services.impl;

import com.cms.model.testimonial.Media;
import com.cms.persistence.repository.MediaRepository;
import com.cms.services.ImageService;
import com.cms.services.YoutubeService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MediaServiceImplTest {

    @Mock
    private ImageService imageService;

    @Mock
    private YoutubeService youtubeService;

    @Mock
    private MediaRepository mediaRepository;

    @InjectMocks
    private MediaServiceImpl mediaService;

    @Test
    void saveCombinesImageAndVideoMedia() {
        MultipartFile image = new MockMultipartFile("file", "photo.jpg", "image/jpeg", new byte[]{1, 2, 3});
        String youtubeUrl = "https://www.youtube.com/watch?v=dQw4w9WgXcQ";

        Media imageMedia = Media.builder().publicId("image-id").url("https://cdn/image").build();
        Media videoMedia = Media.builder().videoId("video-id").videoUrl("https://youtube/embed/video-id").build();
        Media storedMedia = Media.builder().id("media-id").build();

        when(imageService.guardarImagen(image)).thenReturn(imageMedia);
        when(youtubeService.fromUrl(youtubeUrl)).thenReturn(videoMedia);
        when(mediaRepository.save(imageMedia, videoMedia)).thenReturn(storedMedia);

        Media result = mediaService.save(image, youtubeUrl);

        assertSame(storedMedia, result);
        verify(imageService).guardarImagen(image);
        verify(youtubeService).fromUrl(youtubeUrl);
        verify(mediaRepository).save(imageMedia, videoMedia);
    }

    @Test
    void saveSkipsProvidersWhenInputIsEmpty() {
        Media storedMedia = Media.builder().id("media-id").build();
        MultipartFile image = new MockMultipartFile("file", new byte[0]);

        when(mediaRepository.save(null, null)).thenReturn(storedMedia);

        Media result = mediaService.save(image, "   ");

        assertSame(storedMedia, result);
        verify(imageService, never()).guardarImagen(image);
        verify(youtubeService, never()).fromUrl("   ");
        verify(mediaRepository).save(null, null);
    }

    @Test
    void deleteImageRemovesRemoteAssetAndRepositoryFields() {
        mediaService.deleteImage("public-id");

        verify(imageService).deleteImage("public-id");
        verify(mediaRepository).clearImageFields("public-id");
    }

    @Test
    void deleteVideoOnlyClearsRepositoryFields() {
        mediaService.deleteVideo("video-id");

        verify(mediaRepository).clearVideoField("video-id");
        verify(imageService, never()).deleteImage("video-id");
    }
}
