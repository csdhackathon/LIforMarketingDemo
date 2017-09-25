package com.ars.table;

import android.content.Context;

import com.ars.orm.OColumn;
import com.ars.orm.OModel;
import com.ars.orm.types.ColumnType;

public class ResCountry extends OModel {

    OColumn name = new OColumn("Name", ColumnType.VARCHAR);

    public ResCountry(Context context) {
        super(context, "res.country");
    }
}
