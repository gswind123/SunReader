package reader.sun.common;

import java.io.File;

import reader.sun.common.model.DataLocator;
import reader.sun.common.model.DataModel;

/**
 * Manager data from src to memory
 * Created by yw_sun on 2015/7/21.
 */
public interface DataProvider {
    public void parseFileToMem(File srcFile, DataLocator locator);
    public DataModel readData(DataLocator locator);
}
