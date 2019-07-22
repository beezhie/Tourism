package com.wisata.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Rect;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

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
import com.wisata.adapters.WisataAdapter;
import com.wisata.models.Wisata;

import java.util.ArrayList;
import java.util.List;

public class WisataFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private RecyclerView recyclerView;
    private WisataAdapter adapter;
    private List<Wisata> wisataList;
    private FirebaseDatabase database;
    private DatabaseReference ref;
    private FirebaseStorage storage;
    private StorageReference storageRef;
    private StorageReference pathReference;
    private ProgressDialog mDialog;

    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public WisataFragment() {}

    public static WisataFragment newInstance(String param1, String param2) {
        WisataFragment fragment = new WisataFragment();
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
        View view = inflater.inflate(R.layout.fragment_wisata, container, false);
        wisataList = new ArrayList<>();
        adapter = new WisataAdapter(getActivity(), wisataList);
        recyclerView = view.findViewById(R.id.wisata);

        mDialog = new ProgressDialog(getContext());
        mDialog.setMessage("Please Wait......");
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.show();

        final RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(),2);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(10), true));
        recyclerView.setAdapter(adapter);

        database = FirebaseDatabase.getInstance();
        ref = database.getReference();

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getContext(), recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Wisata tempat = wisataList.get(position);
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
        /*final SwipeRefreshLayout refreshLayout = view.findViewById(R.id.refresh);
        refreshLayout.setColorScheme(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Toast.makeText(getActivity(), "Load data...", Toast.LENGTH_SHORT).show();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        refreshLayout.setRefreshing(false);
                        if(wisataList.size() != 0){
                            wisataList.clear();
                        }
                        myLocation();
                    }
                }, 3000);
            }
        });*/

        myLocation();
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
    private void ambilData(final double latTuju, final double langTuju){
        ref.child("wisata").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot wisata) {
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
                                            wisataList.add(data);
                                            adapter.notifyDataSetChanged();
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
                    mDialog.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // item bottom
            } else {
                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1);
                if (position >= spanCount) {
                    outRect.top = spacing;
                }
            }
        }
    }

    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }
    private void myLocation(){
        if (ActivityCompat.checkSelfPermission(getContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getContext(),
                        android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
           requestPermissions(
                    new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION,
                            android.Manifest.permission.ACCESS_FINE_LOCATION},
                    1);
           mDialog.dismiss();
        } else {
            LocationRequest request = new LocationRequest();
            request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            FusedLocationProviderClient client = LocationServices.getFusedLocationProviderClient(getContext());

            client.requestLocationUpdates(request, new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {

                    Location location = locationResult.getLastLocation();
                    if (location != null) {
                        ambilData(location.getLatitude(),location.getLongitude());
                    }
                }
            }, null);
        }
    }
    private float getDistancBetweenTwoPoints(double lat1,double lon1,double lat2,double lon2) {

        float[] distance = new float[2];

        Location.distanceBetween( lat1, lon1,
                lat2, lon2, distance);

        return distance[0]/1000;
    }
}
