package ie.ul.kevin_st_john.movie_quotes;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.view.View;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Document;

import java.util.ArrayList;
import java.util.List;

public class MovieQuoteAdapter extends RecyclerView.Adapter<MovieQuoteAdapter.MovieQuoteViewHolder>{

    private List<DocumentSnapshot> mMovieQuotesSnapshots = new ArrayList<>();

    public MovieQuoteAdapter(){
        CollectionReference moviequotesRef = FirebaseFirestore.getInstance().collection(Constants.COLLECTION_PATH);

        moviequotesRef.orderBy(Constants.KEY_CREATED, Query.Direction.DESCENDING).limit(50).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                if(e!=null){
                    Log.w(Constants.TAG, "Listening failed");
                    return;
                }
                mMovieQuotesSnapshots = documentSnapshots.getDocuments();
                notifyDataSetChanged();
            }
        });
    }


    @NonNull
    @Override
    public MovieQuoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        //boiler plate - never changes
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.moviequotes_itemview, parent, false);
        return new MovieQuoteViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieQuoteViewHolder movieQuoteViewHolder, int i) {
        DocumentSnapshot ds = mMovieQuotesSnapshots.get(i);
        //needs to be cast to a string as get returns a generic object
        String quote = (String)ds.get(Constants.KEY_QUOTE);
        String movie = (String)ds.get(Constants.KEY_MOVIE);
        movieQuoteViewHolder.mQuoteTextView.setText(quote);
        movieQuoteViewHolder.mMovieTextView.setText(movie);

    }

    @Override
    public int getItemCount() {
        return mMovieQuotesSnapshots.size();
    }

    class MovieQuoteViewHolder extends RecyclerView.ViewHolder{
        private TextView mQuoteTextView;
        private TextView mMovieTextView;


        public MovieQuoteViewHolder(@NonNull final View itemView) {
            super(itemView);
            mQuoteTextView = itemView.findViewById(R.id.itemview_quote);
            mMovieTextView = itemView.findViewById(R.id.itemview_movie);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DocumentSnapshot ds = mMovieQuotesSnapshots.get(getAdapterPosition());
                    Context c = itemView.getContext();
                    Intent intent =new Intent(c, MovieQuoteDetailActivity.class);

                    //push data to the 2nd screen
                    // you are passing to the other intent
                    intent.putExtra(Constants.EXTRA_DOC_ID, ds.getId());
                    c.startActivity(intent);
                }
            });

         }
    }
}
