package com.unal.davsanba.biciparche.Objects;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by davsa on 18/10/2016.
 */
public class Group implements Parcelable {

    private String      groupId;
    private String      groupName;
    private Route  groupRoute;
    private List<String>  groupUsers;
    private String      groupAdminUserID;

    public Group() {    }

    public Group(String groupId, String groupName, Route groupRoute, List<String> groupUsers, String groupAdminUserID) {
        this.groupId = groupId;
        this.groupName = groupName;
        this.groupRoute = groupRoute;
        this.groupUsers = groupUsers;
        this.groupAdminUserID = groupAdminUserID;
    }

    public Group(String groupName,  String groupAdminUserID) {
        this.groupName = groupName;
        this.groupAdminUserID = groupAdminUserID;
        groupUsers = new ArrayList<>();
    }

    public Group(String groupName, Route groupRoute, List<String> groupUsers, String groupAdminUserID) {
        this.groupName = groupName;
        this.groupRoute = groupRoute;
        this.groupUsers = groupUsers;
        this.groupAdminUserID = groupAdminUserID;
    }

    public void addUser(String user){
        groupUsers.add(user);
    }

    public void removeUser(String user){
        groupUsers.remove(user);
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public Route getGroupRoute() {
        return groupRoute;
    }

    public void setGroupRoute(Route groupRoute) {
        this.groupRoute = groupRoute;
    }

    public List<String> getGroupUsers() {
        return groupUsers;
    }

    public void setGroupUsers(List<String> groupUsers) {
        this.groupUsers = groupUsers;
    }

    public String getGroupAdminUserID() {
        return groupAdminUserID;
    }

    public void setGroupAdminUserID(String groupAdminUserID) {
        this.groupAdminUserID = groupAdminUserID;
    }

    protected Group(Parcel in) {
        groupId = in.readString();
        groupName = in.readString();
        groupRoute = in.readParcelable(Route.class.getClassLoader());
        groupUsers = in.createStringArrayList();
        groupAdminUserID = in.readString();
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
        dest.writeString(groupId);
        dest.writeString(groupName);
        dest.writeParcelable(groupRoute, flags);
        dest.writeStringList(groupUsers);
        dest.writeString(groupAdminUserID);
    }
}
