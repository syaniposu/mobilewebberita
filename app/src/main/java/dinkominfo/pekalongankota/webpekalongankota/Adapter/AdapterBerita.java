package dinkominfo.pekalongankota.webpekalongankota.Adapter;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import dinkominfo.pekalongankota.webpekalongankota.Application.AppTools;
import dinkominfo.pekalongankota.webpekalongankota.Model.DataBerita;
import dinkominfo.pekalongankota.webpekalongankota.R;

public class AdapterBerita extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int VIEW_HEADER = 0;
    private final int VIEW_ITEM = 1;
    private final int VIEW_PROG = 2;

    private List<DataBerita> items = new ArrayList<>();
    private int batas;

    private boolean loading;
    private OnLoadMoreListener onLoadMoreListener;

    private Context ctx;
    private OnItemClickListener mOnItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(View view, DataBerita obj, int position);
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public AdapterBerita(Context context, RecyclerView view, List<DataBerita> items) {
        this.items = items;
        this.ctx = context;
        lastItemViewDetector(view);
    }

    public class HeaderViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView title;
        public TextView date;
        public ProgressBar progressBar;
        public ImageView image;
        public LinearLayout lyt_parent;

        public HeaderViewHolder(View v) {
            super(v);
            title = (TextView) v.findViewById(R.id.title);
            date = (TextView) v.findViewById(R.id.date);
            progressBar = (ProgressBar) v.findViewById(R.id.progress);
            image = (ImageView) v.findViewById(R.id.image);
            lyt_parent = (LinearLayout) v.findViewById(R.id.lyt_parent);
        }
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView title;
        public TextView short_content;
        public TextView date;
        public ProgressBar progressBar;
        public ImageView image;
        public LinearLayout lyt_parent;

        public ItemViewHolder(View v) {
            super(v);
            title = (TextView) v.findViewById(R.id.title);
            short_content = (TextView) v.findViewById(R.id.short_content);
            date = (TextView) v.findViewById(R.id.date);
            progressBar = (ProgressBar) v.findViewById(R.id.progress);
            image = (ImageView) v.findViewById(R.id.image);
            lyt_parent = (LinearLayout) v.findViewById(R.id.lyt_parent);
        }
    }


    public static class ProgressViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;

        public ProgressViewHolder(View v) {
            super(v);
            progressBar = (ProgressBar) v.findViewById(R.id.progress_loading);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        if (viewType == VIEW_HEADER) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_header, parent, false);
            vh = new HeaderViewHolder(v);
        }
        else if (viewType == VIEW_ITEM) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_berita, parent, false);
            vh = new ItemViewHolder(v);
        }
        else {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_loading, parent, false);
            vh = new ProgressViewHolder(v);
        }
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof HeaderViewHolder) {
            final DataBerita o = items.get(position);
            HeaderViewHolder vItem = (HeaderViewHolder) holder;
            vItem.title.setText(o.getJudulBerita());
            vItem.date.setText(o.getTanggalBerita());
            AppTools.displayImageOriginal(ctx, vItem.progressBar, vItem.image, o.getGambarBerita());
            vItem.lyt_parent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onItemClick(view, o, position);
                    }
                }
            });
        }
        else if (holder instanceof ItemViewHolder) {
            final DataBerita o = items.get(position);
            ItemViewHolder vItem = (ItemViewHolder) holder;
            vItem.title.setText(o.getJudulBerita());
            vItem.date.setText(o.getTanggalBerita());
            vItem.short_content.setText(o.getShortBerita());
            AppTools.displayImageOriginal(ctx, vItem.progressBar, vItem.image, o.getGambarBerita());
            vItem.lyt_parent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onItemClick(view, o, position);
                    }
                }
            });
        }
        else {
            ((ProgressViewHolder) holder).progressBar.setIndeterminate(true);
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public int getItemViewType(int position) {
        if(position == 0){
            return VIEW_HEADER;
        }
        else{
            return this.items.get(position) != null ? VIEW_ITEM : VIEW_PROG;
        }
    }

    public void insertData(List<DataBerita> items, int batas) {
        setLoaded();
        int positionStart = getItemCount();
        int itemCount = items.size();
        this.items.addAll(items);
        this.batas = batas;
        notifyItemRangeInserted(positionStart, itemCount);
    }

    public void setLoaded() {
        loading = false;
        for (int i = 0; i < getItemCount(); i++) {
            if (items.get(i) == null) {
                items.remove(i);
                notifyItemRemoved(i);
            }
        }
    }

    public void setLoading() {
        if (getItemCount() != 0) {
            this.items.add(null);
            notifyItemInserted(getItemCount() - 1);
            loading = true;
        }
    }

    public void resetListData() {
        this.items = new ArrayList<>();
        notifyDataSetChanged();
    }

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
    }

    private void lastItemViewDetector(RecyclerView recyclerView) {
        if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {
            final LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    final int lastPos = layoutManager.findLastVisibleItemPosition();
                    recyclerView.post(new Runnable() {
                        @Override
                        public void run() {
                            if (!loading && lastPos == getItemCount() - 1 && onLoadMoreListener != null) {
                                if (onLoadMoreListener != null) {
                                    int current_page = getItemCount() / batas;
                                    onLoadMoreListener.onLoadMore(current_page);
                                }
                                loading = true;
                            }
                        }
                    });
                }
            });
        }
    }

    public interface OnLoadMoreListener {
        void onLoadMore(int current_page);
    }

}