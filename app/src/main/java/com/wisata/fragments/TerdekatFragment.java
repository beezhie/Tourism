package com.wisata.fragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.*;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.wisata.DetailWisataActivity;
import com.wisata.R;
import com.wisata.RecyclerTouchListener;
import com.wisata.adapters.*;
import com.wisata.models.*;
import java.util.ArrayList;

import javax.crypto.KeyAgreement;

public class TerdekatFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private RecyclerView recyclerView;
    private TerdekatAdapter adapter;
    private List<Wisata> terdekatList;
    private FirebaseDatabase database;
    private DatabaseReference ref;
    private List<Wisata> cekBanding;
    private ProgressDialog mDialog;
    private FirebaseStorage storage;
    private StorageReference storageRef;
    private StorageReference pathReference;

    ArrayAdapter kat;
    private List<String> panjang;
    private List<String> urut;

    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public TerdekatFragment() {}

    public static TerdekatFragment newInstance(String param1, String param2) {
        TerdekatFragment fragment = new TerdekatFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_terdekat, container, false);
        terdekatList = new ArrayList<>();
        cekBanding = new ArrayList<>();
        recyclerView = view.findViewById(R.id.terdekat);
        mDialog = new ProgressDialog(getContext());
        mDialog.setMessage("Please Wait......");
        mDialog.setCanceledOnTouchOutside(false);

        adapter = new TerdekatAdapter(terdekatList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        database = FirebaseDatabase.getInstance();
        ref = database.getReference();
        FloatingActionButton fab = view.findViewById(R.id.urut);
        fab.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
                ambilKategori();
           }
       });
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getContext(), recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Wisata tempat = terdekatList.get(position);
                Intent intent = new Intent(getActivity(),DetailWisataActivity.class);
                intent.putExtra("idWisata",tempat.getIdWisata());
                intent.putExtra("nama",tempat.getNama());
                intent.putExtra("alamat",tempat.getAlamat());
                intent.putExtra("deskripsi",tempat.getDeskripsi());
                intent.putExtra("kategori",tempat.getIdKategori());
                intent.putExtra("lat",tempat.getLat());
                intent.putExtra("lang",tempat.getLang());
                intent.putExtra("lattuju",tempat.getLattuju());
                intent.putExtra("langtuju",tempat.getLangtuju());
                intent.putExtra("jarak",String.valueOf(tempat.getJarak()));
                startActivity(intent);
                Toast.makeText(getContext(), tempat.getNama() + " is selected!", Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        myLocation("Semua");
        return view;
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    private void ambilData(final String idKategori, final double latTuju, final double langTuju){
        if(!idKategori.equals("Semua")){
            panjang = new ArrayList<>();
            cekBanding = new ArrayList<>();
            ref.child("wisata").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull final DataSnapshot wisata) {
                    String key;
                    String nama;
                    String alamat;
                    String deskripsi;
                    String kategori;
                    String lat;
                    String lang;
                    for(DataSnapshot childWisata : wisata.getChildren()){
                        Wisata hasil = childWisata.getValue(Wisata.class);
                        key = childWisata.getKey();
                        nama = hasil.getNama();
                        alamat = hasil.getAlamat();
                        deskripsi = hasil.getDeskripsi();
                        kategori = hasil.getIdKategori();
                        lat = hasil.getLat();
                        lang = hasil.getLang();

                        if(kategori.equals(idKategori)){
                            Log.v("hasil","ketemu");
                            final String finalKey = key;
                            final String finalNama = nama;
                            final String finalAlamat = alamat;
                            final String finalDeskripsi = deskripsi;
                            final String finalLat = lat;
                            final String finalLang = lang;
                            panjang.add(key);
                            ref.child("kategori").child(idKategori).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull final DataSnapshot Kategori) {
                                    final String kategori = Kategori.child("nama").getValue().toString();
                                    ref.child("gambar").addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot gambar) {
                                            for(DataSnapshot childGambar : gambar.getChildren()){
                                                if(childGambar.child("idGambar").getValue().toString().equals(finalKey)){
                                                    String image = childGambar.child("nama").getValue().toString();
                                                    Log.v("hasil","ketemu " +image);
                                                    storage = FirebaseStorage.getInstance();
                                                    storageRef = storage.getReferenceFromUrl("gs://tourism-f5d93.appspot.com");
                                                    pathReference = storageRef.child(finalKey).child(image);
                                                    int jarak = (int)getDistancBetweenTwoPoints(Double.parseDouble(finalLat),Double.parseDouble(finalLang),latTuju,langTuju);
                                                    Wisata data = new Wisata(finalKey, finalNama,finalAlamat,finalDeskripsi,kategori,pathReference,finalLat,finalLang,jarak,String.valueOf(latTuju),String.valueOf(langTuju));
                                                    cekBanding.add(data);
                                                    if(cekBanding.size() == panjang.size()){
                                                        Collections.sort(cekBanding,Wisata.jarakComparator);
                                                        for(Wisata str : cekBanding){
                                                            terdekatList.add(str);
                                                            adapter.notifyDataSetChanged();
                                                        }
                                                    }
                                                    break;
                                                }
                                            }
                                        }
                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }
                    }
                    mDialog.dismiss();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }else{
            ref.child("wisata").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull final DataSnapshot wisata) {
                    String key;
                    String nama;
                    String alamat;
                    String deskripsi;
                    String kategori;
                    String lat;
                    String lang;
                    for(DataSnapshot childWisata : wisata.getChildren()){
                        Wisata hasil = childWisata.getValue(Wisata.class);
                        key = childWisata.getKey();
                        nama = hasil.getNama();
                        alamat = hasil.getAlamat();
                        deskripsi = hasil.getDeskripsi();
                        kategori = hasil.getIdKategori();
                        lat = hasil.getLat();
                        lang = hasil.getLang();

                        final String finalKey = key;
                        final String finalNama = nama;
                        final String finalAlamat = alamat;
                        final String finalDeskripsi = deskripsi;
                        final String finalKategoti = kategori;
                        final String finalLat = lat;
                        final String finalLang = lang;
                        ref.child("kategori").child(finalKategoti).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                final String kategori = dataSnapshot.child("nama").getValue().toString();
                                ref.child("gambar").addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot gambar) {
                                        for(DataSnapshot childGambar : gambar.getChildren()){
                                            if(childGambar.child("idGambar").getValue().toString().equals(finalKey)){
                                                String image = childGambar.child("nama").getValue().toString();
                                                storage = FirebaseStorage.getInstance();
                                                storageRef = storage.getReferenceFromUrl("gs://tourism-f5d93.appspot.com");
                                                pathReference = storageRef.child(finalKey).child(image);
                                                int jarak = (int)getDistancBetweenTwoPoints(Double.parseDouble(finalLat),Double.parseDouble(finalLang),latTuju,langTuju);
                                                Wisata data = new Wisata(finalKey, finalNama,finalAlamat,finalDeskripsi,kategori,pathReference,finalLat,finalLang,jarak,String.valueOf(latTuju),String.valueOf(langTuju));
                                                cekBanding.add(data);
                                                if(cekBanding.size() == wisata.getChildrenCount()){
                                                    Collections.sort(cekBanding,Wisata.jarakComparator);
                                                    for(Wisata str : cekBanding){
                                                        terdekatList.add(str);
                                                        adapter.notifyDataSetChanged();
                                                    }
                                                }

                                                break;
                                            }
                                        }
                                    }
                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }

                    mDialog.dismiss();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }
    private float getDistancBetweenTwoPoints(double lat1,double lon1,double lat2,double lon2) {

        float[] distance = new float[2];

        Location.distanceBetween( lat1, lon1,
                lat2, lon2, distance);

        return distance[0]/1000;
    }
    private void myLocation(final String idKategori){
        mDialog.show();
        if (ActivityCompat.checkSelfPermission(getContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getContext(),
                        android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(
                    new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION,
                            android.Manifest.permission.ACCESS_FINE_LOCATION},
                    1);
        } else {
            LocationRequest request = new LocationRequest();
            request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            FusedLocationProviderClient client = LocationServices.getFusedLocationProviderClient(getContext());

            client.requestLocationUpdates(request, new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {

                    Location location = locationResult.getLastLocation();
                    if (location != null) {
                        ambilData(idKategori,location.getLatitude(),location.getLongitude());
                    }
                }
            }, null);
        }
    }

    private void ambilKategori(){
        mDialog.show();
        urut = new ArrayList<String>();
        urut.add("Semua");
        ref.child("kategori").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot x : dataSnapshot.getChildren()){
                    String nama = x.child("nama").getValue().toString();
                    urut.add(nama);
                }
                kat = new ArrayAdapter<String>(getContext(),android.R.layout.simple_list_item_2,urut);
                showDialog();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void showDialog(){
        mDialog.dismiss();
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Pilih Kategori");
        builder.setSingleChoiceItems(urut.toArray(new String[urut.size()]), -1, null);
        builder.setPositiveButton("Terapkan",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        final int select = ((AlertDialog)dialog).getListView().getCheckedItemPosition();
                        terdekatList.clear();
                        cekBanding.clear();
                        adapter.notifyDataSetChanged();
                        Toast.makeText(getContext(),"Cari Berdasarkan Kategori : " + urut.get(select), Toast.LENGTH_SHORT).show();
                        if(urut.get(select).equals("Semua")){
                            myLocation("Semua");
                        }else {
                            ref.child("kategori").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot kategori) {
                                    for(DataSnapshot kat : kategori.getChildren()){
                                        if(kat.child("nama").getValue().toString().equals(urut.get(select))){
                                            myLocation(kat.child("idKategori").getValue().toString());
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }
                    }
                });
        AlertDialog alertdialog = builder.create();
        alertdialog.show();
    }
}
