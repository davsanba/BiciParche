package com.unal.davsanba.biciparche.Objects;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;

/**
 * Created by davsa on 18/10/2016.
 */
public class Route implements Parcelable {

    private String RouteID;
    private String RouteOwnerID;
    private String RouteName;
    private String RouteDays;
    private String RouteHour;

    private LatLng RouteStart;
    private LatLng RouteEnd;

    private List<LatLng> RouteMarks;



    public Route() {    }

    public Route(String routeID, String routeOwnerID, String routeName, String routeDays, String routeHour, LatLng routeStart, LatLng routeEnd, List<LatLng> routeMarks) {
        RouteID = routeID;
        RouteOwnerID = routeOwnerID;
        RouteName = routeName;
        RouteDays = routeDays;
        RouteHour = routeHour;
        RouteStart = routeStart;
        RouteEnd = routeEnd;
        RouteMarks = routeMarks;
    }

    public Route(String routeOwnerID, String routeName, String routeDays, String routeHour, LatLng routeStart, LatLng routeEnd, List<LatLng> routeMarks) {
        RouteOwnerID = routeOwnerID;
        RouteName = routeName;
        RouteDays = routeDays;
        RouteHour = routeHour;
        RouteStart = routeStart;
        RouteEnd = routeEnd;
        RouteMarks = routeMarks;
    }


    protected Route(Parcel in) {
        RouteID = in.readString();
        RouteOwnerID = in.readString();
        RouteName = in.readString();
        RouteDays = in.readString();
        RouteHour = in.readString();
        RouteStart = in.readParcelable(LatLng.class.getClassLoader());
        RouteEnd = in.readParcelable(LatLng.class.getClassLoader());
        RouteMarks = in.createTypedArrayList(LatLng.CREATOR);
    }

    public static final Creator<Route> CREATOR = new Creator<Route>() {
        @Override
        public Route createFromParcel(Parcel in) {
            return new Route(in);
        }

        @Override
        public Route[] newArray(int size) {
            return new Route[size];
        }
    };


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(RouteID);
        dest.writeString(RouteOwnerID);
        dest.writeString(RouteName);
        dest.writeString(RouteDays);
        dest.writeString(RouteHour);
        dest.writeParcelable(RouteStart, flags);
        dest.writeParcelable(RouteEnd, flags);
        dest.writeTypedList(RouteMarks);
    }


    public String getRouteID() {

        return RouteID;
    }

    public void setRouteID(String routeID) {
        RouteID = routeID;
    }

    public String getRouteOwnerID() {
        return RouteOwnerID;
    }

    public void setRouteOwnerID(String routeOwnerID) {
        RouteOwnerID = routeOwnerID;
    }

    public String getRouteName() {
        return RouteName;
    }

    public void setRouteName(String routeName) {
        RouteName = routeName;
    }

    public String getRouteDays() {
        return RouteDays;
    }

    public void setRouteDays(String routeDays) {
        RouteDays = routeDays;
    }

    public String getRouteHour() {
        return RouteHour;
    }

    public void setRouteHour(String routeHour) {
        RouteHour = routeHour;
    }

    public LatLng getRouteStart() {
        return RouteStart;
    }

    public void setRouteStart(LatLng routeStart) {
        RouteStart = routeStart;
    }

    public LatLng getRouteEnd() {
        return RouteEnd;
    }

    public void setRouteEnd(LatLng routeEnd) {
        RouteEnd = routeEnd;
    }

    public List<LatLng> getRouteMarks() {
        return RouteMarks;
    }

    public void setRouteMarks(List<LatLng> routeMarks) {
        RouteMarks = routeMarks;
    }

}
