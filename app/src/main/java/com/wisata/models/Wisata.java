package com.wisata.models;


import com.google.firebase.storage.StorageReference;

import java.util.Comparator;

public class Wisata{
    String idWisata;
    String nama;
    String alamat;
    String deskripsi;
    String idKategori;
    StorageReference gambar;
    String lat;
    String lang;
    String lattuju;
    String langtuju;
    int jarak;
    int rating;
    int rekomendasi;

    public Wisata(){}

    public Wisata(String nama, String idKategori) {
        this.nama = nama;
        this.idKategori = idKategori;
    }

    public Wisata(String idWisata, String nama, String alamat, String deskripsi, String idKategori, StorageReference gambar, String lat, String lang, int jarak, String lattuju, String langtuju) {
        this.idWisata = idWisata;
        this.nama = nama;
        this.alamat = alamat;
        this.deskripsi = deskripsi;
        this.idKategori = idKategori;
        this.gambar = gambar;
        this.lat = lat;
        this.lang = lang;
        this.jarak = jarak;
        this.lattuju = lattuju;
        this.langtuju = langtuju;
    }
    public Wisata(String idWisata, String nama, String alamat, String deskripsi, String idKategori, StorageReference gambar, String lat, String lang,int jarak,String lattuju,String langtuju,int rating) {
        this.idWisata = idWisata;
        this.nama = nama;
        this.alamat = alamat;
        this.deskripsi = deskripsi;
        this.idKategori = idKategori;
        this.gambar = gambar;
        this.lat = lat;
        this.lang = lang;
        this.jarak = jarak;
        this.lattuju = lattuju;
        this.langtuju = langtuju;
        this.rating = rating;
    }

    public Wisata(String idWisata, String nama, String alamat, String deskripsi, String idKategori, StorageReference gambar, String lat, String lang,int jarak,String lattuju,String langtuju,int rating,int rekomendasi) {
        this.idWisata = idWisata;
        this.nama = nama;
        this.alamat = alamat;
        this.deskripsi = deskripsi;
        this.idKategori = idKategori;
        this.gambar = gambar;
        this.lat = lat;
        this.lang = lang;
        this.jarak = jarak;
        this.lattuju = lattuju;
        this.langtuju = langtuju;
        this.rating = rating;
        this.rekomendasi = rekomendasi;
    }

    public int getJarak() {
        return jarak;
    }

    public String getIdWisata() {
        return idWisata;
    }

    public String getNama() {
        return nama;
    }

    public String getAlamat() {
        return alamat;
    }

    public String getDeskripsi() {
        return deskripsi;
    }


    public String getIdKategori() {
        return idKategori;
    }

    public StorageReference getGambar() {
        return gambar;
    }

    public String getLat() {
        return lat;
    }

    public String getLang() {
        return lang;
    }

    public String getLattuju() {
        return lattuju;
    }

    public String getLangtuju() {
        return langtuju;
    }

    public int getRating() {
        return rating;
    }

    public int getRekomendasi() {
        return rekomendasi;
    }

    public void setIdWisata(String idWisata) {
        this.idWisata = idWisata;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }

    public void setDeskripsi(String deskripsi) {
        this.deskripsi = deskripsi;
    }

    public void setIdKategori(String idKategori) {
        this.idKategori = idKategori;
    }

    public void setGambar(StorageReference gambar) {
        this.gambar = gambar;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public void setLattuju(String lattuju) {
        this.lattuju = lattuju;
    }

    public void setLangtuju(String langtuju) {
        this.langtuju = langtuju;
    }

    public void setJarak(int jarak) {
        this.jarak = jarak;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public void setRekomendasi(int rekomendasi) {
        this.rekomendasi = rekomendasi;
    }

    public static Comparator<Wisata> jarakComparator = new Comparator<Wisata>() {

        public int compare(Wisata j1, Wisata j2) {
            int jarak1 = j1.getJarak();
            int jarak2 = j2.getJarak();
            return jarak1-jarak2;
        }};
    public static Comparator<Wisata> ratingComparator = new Comparator<Wisata>() {

        public int compare(Wisata r1, Wisata r2) {
            int rating1 =  r1.getRating();
            int rating2 =  r2.getRating();
            return rating1-rating2;
        }};
    public static Comparator<Wisata> rekomendasiComparator = new Comparator<Wisata>() {

        public int compare(Wisata r1, Wisata r2) {
            int reko1 =  r1.getRekomendasi();
            int reko2 =  r2.getRekomendasi();
            return reko1-reko2;
        }};
}
//https://beginnersbook.com/2013/12/java-arraylist-of-object-sort-example-comparable-and-comparator/