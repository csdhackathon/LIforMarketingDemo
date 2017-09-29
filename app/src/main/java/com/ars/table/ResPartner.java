package com.ars.table;

import android.content.Context;
import android.net.Uri;

import com.ars.orm.OColumn;
import com.ars.orm.OModel;
import com.ars.orm.types.ColumnType;

public class ResPartner extends OModel {

    OColumn name = new OColumn("Name", ColumnType.VARCHAR);
    OColumn company_type = new OColumn("Company Type", ColumnType.VARCHAR);
    OColumn street = new OColumn("Street", ColumnType.VARCHAR);
    OColumn street2 = new OColumn("Street2", ColumnType.VARCHAR);
    OColumn city = new OColumn("City", ColumnType.VARCHAR);
    OColumn state_id = new OColumn("State", ColumnType.MANY2ONE, "res.state");
    OColumn country_id = new OColumn("Country", ColumnType.MANY2ONE, "res.country");
    OColumn zip = new OColumn("ZIP", ColumnType.VARCHAR);
    OColumn website = new OColumn("Website", ColumnType.VARCHAR);
    OColumn phone = new OColumn("Phone", ColumnType.VARCHAR);
    OColumn mobile = new OColumn("Mobile", ColumnType.VARCHAR);
    OColumn fax = new OColumn("Fax", ColumnType.VARCHAR);
    OColumn email = new OColumn("Email", ColumnType.VARCHAR);
    OColumn image_medium = new OColumn("Image", ColumnType.BLOB);

    OColumn address = new OColumn("Address", ColumnType.VARCHAR);
    OColumn latitude = new OColumn("Latitude", ColumnType.VARCHAR);
    OColumn longitude = new OColumn("Longitude", ColumnType.VARCHAR);//longitude

    OColumn isFavourite = new OColumn("isFavourite", ColumnType.VARCHAR).makeLocal(); // local column


    public ResPartner(Context context) {
        super(context, "res.partner");
    }

    @Override
    public Uri uri() {
        return Uri.parse("content://com.ars.contacts.res_partner/res_partner");
    }
}
