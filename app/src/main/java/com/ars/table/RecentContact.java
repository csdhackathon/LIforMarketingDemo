package com.ars.table;

import android.content.Context;

import com.ars.orm.OColumn;
import com.ars.orm.OModel;
import com.ars.orm.types.ColumnType;

public class RecentContact extends OModel {

    OColumn contact_id = new OColumn("Contact Id", ColumnType.INTEGER);
    OColumn write_date = new OColumn("Write Date", ColumnType.VARCHAR);

    public RecentContact(Context context) {
        super(context, "recent.contact");
    }
}
