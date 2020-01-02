package com.tenke.call;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;

import com.tenke.lib_common.ApplicationContextLink;

import java.util.ArrayList;
import java.util.List;

public class CallManager {

    private final Context mContext;
    private final List<Contact> mContacts;

    private CallManager(){
        mContext = ApplicationContextLink.LinkToApplication();
        mContacts = readContacts();
    }

    private static final class Holder{
        private static final CallManager INSTANCE = new CallManager();
    }

    public static CallManager getInstance(){
        return Holder.INSTANCE;
    }

    public boolean callByName(String name){
        for(Contact contact : mContacts){
            if(contact.getName().equals(name)){
                call(contact.getPhoneNum());
                return true;
            }
        }
        return false;
    }


    public void call(String phoneNum){
        Intent intent = new Intent(Intent.ACTION_CALL);
        Uri uri = Uri.parse("tel:" + phoneNum);
        intent.setData(uri);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(intent);
    }

    private List<Contact> readContacts(){
        List<Contact> contacts = new ArrayList<>();
        Cursor cursor = mContext.getContentResolver().query(
                ContactsContract.Contacts.CONTENT_URI,
                null,
                null,
                null,
                null);
        while (cursor.moveToNext()){
            String name  = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
            String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
            Cursor phoneCurser = mContext.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    null,
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "= "+id,
                    null,
                    null);
            while (phoneCurser.moveToNext()) {
                String phoneNum = phoneCurser.getString(phoneCurser.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
//                phoneNum.replace("-", "");
//                phoneNum.replace(" ", "");
                Contact contact = new Contact(name,phoneNum);
                contacts.add(contact);
            }
        }
        return contacts;
    }

}
