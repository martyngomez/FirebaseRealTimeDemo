package com.martyngomez.firebasedemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.martyngomez.firebasedemo.model.Artist;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String ARTIST_NODE =  "Artists";
    private static final String TAG = "MainActivity" ;
    private DatabaseReference databaseReference;

    private ListView lstArtist;
    private ArrayAdapter arrayAdapter;
    private List<String> artistNames;
    private List<Artist> artists;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lstArtist = (ListView) findViewById(R.id.lstArtist);
        artistNames = new ArrayList<>();
        artists = new ArrayList<>();
        arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, artistNames);
        lstArtist.setAdapter(arrayAdapter);

        FirebaseDatabase.getInstance().setPersistenceEnabled(true); //Cuando esta sin Internet guarda en cache y actualiza cuando se conecta.
        databaseReference = FirebaseDatabase.getInstance().getReference(); //Obtiene la referencia, fir-demo-9946b

        databaseReference.child(ARTIST_NODE).addValueEventListener(new ValueEventListener() { //Listener cambios / Errores
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                artistNames.clear();
                artists.clear();
                if (dataSnapshot.exists()){
                    for (DataSnapshot snapshot:dataSnapshot.getChildren()) { //Trae los datos y recorre
                        Artist artist = snapshot.getValue(Artist.class); //Trae dato puntual del hijo
                        Log.w(TAG, "Artist Name: " + artist.getName());
                        artistNames.add(artist.getName());
                        artists.add(artist);
                    }
                }
                arrayAdapter.notifyDataSetChanged();  //Notifica al adaptador
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        lstArtist.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {  //Listener que capta cuando se presiona un tiempo un item
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long l) {
                String idArtist = artists.get(position).getId();
                artists.remove(position);
                artistNames.remove(position);
                databaseReference.child(ARTIST_NODE).child(idArtist).removeValue(); // Elimina en BD
                return true;
            }
        });

    }

    public void createArtist(View view){
        Artist artist = new Artist(databaseReference.push().getKey(),"Garbage", "Rock"); //Obtiene key y pasa por parametro
        // databaseReference.push().getKey() // Inserta un registro y devuelve Key . Funciona como in id Autoincremental
        databaseReference.child(ARTIST_NODE).child(artist.getId()).setValue(artist); // Establece valor
    }


}
