package com.cathedralsw.schoolteacher.classes;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import static android.content.ContentValues.TAG;


/**
 * Created by alexis on 25/10/17.
 */

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    private String refreshedToken;

    public String getRefreshedToken() {
        return refreshedToken;
    }

    public void setRefreshedToken(String refreshedToken) {
        this.refreshedToken = refreshedToken;
    }

    public MyFirebaseInstanceIDService() {
    }

    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
//        sendRegistrationToServer(refreshedToken);
    }
    // [END refresh_token]

    /**
     * Persist token to third-party servers.
     *
     * Modify this method to associate the user's FCM InstanceID token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
//    private void sendRegistrationToServer(String token) {
////        new updateTokenTask().execute();
//    }


//    public class updateTokenTask extends AsyncTask<Void, Void, JSONObject> {
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//        }
//
//        @Override
//        protected JSONObject doInBackground(Void... params) {
//
//            JSONObject response = null;
//            try {
//                response = NetworkUtils.schoolFireBaseToken(schoolToken);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//
//            return response;
//        }
//
//        @Override
//        protected void onPostExecute(JSONObject result) {
//
//
//        }
//    }



}