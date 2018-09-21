package dinkominfo.pekalongankota.webpekalongankota.Model;

import java.io.Serializable;

public class DataBerita implements Serializable {

    private String idBerita;
    private String judulBerita;
    private String gambarBerita;
    private String isiBerita;
    private String shortBerita;
    private String hitBerita;
    private String tanggalBerita;
    private String namaKategori;
    private String ketKategori;

    public String getIdBerita() {
        return idBerita;
    }

    public void setIdBerita(String idBerita) {
        this.idBerita = idBerita;
    }

    public String getJudulBerita() {
        return judulBerita;
    }

    public void setJudulBerita(String judulBerita) {
        this.judulBerita = judulBerita;
    }

    public String getGambarBerita() {
        return gambarBerita;
    }

    public void setGambarBerita(String gambarBerita) {
        this.gambarBerita = gambarBerita;
    }

    public String getIsiBerita() {
        return isiBerita;
    }

    public void setIsiBerita(String isiBerita) {
        this.isiBerita = isiBerita;
    }

    public String getShortBerita() {
        return shortBerita;
    }

    public void setShortBerita(String shortBerita) {
        this.shortBerita = shortBerita;
    }

    public String getHitBerita() {
        return hitBerita;
    }

    public void setHitBerita(String hitBerita) {
        this.hitBerita = hitBerita;
    }

    public String getTanggalBerita() {
        return tanggalBerita;
    }

    public void setTanggalBerita(String tanggalBerita) {
        this.tanggalBerita = tanggalBerita;
    }

    public String getNamaKategori() {
        return namaKategori;
    }

    public void setNamaKategori(String namaKategori) {
        this.namaKategori = namaKategori;
    }

    public String getKetKategori() {
        return ketKategori;
    }

    public void setKetKategori(String ketKategori) {
        this.ketKategori = ketKategori;
    }

}
