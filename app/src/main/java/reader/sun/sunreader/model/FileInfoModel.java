package reader.sun.sunreader.model;

/**
 * Created by yw_sun on 2015/7/16.
 */
public class FileInfoModel {
    public String absolutePath;
    public String name;
    public boolean isDirectory;

    public FileInfoModel(String absolutePath, String name) {
        this(absolutePath, name, false);
    }
    public FileInfoModel(String absolutePath, String name, boolean isDirectory) {
        this.absolutePath = absolutePath;
        this.name = name;
        this.isDirectory = isDirectory;
    }
}
