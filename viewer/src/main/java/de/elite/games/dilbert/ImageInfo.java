package de.elite.games.dilbert;

class ImageInfo {

    private final String src;
    private final int width;
    private final int height;

    ImageInfo(final String src, final int width, final int height) {
        this.src = src;
        this.width = width;
        this.height = height;
    }

    String getSrc() {
        return src;
    }

    int getWidth() {
        return width;
    }

    int getHeight() {
        return height;
    }

    @Override
    public String toString() {
        return "ImageInfo{" +
                "src='" + src + '\'' +
                ", width=" + width +
                ", height=" + height +
                '}';
    }
}
