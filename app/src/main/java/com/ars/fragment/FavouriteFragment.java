package com.ars.fragment;


import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.ars.R;
import com.ars.activity.ContactDetailActivity;
import com.ars.orm.ListRow;
import com.ars.orm.OListAdapter;
import com.ars.table.RecentContact;
import com.ars.table.ResPartner;
import com.ars.utils.BitmapUtils;


/**
 * A simple {@link Fragment} subclass.
 */
public class FavouriteFragment extends Fragment implements OListAdapter.OnViewBindListener,
        LoaderManager.LoaderCallbacks<Cursor>, AdapterView.OnItemClickListener {

    private ResPartner resPartner;
    private OListAdapter oListAdapter;
    private ListView favContactList;
    private RecentContact recentContact;

    public FavouriteFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_favourite, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        resPartner = new ResPartner(getContext());
        recentContact = new RecentContact(getContext());
        favContactList = (ListView) view.findViewById(R.id.favContactList);
        oListAdapter = new OListAdapter(getContext(), null, R.layout.favourite_list_item);
        oListAdapter.setOnViewBindListener(this);
        favContactList.setAdapter(oListAdapter);
        favContactList.setOnItemClickListener(this);
        getLoaderManager().initLoader(0, null, this);

    }

    @Override
    public void onViewBind(View view, Cursor cursor, ListRow row) {
        TextView textContactName, textContactEmail, textContactCity, textContactNumber;
        ImageView profileImage, isCompany;

        textContactName = (TextView) view.findViewById(R.id.textViewName);
        textContactEmail = (TextView) view.findViewById(R.id.textViewEmail);
        textContactCity = (TextView) view.findViewById(R.id.textViewCity);
        textContactNumber = (TextView) view.findViewById(R.id.textViewContact);
        profileImage = (ImageView) view.findViewById(R.id.profile_image);
        isCompany = (ImageView) view.findViewById(R.id.isCompany);

        String stringName, stringEmail, stringCity, stringMobile, stringImage, stringCompanyType;

        stringName = row.getString("name");
        stringEmail = row.getString("email");
        stringCity = row.getString("city");
        stringMobile = row.getString("mobile");
        stringImage = row.getString("image_medium");
        stringCompanyType = row.getString("company_type");

        textContactName.setText(stringName);
        textContactEmail.setText(stringEmail);
        textContactEmail.setVisibility(stringEmail.equals("false") ? View.GONE : View.VISIBLE);

        textContactCity.setText(stringCity);
        textContactCity.setVisibility(stringCity.equals("false") ? View.GONE : View.VISIBLE);

        textContactNumber.setText(stringMobile);
        textContactNumber.setVisibility(stringMobile.equals("false") ? View.GONE : View.VISIBLE);

        isCompany.setVisibility(stringCompanyType.equals("person") ? View.GONE : View.VISIBLE);

        if (stringImage.equals("false")) {
            profileImage.setImageBitmap(BitmapUtils.getAlphabetImage(getContext(), stringName));
        } else {
            profileImage.setImageBitmap(BitmapUtils.getBitmapImage(getContext(),
                    stringImage));
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri uri = Uri.parse("content://com.ars.contacts.res_partner/res_partner");
        return new CursorLoader(getContext(), uri, null, "isFavourite = ? ", new String[]{"true"}, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        oListAdapter.changeCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        oListAdapter.changeCursor(null);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Cursor cr = (Cursor) oListAdapter.getItem(position);

        Intent intent = new Intent(getActivity(), ContactDetailActivity.class);
        intent.putExtra("id", cr.getInt(cr.getColumnIndex("_id")));
        ContentValues values = new ContentValues();
        values.put("contact_id", cr.getInt(cr.getColumnIndex("_id")));
        recentContact.update_or_create(values, "contact_id = ? ", cr.getInt(cr.getColumnIndex("_id")) + "");
        getContext().getContentResolver().notifyChange(resPartner.uri(), null);
        startActivity(intent);
    }
}
