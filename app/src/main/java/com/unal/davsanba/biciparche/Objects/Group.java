package com.unal.davsanba.biciparche.Objects;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by davsa on 18/10/2016.
 */
public class Group implements Parcelable {
    private String groupName;
    private GroupRoute groupRoute;
    private List<User> groupUsers;
    private User groupAdminUser;

    protected Group(Parcel in) {
        groupName = in.readString();
        groupRoute = in.readParcelable(GroupRoute.class.getClassLoader());
        groupUsers = in.createTypedArrayList(User.CREATOR);
        groupAdminUser = in.readParcelable(User.class.getClassLoader());
    }

    public static final Creator<Group> CREATOR = new Creator<Group>() {
        @Override
        public Group createFromParcel(Parcel in) {
            return new Group(in);
        }

        @Override
        public Group[] newArray(int size) {
            return new Group[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(groupName);
        dest.writeParcelable(groupRoute, flags);
        dest.writeTypedList(groupUsers);
        dest.writeParcelable(groupAdminUser, flags);
    }
}
