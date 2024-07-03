package codesquad.http11;

/**
 * 지원할 컨텐츠 타입의 확장자 목록
 * html
 * css
 * js
 * ico
 * png
 * jpg
 * svg
 */
public enum MimeType {
    HTML("text/html"),
    CSS("text/css"),
    JS("application/javascript"),
    ICO("image/x-icon"),
    PNG("image/png"),
    JPG("image/jpeg"),
    SVG("image/svg+xml"),
    DEFAULT("application/octet-stream");

    public final String type;

    MimeType(String type) {
        this.type = type;
    }

    public static MimeType findMimeTypeByFileName(String fileName) {
        if (fileName.endsWith(".html") || fileName.endsWith(".htm")) {
            return HTML;
        } else if (fileName.endsWith(".css")) {
            return CSS;
        } else if (fileName.endsWith(".js")) {
            return JS;
        } else if (fileName.endsWith(".ico")) {
            return ICO;
        } else if (fileName.endsWith(".png")) {
            return PNG;
        } else if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")) {
            return JPG;
        } else if (fileName.endsWith(".svg")) {
            return SVG;
        }
        return DEFAULT;
    }
}
