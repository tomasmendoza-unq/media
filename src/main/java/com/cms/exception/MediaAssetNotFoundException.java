package com.cms.exception;

public final class MediaAssetNotFoundException extends RuntimeException {

    private final String assetType;
    private final String assetId;

    public MediaAssetNotFoundException(String assetType, String assetId) {
        super("invalid " + assetType + " #" + assetId);
        this.assetType = assetType;
        this.assetId = assetId;
    }

    public String getAssetType() {
        return assetType;
    }

    public String getAssetId() {
        return assetId;
    }
}
