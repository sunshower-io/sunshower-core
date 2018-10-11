package io.sunshower.model.core.auth;

public enum ImageType {
  SVG,
  JPG,
  PNG,
  GIF;

  public static ImageType forPath(String path) {
    char ch;
    int len;
    if (path == null
        || (len = path.length()) == 0
        || (ch = path.charAt(len - 1)) == '/'
        || ch == '\\'
        || ch == '.') {
      throw new IllegalArgumentException("No image type for null or empty");
    }
    int dotInd = path.lastIndexOf('.'),
        sepInd = Math.max(path.lastIndexOf('/'), path.lastIndexOf('\\'));
    if (dotInd <= sepInd) {
      throw new IllegalArgumentException("No extension provided");
    } else {
      final String p = path.substring(dotInd + 1).toLowerCase();
      switch (p) {
        case "svg":
          return SVG;
        case "jpg":
          return JPG;
        case "png":
          return PNG;
        case "gif":
          return GIF;
      }
      throw new IllegalArgumentException("no known image type: " + p);
    }
  }
}
