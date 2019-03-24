package fiubatdp2g1_hoycomo.hoycomo.service.api;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;


public class HttpRequester {

    static private RequestQueue requestQueue;

    static public void Initialize(Context context) {
        requestQueue = Volley.newRequestQueue(context.getApplicationContext());
    }

    private static <T>  void SendRequest(Request<T> request ) {
        requestQueue.add(request);
    }


    private static JsonObjectRequest CreateRequest(final int method, final String url, final JSONObject body, Response.Listener<JSONObject> responseListener) {
        return new JsonObjectRequest (method, url, body, responseListener , new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Http request",method + "Error in: " + url);
                if (body != null) {
                    Log.e("Http request", body.toString());
                    //Log.e("Http request", error.getMessage());
                } else {
                    Log.e("Http request", "null" );
                }
            }
        });
    }

    public static void SendGETRequest(String url, JSONObject body, Response.Listener<JSONObject> responseListener){
        JsonObjectRequest jsonObjectRequest = CreateRequest(Request.Method.GET, url, body, responseListener);
        HttpRequester.SendRequest( jsonObjectRequest );
    }

    public static void SendPOSTRequest(String url, JSONObject body, Response.Listener<JSONObject> responseListener) {
        JsonObjectRequest jsonObjectRequest = CreateRequest(Request.Method.POST, url, body, responseListener);
        HttpRequester.SendRequest( jsonObjectRequest );
    }

    public static void SendDELETERequest(String url, JSONObject body, Response.Listener<JSONObject> responseListener) {
        JsonObjectRequest jsonObjectRequest = CreateRequest(Request.Method.DELETE, url, body, responseListener);
        HttpRequester.SendRequest( jsonObjectRequest );
    }

    public static void SendPUTRequest(String url, JSONObject body, Response.Listener<JSONObject> responseListener) {
        JsonObjectRequest jsonObjectRequest = CreateRequest(Request.Method.PUT, url, body, responseListener);
        HttpRequester.SendRequest( jsonObjectRequest );
    }
}
