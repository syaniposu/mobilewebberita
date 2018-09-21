package dinkominfo.pekalongankota.webpekalongankota.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import dinkominfo.pekalongankota.webpekalongankota.Application.AppBase;
import dinkominfo.pekalongankota.webpekalongankota.Application.AppService;
import dinkominfo.pekalongankota.webpekalongankota.Application.AppTools;
import dinkominfo.pekalongankota.webpekalongankota.Model.DataBerita;
import dinkominfo.pekalongankota.webpekalongankota.R;

public class DetailActivity extends AppCompatActivity {

    private static final String EXTRA_OBJECT = "key.EXTRA_OBJECT";
    private DataBerita dataBerita;

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.title) TextView title;
    @BindView(R.id.content) TextView content;
    @BindView(R.id.date) TextView date;
    @BindView(R.id.kategori) TextView kategori;
    @BindView(R.id.progress) ProgressBar progress;
    @BindView(R.id.image) ImageView image;

    public static void navigate(Activity activity, DataBerita obj) {
        Intent i = new Intent(activity, DetailActivity.class);
        i.putExtra(EXTRA_OBJECT, obj);
        activity.startActivity(i);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        if (savedInstanceState == null) {
            dataBerita = (DataBerita) getIntent().getSerializableExtra(EXTRA_OBJECT);
            loadData();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void loadData() {
        Map<String, String> params = new HashMap<>();
        params.put("id_berita", dataBerita.getIdBerita());
        AppService.postData(AppBase.BASE_URL + "getBerita", params, new AppService.VolleyCallback() {
            @Override
            public void onSuccess(JSONObject jsonObject) {
                try {
                    String status = jsonObject.getString("status");
                    if(status.equalsIgnoreCase("success")){
                        JSONObject data = jsonObject.getJSONObject("data");
                        dataBerita.setIdBerita(data.getString("id_berita"));
                        dataBerita.setJudulBerita(data.getString("judul_berita"));
                        dataBerita.setGambarBerita(data.getString("gambar_berita"));
                        dataBerita.setIsiBerita(data.getString("isi_berita"));
                        dataBerita.setShortBerita(data.getString("short_berita"));
                        dataBerita.setHitBerita(data.getString("hit_berita"));
                        dataBerita.setTanggalBerita(data.getString("tanggal_berita"));
                        dataBerita.setNamaKategori(data.getString("nama_kategori"));
                        dataBerita.setKetKategori(data.getString("ket_kategori"));

                        title.setText(dataBerita.getJudulBerita());
                        date.setText(dataBerita.getTanggalBerita());
                        content.setText(Html.fromHtml(dataBerita.getIsiBerita()));
                        kategori.setText(dataBerita.getNamaKategori());
                        kategori.setBackground(getDrawable(R.color.orange_A700));
                        AppTools.displayImageOriginal(getApplicationContext(), progress, image, dataBerita.getGambarBerita());
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(VolleyError volleyError) {
                //
            }
        });
    }

}
