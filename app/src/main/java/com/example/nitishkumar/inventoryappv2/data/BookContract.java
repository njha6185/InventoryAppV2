package com.example.nitishkumar.inventoryappv2.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public final class BookContract {

    private BookContract() {}

    public static final String CONTENT_AUTHORITY = "com.example.nitishkumar.inventoryappv2";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_BOOKS = "Books";

    public static final class BookEntry implements BaseColumns{

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_BOOKS);

        public static final String CONTENT_LIST_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" +
                CONTENT_AUTHORITY + "/" + PATH_BOOKS;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" +
                CONTENT_AUTHORITY + "/" + PATH_BOOKS;

        public final static String TABLE_NAME = "Books";

        public final static String _ID = BaseColumns._ID;
        public final static String COLUMN_BOOK_NAME = "Books_Name";
        public final static String COLUMN_BOOK_CATEGORY = "Books_Category";
        public final static String COLUMN_BOOK_PRICE = "Books_Price";
        public final static String COLUMN_BOOK_QUANTIT = "Books_Quantity";
        public final static String COLUMN_BOOK_SUPPLIER_NAME = "Supplier_Name";
        public final static String COLUMN_BOOK_SUPPLIER_NO = "Supplier_No";
        public final static String COLUMN_BOOK_SUPPLIER_EMAIL = "Supplier_Email";
    }
}