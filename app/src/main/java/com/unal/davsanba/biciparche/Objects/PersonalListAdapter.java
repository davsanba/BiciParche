package com.unal.davsanba.biciparche.Objects;

import android.content.Context;
import android.graphics.*;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.unal.davsanba.biciparche.R;

import java.io.InputStream;
import java.util.List;

/**
 * Created by davsa on 14/11/2016.
 */
public class PersonalListAdapter extends BaseAdapter {

    private Context mContext;
    private List<Route> mRouteList;

    private final String TAG = "PersonalListAdapter";

    public PersonalListAdapter(Context mContext, List<Route> mRouteList) {
        this.mContext = mContext;
        this.mRouteList = mRouteList;
    }

    @Override
    public int getCount() {
        return mRouteList.size();
    }

    @Override
    public Object getItem(int position) {
        return mRouteList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = View.inflate(mContext, R.layout.personal_route_list, null);

        Route cRoute = mRouteList.get(position);
        String url = getUrl(cRoute);
        //Log.d(TAG, url);

        ImageView mMapPreview = (ImageView) v.findViewById(R.id.image_map_preview);

        new DownloadImageTask(mMapPreview).execute(url);


        TextView mRouteName = (TextView) v.findViewById(R.id.static_map_list_name);
        TextView mRouteDays = (TextView) v.findViewById(R.id.static_map_list_days);
        TextView mRouteHour = (TextView) v.findViewById(R.id.static_map_list_hour);

        mRouteName.setText(cRoute.getRouteName());
        mRouteDays.setText(cRoute.getRouteDays());
        mRouteHour.setText(cRoute.getRouteHour());



        v.setTag(cRoute.getRouteName());

        return v;
    }

    public String getUrl(Route cRoute){
        String url = "https://maps.googleapis.com/maps/api/staticmap?size=200x200&markers=";

        url += cRoute.getRouteStart().latitude + "," + cRoute.getRouteStart().longitude + "|";
        url += cRoute.getRouteEnd().latitude + "," + cRoute.getRouteEnd().longitude;
        url += "&key=" + mContext.getResources().getString(R.string.google_api_key);

        return url;
    }


    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
                Toast.makeText(mContext, mContext.getString(R.string.toast_error_profile_photo), Toast.LENGTH_LONG ).show();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }
}
