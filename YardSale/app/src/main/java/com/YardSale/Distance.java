package com.YardSale;



import android.support.v7.app.AppCompatActivity;



public class Distance extends AppCompatActivity {

    public static class GetJuLiUtils {
        private static final double EARTH_RADIUS = 6378137.0;

        public static double getDistance(double longitude, double latitue, double longitude2, double latitue2) {
            double lat1 = rad(latitue);
            double lat2 = rad(latitue2);
            double a = lat1 - lat2;
            double b = rad(longitude) - rad(longitude2);
            double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2) + Math.cos(lat1) * Math.cos(lat2) * Math.pow(Math.sin(b / 2), 2)));
            s = s * EARTH_RADIUS;
            s = Math.round(s * 10000) / 10000;
            return s;
        }

        private static double rad(double d) {
            return d * Math.PI / 180.0;

        }
    }
}
