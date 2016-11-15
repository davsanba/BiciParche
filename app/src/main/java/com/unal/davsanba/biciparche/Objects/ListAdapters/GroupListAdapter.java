package com.unal.davsanba.biciparche.Objects.ListAdapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.unal.davsanba.biciparche.Objects.Group;
import com.unal.davsanba.biciparche.R;

import java.util.List;

/**
 * Created by davsa on 15/11/2016.
 */
public class GroupListAdapter extends BaseAdapter {

    private Context mContext;
    private List<Group> mGroupList;

    private final String TAG = "GroupListAdapter";

    public GroupListAdapter(Context mContext, List<Group> mGroupList) {
        this.mContext = mContext;
        this.mGroupList = mGroupList;
    }

    @Override
    public int getCount() {
        return mGroupList.size();
    }

    @Override
    public Object getItem(int position) {
        return mGroupList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = View.inflate(mContext, R.layout.personal_route_list, null);

        Group cGroup = mGroupList.get(position);

        TextView mGroupName  = (TextView) v.findViewById(R.id.static_group_list_name);
        TextView mGroupDays  = (TextView) v.findViewById(R.id.static_group_list_days);
        TextView mGroupHour  = (TextView) v.findViewById(R.id.static_group_list_hour);
        TextView mGroupCount = (TextView) v.findViewById(R.id.static_group_list_users);

        mGroupName.setText(cGroup.getGroupName());
        mGroupDays.setText(mContext.getString(R.string.static_list_group_day)+ cGroup.getGroupRoute().getRouteDays());
        mGroupHour.setText(mContext.getString(R.string.static_list_group_hour) + cGroup.getGroupRoute().getRouteHour());
        mGroupCount.setText(mContext.getString(R.string.static_list_group_members) + cGroup.getGroupUsers().size());


        v.setTag(cGroup.getGroupId());

        return v;
    }

}
