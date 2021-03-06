package net.hockeyapp.android;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.Toast;
import java.io.File;
import java.io.FileOutputStream;
import net.hockeyapp.android.utils.HockeyLog;
import net.hockeyapp.android.utils.Util;
import net.hockeyapp.android.views.PaintView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.VideoPlayer;

public class PaintActivity extends Activity {
    public static final String EXTRA_IMAGE_URI = "imageUri";
    private static final int MENU_CLEAR_ID = 3;
    private static final int MENU_SAVE_ID = 1;
    private static final int MENU_UNDO_ID = 2;
    private String mImageName;
    private PaintView mPaintView;

    /* renamed from: net.hockeyapp.android.PaintActivity.1 */
    class C03861 implements OnClickListener {
        C03861() {
        }

        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case LayoutHelper.WRAP_CONTENT /*-2*/:
                    PaintActivity.this.finish();
                case VideoPlayer.TRACK_DISABLED /*-1*/:
                    PaintActivity.this.makeResult();
                default:
            }
        }
    }

    /* renamed from: net.hockeyapp.android.PaintActivity.2 */
    class C03872 extends AsyncTask<File, Void, Void> {
        final /* synthetic */ Bitmap val$bitmap;

        C03872(Bitmap bitmap) {
            this.val$bitmap = bitmap;
        }

        protected Void doInBackground(File... args) {
            try {
                FileOutputStream out = new FileOutputStream(args[0]);
                this.val$bitmap.compress(CompressFormat.JPEG, 100, out);
                out.close();
            } catch (Throwable e) {
                e.printStackTrace();
                HockeyLog.error("Could not save image.", e);
            }
            return null;
        }
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Uri imageUri = (Uri) getIntent().getExtras().getParcelable(EXTRA_IMAGE_URI);
        this.mImageName = determineFilename(imageUri, imageUri.getLastPathSegment());
        int displayWidth = getResources().getDisplayMetrics().widthPixels;
        int displayHeight = getResources().getDisplayMetrics().heightPixels;
        int currentOrientation = displayWidth > displayHeight ? 0 : MENU_SAVE_ID;
        int desiredOrientation = PaintView.determineOrientation(getContentResolver(), imageUri);
        setRequestedOrientation(desiredOrientation);
        if (currentOrientation != desiredOrientation) {
            HockeyLog.debug("Image loading skipped because activity will be destroyed for orientation change.");
            return;
        }
        this.mPaintView = new PaintView(this, imageUri, displayWidth, displayHeight);
        LinearLayout vLayout = new LinearLayout(this);
        vLayout.setLayoutParams(new LayoutParams(-1, -1));
        vLayout.setGravity(17);
        vLayout.setOrientation(MENU_SAVE_ID);
        LinearLayout hLayout = new LinearLayout(this);
        hLayout.setLayoutParams(new LayoutParams(-1, -1));
        hLayout.setGravity(17);
        hLayout.setOrientation(0);
        vLayout.addView(hLayout);
        hLayout.addView(this.mPaintView);
        setContentView(vLayout);
        Toast.makeText(this, getString(C0388R.string.hockeyapp_paint_indicator_toast), MENU_SAVE_ID).show();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add(0, MENU_SAVE_ID, 0, getString(C0388R.string.hockeyapp_paint_menu_save));
        menu.add(0, MENU_UNDO_ID, 0, getString(C0388R.string.hockeyapp_paint_menu_undo));
        menu.add(0, MENU_CLEAR_ID, 0, getString(C0388R.string.hockeyapp_paint_menu_clear));
        return true;
    }

    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case MENU_SAVE_ID /*1*/:
                makeResult();
                return true;
            case MENU_UNDO_ID /*2*/:
                this.mPaintView.undo();
                return true;
            case MENU_CLEAR_ID /*3*/:
                this.mPaintView.clearImage();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode != 4 || this.mPaintView.isClear()) {
            return super.onKeyDown(keyCode, event);
        }
        OnClickListener dialogClickListener = new C03861();
        new Builder(this).setMessage(C0388R.string.hockeyapp_paint_dialog_message).setPositiveButton(C0388R.string.hockeyapp_paint_dialog_positive_button, dialogClickListener).setNegativeButton(C0388R.string.hockeyapp_paint_dialog_negative_button, dialogClickListener).setNeutralButton(C0388R.string.hockeyapp_paint_dialog_neutral_button, dialogClickListener).show();
        return true;
    }

    private void makeResult() {
        File hockeyAppCache = new File(getCacheDir(), Util.LOG_IDENTIFIER);
        hockeyAppCache.mkdir();
        File result = new File(hockeyAppCache, this.mImageName + ".jpg");
        int suffix = MENU_SAVE_ID;
        while (result.exists()) {
            result = new File(hockeyAppCache, this.mImageName + "_" + suffix + ".jpg");
            suffix += MENU_SAVE_ID;
        }
        this.mPaintView.setDrawingCacheEnabled(true);
        C03872 c03872 = new C03872(this.mPaintView.getDrawingCache());
        File[] fileArr = new File[MENU_SAVE_ID];
        fileArr[0] = result;
        c03872.execute(fileArr);
        Intent intent = new Intent();
        intent.putExtra(EXTRA_IMAGE_URI, Uri.fromFile(result));
        if (getParent() == null) {
            setResult(-1, intent);
        } else {
            getParent().setResult(-1, intent);
        }
        finish();
    }

    private String determineFilename(Uri uri, String fallback) {
        String[] projection = new String[MENU_SAVE_ID];
        projection[0] = "_data";
        String path = null;
        Cursor metaCursor = getApplicationContext().getContentResolver().query(uri, projection, null, null, null);
        if (metaCursor != null) {
            try {
                if (metaCursor.moveToFirst()) {
                    path = metaCursor.getString(0);
                }
                metaCursor.close();
            } catch (Throwable th) {
                metaCursor.close();
            }
        }
        return path == null ? fallback : new File(path).getName();
    }
}
