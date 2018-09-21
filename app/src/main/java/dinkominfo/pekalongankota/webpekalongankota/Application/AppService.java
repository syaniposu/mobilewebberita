package dinkominfo.pekalongankota.webpekalongankota.Application;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import dinkominfo.pekalongankota.webpekalongankota.Volley.DataPart;
import dinkominfo.pekalongankota.webpekalongankota.Volley.VolleyMultipartRequest;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

/**
 * Created by hanifmuhammad on 1/27/18.
 */

public class AppService {

    public static final String TAG = AppService.class.getSimpleName();
    public static final String JSON_REQ = "json_obj_req";
    public static final String CACHE_CONTROL = "max-age=0";
    public static final String USER_AGENT = "Android";
    public static final String TOKEN = "5uo4ob4fseg4fm9h8bl447usq3";

    public static final String EVENT_USER_CONNECT = "event_user_connect";
    public static final String EVENT_USER_DISCONNECT = "event_user_disconnect";
    public static final String EVENT_USER_MESSAGE = "event_user_message";
    public static final String EVENT_GROUP_MESSAGE = "event_group_message";
    public static Socket mSocket;

    private static String socket_tokens = "";
    private static String socket_server = "";

    private static final boolean DEBUG_MODE = false;

    public interface VolleyCallback {
        void onSuccess(JSONObject jsonObject);
        void onError(VolleyError volleyError);
    }

    public interface SocketCallback {
        void onMessage(JSONObject jsonObject);
    }

    public interface EventSocketCallback {
        void onConnected();
        void onError();
        void onDisconnected();
    }

    public static void getData(String url, final VolleyCallback volleyCallback) {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(DEBUG_MODE) Log.d(TAG, "Response: "+response.toString());
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    volleyCallback.onSuccess(jsonObject);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                volleyCallback.onError(error);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap();
                headers.put("Cache-Control", CACHE_CONTROL);
                headers.put("User-Agent", USER_AGENT);
                headers.put("Token", TOKEN);
                return headers;
            }
        };

        AppBase.getInstance().addToRequestQueue(stringRequest, JSON_REQ);
    }

    public static void postData(String url, final Map<String, String> data, final VolleyCallback volleyCallback) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(DEBUG_MODE) Log.d(TAG, "Response: "+response.toString());
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    volleyCallback.onSuccess(jsonObject);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                volleyCallback.onError(error);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap();
                headers.put("Cache-Control", CACHE_CONTROL);
                headers.put("User-Agent", USER_AGENT);
                headers.put("Token", TOKEN);
                return headers;
            }

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = data;
                return params;
            }
        };

        AppBase.getInstance().addToRequestQueue(stringRequest, JSON_REQ);
    }

    public static void postDataMultipart(String url, final Map<String, String> data_1, final Map<String, DataPart> data_2, final VolleyCallback volleyCallback) {
        VolleyMultipartRequest stringRequest = new VolleyMultipartRequest(Request.Method.POST, url, new Response.Listener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse response) {
                String resultResponse = new String(response.data);
                if(DEBUG_MODE) Log.d(TAG, "Response: "+response.toString());
                try {
                    JSONObject jsonObject = new JSONObject(resultResponse);
                    volleyCallback.onSuccess(jsonObject);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                volleyCallback.onError(error);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap();
                headers.put("Cache-Control", CACHE_CONTROL);
                headers.put("User-Agent", USER_AGENT);
                headers.put("Token", TOKEN);
                return headers;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = data_1;
                return params;
            }

            @Override
            protected Map<String, DataPart> getByteData() throws AuthFailureError {
                Map<String, DataPart> params = data_2;
                return params;
            }
        };

        AppBase.getInstance().addToRequestQueue(stringRequest, JSON_REQ);
    }

    public static void connectSocket(final Map<String, Object> socket) {
        disconnectSocket();
        try {
            IO.Options options = new IO.Options();
            options.forceNew = true;
            options.timeout = (60 * 1000); //set -1 to  disable it
            options.reconnection = true;
            options.reconnectionDelay = (long) 3000;
            options.reconnectionDelayMax = (long) 60000;
            options.reconnectionAttempts = 99999;
            socket_tokens = socket.get("tokens").toString();
            socket_server = socket.get("server").toString();
            options.query = "token=" + socket_tokens;
            mSocket = IO.socket(socket_server, options);
            if (!mSocket.connected()) mSocket.connect();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public static void eventSocket(final EventSocketCallback eventSocketCallback) {
        if(mSocket != null) {
            mSocket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    eventSocketCallback.onConnected();
                    if(DEBUG_MODE) Log.d("Socket Status", "connected to Server "+socket_server);
                }
            }).on(Socket.EVENT_CONNECT_ERROR, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    eventSocketCallback.onError();
                    if(DEBUG_MODE) Log.d("Socket Status", "error connected");
                }
            }).on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    eventSocketCallback.onDisconnected();
                    if(DEBUG_MODE) Log.d("Socket Status", "disconnected from Server "+socket_server);
                }
            });
        }
    }

    public static void disconnectSocket() {
        if(mSocket != null) {
            mSocket.off(Socket.EVENT_CONNECT);
            mSocket.off(Socket.EVENT_CONNECT_ERROR);
            mSocket.off(Socket.EVENT_DISCONNECT);
            mSocket.off(EVENT_USER_CONNECT);
            mSocket.off(EVENT_USER_MESSAGE);
            mSocket.off(EVENT_USER_DISCONNECT);
            mSocket.off(EVENT_GROUP_MESSAGE);
            mSocket.disconnect();
            mSocket.close();
            mSocket = null;
        }
    }

    public static void sendMessage(String event, JSONObject data) {
        if(mSocket != null) {
            mSocket.emit(event, data);
            if(DEBUG_MODE) Log.d("Socket Status", "send message "+event);
        }
    }

    public static void getMessage(String event, final SocketCallback socketCallback) {
        if(mSocket != null) {
            mSocket.on(event, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    JSONObject data = (JSONObject) args[0];
                    socketCallback.onMessage(data);
                }
            });
        }
    }

    public static boolean isConnect(Context context) {
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            if (activeNetworkInfo != null) {
                if (activeNetworkInfo.isConnected() || activeNetworkInfo.isConnectedOrConnecting()) {
                    return true;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        } catch (Exception e){
            return false;
        }
    }

}
