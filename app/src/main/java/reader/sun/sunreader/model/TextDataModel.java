package reader.sun.sunreader.model;

import reader.sun.common.foundation.util.StringUtil;
import reader.sun.common.model.DataModel;

/**
 * Created by yw_sun on 2015/7/21.
 */
public class TextDataModel extends DataModel {
    public String mTextData = "";

    @Override
    public boolean isEmpty() {
        return StringUtil.emptyOrNull(mTextData);
    }
}
