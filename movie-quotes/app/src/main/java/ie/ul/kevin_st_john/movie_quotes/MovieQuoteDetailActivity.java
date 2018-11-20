package ie.ul.kevin_st_john.movie_quotes;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MovieQuoteDetailActivity extends AppCompatActivity {

    private TextView mQuoteTextView;
    private TextView mMovieTextView;
    private DocumentReference mDocRef;
    private DocumentSnapshot mDocSnapshot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_quote_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mQuoteTextView = findViewById(R.id.detail_quote);
        mMovieTextView = findViewById(R.id.detail_movie);

        //Pull the data out of the received
        Intent receivedIntent = getIntent();
        String docId = receivedIntent.getStringExtra(Constants.EXTRA_DOC_ID);

        //Temp test
        //mQuoteTextView.setText(docId);

        mDocRef = FirebaseFirestore.getInstance().collection(Constants.COLLECTION_PATH).document(docId);
        mDocRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(Constants.TAG, "Listen Failed");
                    return;
                }
                if (documentSnapshot.exists()) {
                    // save the snapshot
                    mDocSnapshot = documentSnapshot;

                    //user can now see it
                    mQuoteTextView.setText((String) documentSnapshot.get(Constants.KEY_QUOTE));
                    mMovieTextView.setText((String) documentSnapshot.get(Constants.KEY_MOVIE));
                }
            }
        });


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showEditDialog();
            }
        });
    }

    private void showEditDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //moviequote_dialog is the new layout XML file you created
        View view = getLayoutInflater().inflate(R.layout.moviequote_dialog, null, false);

        builder.setView(view);
        builder.setTitle("Edit Quote");

        //dialog_quote_edittext is the Edit Text View you created in new XML file moviequote_dialog
        final TextView quoteEditText = view.findViewById(R.id.dialog_quote_edittext);
        final TextView movieEditText = view.findViewById(R.id.dialog_movie_edittext);


        //when we launch dialog prepopulate it with existing text
        quoteEditText.setText((String) mDocSnapshot.get(Constants.KEY_QUOTE));
        movieEditText.setText((String) mDocSnapshot.get(Constants.KEY_MOVIE));

        builder.setNegativeButton(android.R.string.cancel, null);

        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Map<String, Object> mq = new HashMap<>();

                mq.put(Constants.KEY_QUOTE, quoteEditText.getText().toString());
                mq.put(Constants.KEY_MOVIE, quoteEditText.getText().toString());
                mq.put(Constants.KEY_CREATED, new Date());

                // This code creates a new Firebase entry
                //FirebaseFirestore.getInstance().collection(Constants.COLLECTION_PATH).add(mq);

                //Instead we want to edit an existing one
                mDocRef.update(mq);
            }
        });

        builder.create().show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_delete:
                mDocRef.delete();
                finish();

                return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
