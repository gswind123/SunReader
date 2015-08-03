package reader.sun.common.model;

/**
 * Represent a piece of data in DataProvider
 * Created by yw_sun on 2015/7/21.
 */
public class DataLocator implements  Cloneable{
    @Override
    public Object clone() {
        return new DataLocator();
    }

    public boolean isEmpty() {
        return false;
    }
}
