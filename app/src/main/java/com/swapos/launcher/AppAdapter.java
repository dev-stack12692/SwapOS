package com.swapos.launcher;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
public class AppAdapter extends RecyclerView.Adapter<AppAdapter.VH> {
    private final List<AppInfo> all;
    private List<AppInfo> shown;
    public AppAdapter(List<AppInfo> apps) { this.all = apps; this.shown = apps; }
    public void setChip(String chip) { shown = Apps.filter(all, chip); notifyDataSetChanged(); }
    static class VH extends RecyclerView.ViewHolder {
        ImageView icon; TextView label;
        VH(View v) { super(v); icon = v.findViewById(R.id.icon); label = v.findViewById(R.id.label); }
    }
    public VH onCreateViewHolder(ViewGroup p, int t) {
        return new VH(LayoutInflater.from(p.getContext()).inflate(R.layout.item_app, p, false));
    }
    public void onBindViewHolder(VH h, int pos) {
        AppInfo a = shown.get(pos);
        h.icon.setImageDrawable(a.icon);
        h.label.setText(a.label);
        h.itemView.setOnClickListener(v -> {
            Context c = v.getContext();
            Intent i = new Intent(); i.setClassName(a.packageName, a.className);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); c.startActivity(i);
        });
        h.itemView.setOnLongClickListener(v -> {
            Context c = v.getContext();
            Intent i = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                    Uri.parse("package:" + a.packageName));
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); c.startActivity(i); return true;
        });
    }
    public int getItemCount() { return shown.size(); }
}
