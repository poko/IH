package net.ecoarttech.ihplus.adapter;

import java.util.ArrayList;
import java.util.List;

import net.ecoarttech.ihplus.R;
import net.ecoarttech.ihplus.model.Hike;
import net.ecoarttech.ihplus.model.HikeV2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class SearchListAdapter extends BaseAdapter {
	private List<HikeV2> mList;
	private LayoutInflater mInflater;

	public SearchListAdapter(Context context, List<HikeV2> hikes) {
		this.mList = hikes;
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public void setHikes(List<HikeV2> hikes) {
		this.mList = hikes;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		if (mList != null)
			return mList.size();
		return 0;
	}

	@Override
	public Object getItem(int position) {
		if (mList != null)
			return mList.get(position);
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) { // inflate a new xml resource
			convertView = mInflater.inflate(R.layout.search_item, parent, false);
			holder = new ViewHolder();
			holder.name = (TextView) convertView.findViewById(R.id.hike_name);
			holder.desc = (TextView) convertView.findViewById(R.id.hike_desc);
			holder.info = (TextView) convertView.findViewById(R.id.hike_info);
			convertView.setTag(holder);
		} else { // view has already been loaded, get via tag
			holder = (ViewHolder) convertView.getTag();
		}
		// set fields
		HikeV2 hike = mList.get(position);
		holder.name.setText(hike.getName());
		holder.desc.setText(hike.getDescription());
		holder.info.setText(String.format("created by %s, %s.", hike.getUsername(), hike.getDate()));

		return convertView;
	}

	private static class ViewHolder {
		TextView name;
		TextView desc;
		TextView info;
	}

}
