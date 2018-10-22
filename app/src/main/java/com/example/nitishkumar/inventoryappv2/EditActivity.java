package com.example.nitishkumar.inventoryappv2;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.NavUtils;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import static com.example.nitishkumar.inventoryappv2.data.BookContract.*;

public class EditActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int EXISTING_BOOK_LOADER = 0;
    private Uri mCurrentBookUri;
    private Menu menu;
    private EditText bookNameEditTextViwew;
    private EditText bookCategoryEditTextViwew;
    private EditText bookPriceEditTextViwew;
    private EditText bookQuantityEditTextViwew;
    private EditText bookSupplierNameEditTextViwew;
    private EditText bookSupplierNoEditTextViwew;
    private EditText bookSupplierEmailEditTextViwew;
    private Button callSupplierButton;
    private boolean editModeON;
    private Button qtyIncrement;
    private Button qtyDecrement;
    private int qty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        mCurrentBookUri = intent.getData();
        init();

        if (mCurrentBookUri == null)
        {
            setTitle(getString(R.string.add_book_title));
            convertEditTextToTextView(true);
            bookQuantityEditTextViwew.setText(Integer.toString(qty));
            invalidateOptionsMenu();
            editModeON = true;
        }
        else
        {

            setTitle(getString(R.string.details));
            convertEditTextToTextView(false);
            invalidateOptionsMenu();
            getSupportLoaderManager().initLoader(EXISTING_BOOK_LOADER, null, this);
        }

        callSupplierButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                String phoneNo = bookSupplierNoEditTextViwew.getText().toString().trim();
                phoneNo = "tel:" + phoneNo;
                callIntent.setData(Uri.parse(phoneNo));
                startActivity(callIntent);
            }
        });

        qtyIncrement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String qtyStr = bookQuantityEditTextViwew.getText().toString().trim();
                qty = 0;
                if (!TextUtils.isEmpty(qtyStr))
                {
                    qty = Integer.parseInt(qtyStr);
                }
                qty++;
                bookQuantityEditTextViwew.setText(Integer.toString(qty));
            }
        });

        qtyDecrement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String qtyStr = bookQuantityEditTextViwew.getText().toString().trim();
                qty = 0;
                if (!TextUtils.isEmpty(qtyStr))
                {
                    qty = Integer.parseInt(qtyStr);
                }

                if (qty == 0)
                {
                    return;
                }
                else if (qty > 0)
                {
                    qty--;
                    bookQuantityEditTextViwew.setText(Integer.toString(qty));
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.edit_actionbar_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (mCurrentBookUri == null)
        {
            menu.findItem(R.id.deleteDetail).setVisible(false);
            menu.findItem(R.id.editDetail).setVisible(false);
            menu.findItem(R.id.saveDetail).setVisible(true);
            callSupplierButton.setVisibility(View.GONE);
            qtyDecrement.setVisibility(View.VISIBLE);
            qtyIncrement.setVisibility(View.VISIBLE);
        }
        else
        {
            menu.findItem(R.id.deleteDetail).setVisible(true);
            menu.findItem(R.id.editDetail).setVisible(true);
            menu.findItem(R.id.saveDetail).setVisible(false);
            callSupplierButton.setVisibility(View.VISIBLE);
            qtyDecrement.setVisibility(View.GONE);
            qtyIncrement.setVisibility(View.GONE);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.saveDetail :
                savePet();
                finish();
                return true;

            case R.id.editDetail:
                editModeON = true;
                convertEditTextToTextView(true);
                setTitle(getString(R.string.edit));
                menu.findItem(R.id.editDetail).setVisible(false);
                menu.findItem(R.id.deleteDetail).setVisible(false);
                menu.findItem(R.id.saveDetail).setVisible(true);
                callSupplierButton.setVisibility(View.GONE);
                qtyDecrement.setVisibility(View.VISIBLE);
                qtyIncrement.setVisibility(View.VISIBLE);
                Toast.makeText(this, R.string.edit_mode_notification , Toast.LENGTH_SHORT).show();
                return true;

            case R.id.deleteDetail:
                showDeleteConfirmationDialogur();
                return true;

            case android.R.id.home:
                if (editModeON)
                {
                    DialogInterface.OnClickListener discardButtonClickListener =
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    NavUtils.navigateUpFromSameTask(EditActivity.this);
                                }
                            };
                    showUnsavedChangesDialogue(discardButtonClickListener);
                    return true;
                }
                else
                {
                    NavUtils.navigateUpFromSameTask(EditActivity.this);
                    return true;
                }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (editModeON)
        {
            DialogInterface.OnClickListener discardButtonClickListener =
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            finish();
                        }
                    };
            showUnsavedChangesDialogue(discardButtonClickListener);
        }
        else
        {
            super.onBackPressed();
            return;
        }
    }

    private void showUnsavedChangesDialogue(DialogInterface.OnClickListener discardButtonClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this) ;
        builder.setMessage(R.string.unsaved_alert_notify);
        builder.setPositiveButton(getString(R.string.discard), discardButtonClickListener);
        builder.setNegativeButton(getString(R.string.keep_editing), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (dialogInterface != null)
                {
                    dialogInterface.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void savePet()
    {
        String bookName = bookNameEditTextViwew.getText().toString().trim();
        String bookCategory = bookCategoryEditTextViwew.getText().toString().trim();
        String priceString = bookPriceEditTextViwew.getText().toString().trim();
        String quantityString = bookQuantityEditTextViwew.getText().toString().trim();
        String supplierName = bookSupplierNameEditTextViwew.getText().toString().trim();
        String supplierNo = bookSupplierNoEditTextViwew.getText().toString().trim();
        String supplierEmail = bookSupplierEmailEditTextViwew.getText().toString().trim();

        if (mCurrentBookUri == null && ( TextUtils.isEmpty(bookName) || TextUtils.isEmpty(bookCategory)
                || TextUtils.isEmpty(priceString) || TextUtils.isEmpty(quantityString) || TextUtils.isEmpty(supplierName)
                || TextUtils.isEmpty(supplierNo) || TextUtils.isEmpty(supplierEmail)) )
        {
            Toast.makeText(this, getString(R.string.incomplete_form_notify), Toast.LENGTH_SHORT).show();
            return;
        }

        int bookPrice = 0;
        int bookQuantity = 0;

        if (!TextUtils.isEmpty(priceString) && !TextUtils.isEmpty(quantityString))
        {
            bookPrice = Integer.parseInt(priceString);
            bookQuantity = Integer.parseInt(quantityString);
        }
        else
        {
            Toast.makeText(this, getString(R.string.incomplete_form_notify), Toast.LENGTH_SHORT).show();
            return;
        }

        ContentValues values = new ContentValues();
        values.put(BookEntry.COLUMN_BOOK_NAME, bookName);
        values.put(BookEntry.COLUMN_BOOK_CATEGORY, bookCategory);
        values.put(BookEntry.COLUMN_BOOK_PRICE, bookPrice);
        values.put(BookEntry.COLUMN_BOOK_QUANTIT, bookQuantity);
        values.put(BookEntry.COLUMN_BOOK_SUPPLIER_NAME, supplierName);
        values.put(BookEntry.COLUMN_BOOK_SUPPLIER_NO, supplierNo);
        values.put(BookEntry.COLUMN_BOOK_SUPPLIER_EMAIL, supplierEmail);

        if (mCurrentBookUri == null)
        {
            Uri newUri = getContentResolver().insert(BookEntry.CONTENT_URI, values);

            if (newUri == null)
            {
                Toast.makeText(this, R.string.insertion_failed, Toast.LENGTH_SHORT).show();
            }
            else
            {
                Toast.makeText(this, R.string.insertion_successfully, Toast.LENGTH_SHORT).show();
            }
        }
        else
        {
            int rowsAffected = getContentResolver().update(mCurrentBookUri, values, null, null);
            if (rowsAffected == 0)
            {
                Toast.makeText(this, getString(R.string.updation_failure_notify), Toast.LENGTH_SHORT).show();
            }
            else
            {
                Toast.makeText(this, getString(R.string.update_success_notify), Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void convertEditTextToTextView(boolean show)
    {
        bookNameEditTextViwew.setLongClickable(show);           bookNameEditTextViwew.setFocusable(show);
        bookNameEditTextViwew.setFocusableInTouchMode(show);

        bookCategoryEditTextViwew.setLongClickable(show);       bookCategoryEditTextViwew.setFocusable(show);
        bookCategoryEditTextViwew.setFocusableInTouchMode(show);

        bookPriceEditTextViwew.setLongClickable(show);          bookPriceEditTextViwew.setFocusable(show);
        bookPriceEditTextViwew.setFocusableInTouchMode(show);

        bookQuantityEditTextViwew.setLongClickable(show);       bookQuantityEditTextViwew.setFocusable(show);
        bookQuantityEditTextViwew.setFocusableInTouchMode(show);

        bookSupplierNameEditTextViwew.setLongClickable(show);   bookSupplierNameEditTextViwew.setFocusable(show);
        bookSupplierNameEditTextViwew.setFocusableInTouchMode(show);

        bookSupplierNoEditTextViwew.setLongClickable(show);     bookSupplierNoEditTextViwew.setFocusable(show);
        bookSupplierNoEditTextViwew.setFocusableInTouchMode(show);

        bookSupplierEmailEditTextViwew.setLongClickable(show);  bookSupplierEmailEditTextViwew.setFocusable(show);
        bookSupplierEmailEditTextViwew.setFocusableInTouchMode(show);
    }

    private void init() {
        bookNameEditTextViwew = (EditText)findViewById(R.id.bookNameTextView);
        bookCategoryEditTextViwew = (EditText)findViewById(R.id.bookCategoryTextView);
        bookPriceEditTextViwew = (EditText)findViewById(R.id.bookPrice);
        bookQuantityEditTextViwew = (EditText)findViewById(R.id.bookQuantity);
        bookSupplierNameEditTextViwew = (EditText)findViewById(R.id.bookSupplierNameTextView);
        bookSupplierNoEditTextViwew = (EditText)findViewById(R.id.bookSupplierNoTextView);
        bookSupplierEmailEditTextViwew = (EditText)findViewById(R.id.bookSupplierEmailTextView);
        callSupplierButton = (Button)findViewById(R.id.callButton);
        qtyIncrement = (Button)findViewById(R.id.qtyIncrementButton);
        qtyDecrement = (Button)findViewById(R.id.qtyDecrementButton);
        editModeON = false;
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        String[] projection = {
                BookEntry._ID,
                BookEntry.COLUMN_BOOK_NAME,
                BookEntry.COLUMN_BOOK_PRICE,
                BookEntry.COLUMN_BOOK_CATEGORY,
                BookEntry.COLUMN_BOOK_QUANTIT,
                BookEntry.COLUMN_BOOK_SUPPLIER_NAME,
                BookEntry.COLUMN_BOOK_SUPPLIER_NO,
                BookEntry.COLUMN_BOOK_SUPPLIER_EMAIL
        };

        return new CursorLoader(this,
                mCurrentBookUri,
                projection,
                null, null, null);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {
        if (cursor == null || cursor.getCount() < 1)
        {
            return;
        }
        if (cursor.moveToFirst())
        {
            int nameColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_NAME);
            int categoryColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_CATEGORY);
            int priceColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_QUANTIT);
            int supplierNameColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_SUPPLIER_NAME);
            int supplierNoColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_SUPPLIER_NO);
            int supplierEmailColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_SUPPLIER_EMAIL);

            String name = cursor.getString(nameColumnIndex);
            String category = cursor.getString(categoryColumnIndex);
            int price = cursor.getInt(priceColumnIndex);
            int quantity = cursor.getInt(quantityColumnIndex);
            String supplierName = cursor.getString(supplierNameColumnIndex);
            String supplierNo = cursor.getString(supplierNoColumnIndex);
            String supplierEmail = cursor.getString(supplierEmailColumnIndex);

            bookNameEditTextViwew.setText(name);
            bookCategoryEditTextViwew.setText(category);
            bookPriceEditTextViwew.setText(Integer.toString(price));
            bookQuantityEditTextViwew.setText(Integer.toString(quantity));
            bookSupplierNameEditTextViwew.setText(supplierName);
            bookSupplierNoEditTextViwew.setText(supplierNo);
            bookSupplierEmailEditTextViwew.setText(supplierEmail);
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        bookNameEditTextViwew.setText("");
        bookCategoryEditTextViwew.setText("");
        bookPriceEditTextViwew.setText("");
        bookQuantityEditTextViwew.setText("");
        bookSupplierNameEditTextViwew.setText("");
        bookSupplierNoEditTextViwew.setText("");
        bookSupplierEmailEditTextViwew.setText("");
    }

    public void showDeleteConfirmationDialogur()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.confirm_delete_alert));
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                deleteBook();
            }
        });

        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (dialogInterface != null)
                {
                    dialogInterface.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void deleteBook()
    {
        if (mCurrentBookUri != null)
        {
            int rowDeleted = getContentResolver().delete(mCurrentBookUri, null, null);
            if (rowDeleted == 0)
            {
                Toast.makeText(this, getString(R.string.delete_failed_notify), Toast.LENGTH_SHORT).show();
            }
            else
            {
                Toast.makeText(this, getString(R.string.delete_success_notify), Toast.LENGTH_SHORT).show();
            }
        }
        finish();
    }
}