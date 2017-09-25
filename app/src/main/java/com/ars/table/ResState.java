package com.ars.table;

import android.content.Context;

import com.ars.orm.OColumn;
import com.ars.orm.OModel;
import com.ars.orm.types.ColumnType;

public class ResState extends OModel {

    OColumn name = new OColumn("Name", ColumnType.VARCHAR);

    public ResState(Context context) {
        super(context, "res.state");
    }
}
