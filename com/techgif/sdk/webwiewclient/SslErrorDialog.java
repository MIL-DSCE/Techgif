package com.appsgeyser.sdk.webwiewclient;

import android.annotation.TargetApi;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.net.http.SslError;
import android.os.Build.VERSION;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import com.appsgeyser.sdk.configuration.Constants;

public class SslErrorDialog {
    private Context _activity;

    /* renamed from: com.appsgeyser.sdk.webwiewclient.SslErrorDialog.1 */
    class C01561 implements OnClickListener {
        private final /* synthetic */ SslErrorHandler val$handler;

        C01561(SslErrorHandler sslErrorHandler) {
            this.val$handler = sslErrorHandler;
        }

        public void onClick(DialogInterface dialog, int id) {
            this.val$handler.proceed();
        }
    }

    /* renamed from: com.appsgeyser.sdk.webwiewclient.SslErrorDialog.2 */
    class C01572 implements OnClickListener {
        private final /* synthetic */ SslErrorHandler val$handler;

        C01572(SslErrorHandler sslErrorHandler) {
            this.val$handler = sslErrorHandler;
        }

        public void onClick(DialogInterface dialog, int id) {
            this.val$handler.cancel();
        }
    }

    public SslErrorDialog(Context activity) {
        this._activity = activity;
    }

    @TargetApi(14)
    public void execute(WebView view, SslErrorHandler handler, SslError error) {
        if (VERSION.SDK_INT < 14 || error.getUrl().equals(view.getUrl())) {
            Builder builder = new Builder(this._activity);
            builder.setMessage(Constants.SSL_ERROR_DIALOG_MESSAGE).setTitle(Constants.SSL_ERROR_DIALOG_TITLE).setPositiveButton(Constants.SSL_ERROR_DIALOG_BUTTON_POSITIVE, new C01561(handler)).setNegativeButton(Constants.SSL_ERROR_DIALOG_BUTTON_NEGATIVE, new C01572(handler));
            builder.create().show();
            return;
        }
        handler.proceed();
    }
}
