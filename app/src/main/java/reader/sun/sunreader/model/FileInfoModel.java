package reader.sun.sunreader.model;

/**
 * Created by yw_sun on 2015/7/16.
 */
public class FileInfoModel {
    private String absolutePath;
    private String name;
    private String type;
    private boolean isDirectory;

    public FileInfoModel(String absolutePath, String name) {
        this(absolutePath, name, false);
    }
    public FileInfoModel(String absolutePath, String name, boolean isDirectory) {
        this.absolutePath = absolutePath;
        this.name = name;
        this.isDirectory = isDirectory;
        String[] nameSegs = name.split("\\.");
        if(nameSegs.length<2) {
            this.type = "";
        } else {
            this.type = nameSegs[nameSegs.length-1].toLowerCase();
        }
    }
    public String getAbsolutePath() {
        return absolutePath;
    }
    public String getName() {
        return name;
    }
    public String getType() {
        return type;
    }
    public boolean isDirectory() {
        return isDirectory;
    }
}
