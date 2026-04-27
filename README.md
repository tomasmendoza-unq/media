# shared-media

Reusable media library extracted from the CMS backend.

## What it includes

- `Media` domain model
- `MediaService` orchestration
- SPI contracts for image storage, YouTube resolution, and media persistence
- Default Spring Boot adapters for Cloudinary and YouTube

## Build

```powershell
./mvnw.cmd -f shared-media/pom.xml clean install
```

## Maven dependency

```xml
<dependency>
    <groupId>com.cms.media</groupId>
    <artifactId>shared-media</artifactId>
    <version>0.1.0-SNAPSHOT</version>
</dependency>
```

## Notes

- The library is standalone and can be moved to a dedicated repository without changing its POM.
- The current CMS still consumes it through the Maven reactor while the extraction is stabilized.
- Required properties for the default adapters:
  - `cloudinary.url`
  - `youtube.api.key`
