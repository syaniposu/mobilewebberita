package dinkominfo.pekalongankota.webpekalongankota.Activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import dinkominfo.pekalongankota.webpekalongankota.Adapter.AdapterBerita;
import dinkominfo.pekalongankota.webpekalongankota.Application.AppBase;
import dinkominfo.pekalongankota.webpekalongankota.Application.AppService;
import dinkominfo.pekalongankota.webpekalongankota.Model.DataBerita;
import dinkominfo.pekalongankota.webpekalongankota.R;

public class MainActivity extends AppCompatActivity {

    List<DataBerita> itemList;
    AdapterBerita adapterBerita;
    private int post_total = 0;
    private int failed_page = 0;

    @BindView(R.id.toolbar)
    android.support.v7.widget.Toolbar toolbar;
    @BindView(R.id.swipe_refresh_layout) SwipeRefreshLayout swipe_refresh;
    @BindView(R.id.recyclerView) RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        if (savedInstanceState == null) {
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            adapterBerita = new AdapterBerita(getApplicationContext(), recyclerView, new ArrayList<DataBerita>());
            recyclerView.setAdapter(adapterBerita);
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle("Website Kota Pekalongan");
            requestAction(1);

            swipe_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    adapterBerita.resetListData();
                    requestAction(1);
                }
            });
            adapterBerita.setOnLoadMoreListener(new AdapterBerita.OnLoadMoreListener() {
                @Override
                public void onLoadMore(int current_page) {
                    if (post_total > adapterBerita.getItemCount() && current_page != 0) {
                        int next_page = current_page + 1;
                        requestAction(next_page);
                    } else {
                        adapterBerita.setLoaded();
                    }
                }
            });
            adapterBerita.setOnItemClickListener(new AdapterBerita.OnItemClickListener() {
                @Override
                public void onItemClick(View view, DataBerita obj, int position) {
                    DetailActivity.navigate(MainActivity.this, obj);
                }
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void loadData(final int page_no) {
        Map<String, String> params = new HashMap<>();
        params.put("page", String.valueOf(page_no));
        AppService.postData(AppBase.BASE_URL + "listBerita", params, new AppService.VolleyCallback() {
            @Override
            public void onSuccess(JSONObject jsonObject) {
                swipe_refresh.setRefreshing(false);
                try {
                    String status = jsonObject.getString("status");
                    if(status.equalsIgnoreCase("success")){
                        itemList = new ArrayList<>();
                        JSONObject data = jsonObject.getJSONObject("data");
                        final String nomer = data.getString("no");
                        final String page = data.getString("page");
                        final String batas = data.getString("batas");
                        final String jumlah = data.getString("jumlah");
                        JSONArray tabel = data.getJSONArray("tabel");
                        for(int i = 0; i < tabel.length(); i++) {
                            JSONObject berita = tabel.getJSONObject(i);
                            DataBerita dataBerita = new DataBerita();
                            dataBerita.setIdBerita(berita.getString("id_berita"));
                            dataBerita.setJudulBerita(berita.getString("judul_berita"));
                            dataBerita.setGambarBerita(berita.getString("gambar_berita"));
                            dataBerita.setIsiBerita(berita.getString("isi_berita"));
                            dataBerita.setShortBerita(berita.getString("short_berita"));
                            dataBerita.setHitBerita(berita.getString("hit_berita"));
                            dataBerita.setTanggalBerita(berita.getString("tanggal_berita"));
                            dataBerita.setNamaKategori(berita.getString("nama_kategori"));
                            dataBerita.setKetKategori(berita.getString("ket_kategori"));
                            itemList.add(dataBerita);
                        }

                        post_total = Integer.parseInt(jumlah);
                        adapterBerita.insertData(itemList, Integer.parseInt(batas));
                        swipeProgress(false);
                        if (itemList.size() == 0) {
                            showNoItemView(true);
                        }
                    }else {
                        // Show Dialog
                        String message = jsonObject.getString("data");
                        final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setTitle("Ooops");
                        builder.setMessage(message);
                        builder.setCancelable(false);
                        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                finish();
                            }
                        });
                        builder.show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(VolleyError volleyError) {
                swipe_refresh.setRefreshing(false);
                showNoItemView(false);
                onFailRequest(page_no);
            }
        });
    }

    private void onFailRequest(int page_no) {
        failed_page = page_no;
        adapterBerita.setLoaded();
        swipeProgress(false);
        if (AppService.isConnect(this)) {
            showFailedView(true, getString(R.string.failed_text));
        } else {
            showFailedView(true, getString(R.string.no_internet_text));
        }
    }

    private void requestAction(final int page_no) {
        showFailedView(false, "");
        showNoItemView(false);
        if (page_no == 1) {
            swipeProgress(true);
        } else {
            adapterBerita.setLoading();
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                loadData(page_no);
            }
        }, 2000);
    }

    private void showNoItemView(boolean show) {
        View lyt_no_item = (View) findViewById(R.id.lyt_no_item);
        ((TextView) findViewById(R.id.no_item_message)).setText("Laporan Masih Kosong");
        if (show) {
            recyclerView.setVisibility(View.GONE);
            lyt_no_item.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            lyt_no_item.setVisibility(View.GONE);
        }
    }

    private void swipeProgress(final boolean show) {
        if (!show) {
            swipe_refresh.setRefreshing(show);
            return;
        }
        swipe_refresh.post(new Runnable() {
            @Override
            public void run() {
                swipe_refresh.setRefreshing(show);
            }
        });
    }

    private void showFailedView(boolean show, String message) {
        View lyt_failed = (View) findViewById(R.id.lyt_failed);
        ((TextView) findViewById(R.id.failed_message)).setText(message);
        if (show) {
            recyclerView.setVisibility(View.GONE);
            lyt_failed.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            lyt_failed.setVisibility(View.GONE);
        }
        ((Button) findViewById(R.id.failed_retry)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestAction(failed_page);
            }
        });
    }

}
