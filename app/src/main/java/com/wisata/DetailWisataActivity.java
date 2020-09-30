package com.wisata;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class DetailWisataActivity extends AppCompatActivity {
    private String nama,alamat,kategori,lat,lang,lattuju,langtuju,deskripsi,petaUrl,navigasiUrl,jarak,key;
    private TextView nam,ala,kat,des,jar;
    private ImageView background;
    private FirebaseDatabase database;
    private DatabaseReference ref;
    private DatabaseReference inputData;
    private FirebaseStorage storage;
    private StorageReference storageRef;

    private StorageReference pathReference;
    private ProgressDialog mDialog;

    private final String MyPREFERENCES = "Wisata" ;
    private SharedPreferences sharedpreferences;
    private FloatingActionButton fab;
    boolean favorit;
    private Button rating;
    private static final String TAG = "SignInActivity";
    private static final int RC_SIGN_IN = 0 ;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private GoogleApiClient mGoogleApiClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_wisata);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        nama = getIntent().getStringExtra("nama");
        alamat = getIntent().getStringExtra("alamat");
        kategori = getIntent().getStringExtra("kategori");
        lat = getIntent().getStringExtra("lat");
        lang = getIntent().getStringExtra("lang");
        lattuju = getIntent().getStringExtra("lattuju");
        langtuju = getIntent().getStringExtra("langtuju");
        deskripsi = getIntent().getStringExtra("deskripsi");
        jarak = getIntent().getStringExtra("jarak");
        key = getIntent().getStringExtra("idWisata");

        mDialog = new ProgressDialog(DetailWisataActivity.this);
        mDialog.setMessage("Please Wait......");
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.show();

        inputData = FirebaseDatabase.getInstance().getReference("rating");
        database = FirebaseDatabase.getInstance();
        ref = database.getReference();

        nam = findViewById(R.id.nama);
        ala = findViewById(R.id.alamat);
        kat = findViewById(R.id.kategori);
        des = findViewById(R.id.deskripsi);
        jar = findViewById(R.id.jarak);
        background = findViewById(R.id.background);
        fab = findViewById(R.id.fab);
        rating = findViewById(R.id.rating);

        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        favorit = sharedpreferences.getBoolean(key.trim(),false);

        if(favorit){
            fab.setImageResource(R.drawable.ic_action_favoritewhite);
        }else{
            fab.setImageResource(R.drawable.ic_action_unfavorite);
        }

        nam.setText(nama);
        ala.setText(alamat);
        kat.setText(kategori);
        des.setText(deskripsi);
        jar.setText(jarak);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

                    }
                } /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                Log.v("hasiil"," "+user);
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };
        rating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });

        petaUrl = "https://maps.google.com/maps?q="+lat+","+lang;
        navigasiUrl = "http://maps.google.com/maps?saddr="+lattuju+","+langtuju+"&daddr="+lat+","+lang;

        ref.child("gambar").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot gambar) {
                for (DataSnapshot childGambar : gambar.getChildren()) {
                    if (childGambar.child("idGambar").getValue().toString().equals(key)) {
                        String image = childGambar.child("nama").getValue().toString();
                        storage = FirebaseStorage.getInstance();
                        storageRef = storage.getReferenceFromUrl("gs://tourism-f5d93.appspot.com");
                        pathReference = storageRef.child(key).child(image);
                        Glide.with(getBaseContext() /* context */)
                                .using(new FirebaseImageLoader())
                                .load(pathReference)
                                .into(background);
                        break;
                    }
                }
                mDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!favorit){
                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.putBoolean(key.trim(), true);
                    favorit = true;
                    editor.apply();
                    fab.setImageResource(R.drawable.ic_action_favoritewhite);
                    Snackbar.make(view, "Ditambahkan Kedalam Wisata Favorite", Snackbar.LENGTH_SHORT)
                            .setAction("Action", null).show();
                }else{
                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.putBoolean(key.trim(), false);
                    editor.apply();
                    fab.setImageResource(R.drawable.ic_action_unfavorite);
                    favorit = false;
                    Snackbar.make(view, "Dihapus Dari Wisata Favorite", Snackbar.LENGTH_SHORT)
                            .setAction("Action", null).show();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_detail_wisata, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.share) {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, petaUrl);
            sendIntent.setType("text/plain");
            startActivity(sendIntent);
        }
        else if(id == R.id.peta){
            Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(petaUrl));
            startActivity(appIntent);
        }
        else if(id == R.id.navigasi){
            Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(navigasiUrl));
            startActivity(appIntent);
        }
        return super.onOptionsItemSelected(item);
    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        mDialog.show();
        Log.v("hasil","click button");
        rating.setEnabled(false);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {
                Log.v("hasil"," "+result.getSignInAccount());
            }
        }
    }
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithCredential", task.getException());
                            Toast.makeText(DetailWisataActivity.this, "Authentication failed. : "+task.getException(),
                                    Toast.LENGTH_SHORT).show();
                            rating.setEnabled(false);
                        }else{
                            mDialog.dismiss();
                            final FirebaseUser currentUser = mAuth.getCurrentUser();
                            final Dialog rankDialog = new Dialog(DetailWisataActivity.this, R.style.FullHeightDialog);
                            rankDialog.setContentView(R.layout.rating_dialog);
                            rankDialog.setCancelable(true);
                            final RatingBar ratingBar = rankDialog.findViewById(R.id.dialog_ratingbar);
                            ratingBar.setRating(5);

                            TextView text = rankDialog.findViewById(R.id.rank_dialog_text1);
                            text.setText(nama);

                            Button updateButton = rankDialog.findViewById(R.id.rank_dialog_button);
                            updateButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(final View v) {
                                    inputData.child(key).child("review").child(currentUser.getUid()).child("rating").setValue(ratingBar.getRating()).addOnSuccessListener(DetailWisataActivity.this, new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            ref.child("rating").child(key).child("review").addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot review) {
                                                    float ratingTotal=0;
                                                    for(DataSnapshot rate : review.getChildren()){
                                                        for(DataSnapshot rating : rate.getChildren()){
                                                            float rateawal = Float.parseFloat(rating.getValue().toString());
                                                            ratingTotal = ratingTotal + rateawal;
                                                        }
                                                    }

                                                    double persen = ratingTotal/review.getChildrenCount();
                                                    inputData.child(key).child("ratingTotal").setValue(persen).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            Toast.makeText(getBaseContext(),"Berhasil memberi rating",Toast.LENGTH_SHORT).show();
                                                            rating.setEnabled(true);
                                                        }
                                                    });
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                }
                                            });
                                        }
                                    });
                                    rankDialog.dismiss();
                                }
                            });
                            //now that the dialog is set up, it's time to show it
                            rankDialog.show();
                            rating.setEnabled(false);
                        }
                    }
                });
    }
    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            updateUI(currentUser.getUid());
        }
        mAuth.addAuthStateListener(mAuthListener);
    }
    private void updateUI(String uid){
        Log.v("hasil","click button");
        //Toast.makeText(getBaseContext(),"Sudah Login dengan UID : "+uid, Toast.LENGTH_SHORT).show();
    }
}
