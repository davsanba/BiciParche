package com.unal.davsanba.biciparche.Objects.ListAdapters;

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
import com.unal.davsanba.biciparche.Objects.User;
import com.unal.davsanba.biciparche.R;

import java.io.InputStream;
import java.util.List;

/**
 * Created by davsa on 15/11/2016.
 */
public class UserListAdapter  extends BaseAdapter {

    private Context mContext;
    private List<User> mUserList;

    private final String TAG = "UserListAdapter";

    public UserListAdapter(Context mContext, List<User> mUserList) {
        this.mContext = mContext;
        this.mUserList = mUserList;
    }

    @Override
    public int getCount() {
        return mUserList.size();
    }

    @Override
    public Object getItem(int position) {
        return mUserList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = View.inflate(mContext, R.layout.user_list, null);

        User cUser = mUserList.get(position);
        Log.d(TAG, cUser.getName());
        ImageView mMapPreview = (ImageView) v.findViewById(R.id.image_user_preview);

        new DownloadImageTask(mMapPreview).execute(cUser.getPhotoUrl());

        TextView mUserName = (TextView) v.findViewById(R.id.static_user_list_name);
        TextView mUserMail = (TextView) v.findViewById(R.id.static_user_list_mail);
        TextView mUserCareer = (TextView) v.findViewById(R.id.static_user_list_career);

        mUserName.setText(cUser.getName());
        mUserMail.setText(cUser.getUsername());
        mUserCareer.setText(cUser.getCareer());


        v.setTag(cUser.getUsername());
        return v;
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
                mIcon11 = getCircularBitmap(mIcon11);
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

        public  Bitmap getCircularBitmap(Bitmap bitmap)
        {
            Bitmap output;

            if (bitmap.getWidth() > bitmap.getHeight()) {
                output = Bitmap.createBitmap(bitmap.getHeight(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
            } else {
                output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getWidth(), Bitmap.Config.ARGB_8888);
            }

            Canvas canvas = new Canvas(output);

            final int color = 0xff424242;
            final Paint paint = new Paint();
            final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

            float r = 0;

            if (bitmap.getWidth() > bitmap.getHeight()) {
                r = bitmap.getHeight() / 2;
            } else {
                r = bitmap.getWidth() / 2;
            }

            paint.setAntiAlias(true);
            canvas.drawARGB(0, 0, 0, 0);
            paint.setColor(color);
            canvas.drawCircle(r, r, r, paint);
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
            canvas.drawBitmap(bitmap, rect, rect, paint);

            return output;
        }
    }
}

