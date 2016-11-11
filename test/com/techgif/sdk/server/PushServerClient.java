package com.appsgeyser.sdk.server;

import com.appsgeyser.sdk.AppsgeyserSDKInternal;
import com.appsgeyser.sdk.configuration.Configuration;
import com.appsgeyser.sdk.server.BaseServerClient.OnRequestDoneListener;
import com.google.android.c2dm.C2DMessaging;
import java.io.IOException;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.telegram.messenger.support.widget.helper.ItemTouchHelper.Callback;

public class PushServerClient extends BaseServerClient implements OnRequestDoneListener {

    enum RequestType {
        REGISTER_ID,
        UNREGISTER_ID,
        GET_PUSH_ACCOUNT
    }

    public void sendRegisteredId(String regId) {
        Configuration _configuration = Configuration.getInstance();
        sendRequestAsync("http://push.appsgeyser.com/add_register_id.php?id=" + String.valueOf(_configuration.getApplicationId()) + "&guid=" + _configuration.getAppGuid() + "&regId=" + regId + "&sdk=1", RequestType.REGISTER_ID.ordinal(), this);
    }

    public void sendUnregisteredId(String regId) {
        Configuration _configuration = Configuration.getInstance();
        sendRequestAsync("http://push.appsgeyser.com/remove_register_id.php?id=" + String.valueOf(_configuration.getApplicationId()) + "&guid=" + _configuration.getAppGuid() + "&regId=" + regId + "&sdk=1", RequestType.UNREGISTER_ID.ordinal(), this);
    }

    public void onRequestDone(String requestUrl, int tag, HttpResponse response) {
        try {
            if (response.getStatusLine().getStatusCode() == Callback.DEFAULT_DRAG_ANIMATION_DURATION && tag != RequestType.REGISTER_ID.ordinal() && tag == RequestType.GET_PUSH_ACCOUNT.ordinal()) {
                _savePushAccount(response);
            }
        } catch (Exception e) {
            System.err.println(e);
        }
    }

    private void _savePushAccount(HttpResponse response) {
        Configuration configuration = Configuration.getInstance();
        try {
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                String pushAccount = new JSONObject(EntityUtils.toString(entity)).getString("accountName");
                configuration.saveNewPushAccount(pushAccount, AppsgeyserSDKInternal.getApplication());
                C2DMessaging.register(AppsgeyserSDKInternal.getApplication(), pushAccount);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e2) {
            e2.printStackTrace();
        } catch (IOException e3) {
            e3.printStackTrace();
        }
    }

    public void loadPushAccount() {
        Configuration configuration = Configuration.getInstance();
        sendRequestAsync("http://push.appsgeyser.com/register.php?id=" + String.valueOf(configuration.getApplicationId()) + "&guid=" + configuration.getAppGuid(), RequestType.GET_PUSH_ACCOUNT.ordinal(), this);
    }
}
