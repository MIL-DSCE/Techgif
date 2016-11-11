package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.StateListAnimator;
import android.annotation.SuppressLint;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Outline;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Html;
import android.text.Spannable;
import android.text.TextUtils.TruncateAt;
import android.text.method.LinkMovementMethod;
import android.util.Base64;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import java.io.File;
import java.util.ArrayList;
import java.util.Locale;
import org.telegram.PhoneFormat.PhoneFormat;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.AnimatorListenerAdapterProxy;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.C0691R;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.browser.Browser;
import org.telegram.messenger.exoplayer.DefaultLoadControl;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.SerializedData;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.FileLocation;
import org.telegram.tgnet.TLRPC.InputFile;
import org.telegram.tgnet.TLRPC.PhotoSize;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_help_getSupport;
import org.telegram.tgnet.TLRPC.TL_help_support;
import org.telegram.tgnet.TLRPC.TL_inputGeoPointEmpty;
import org.telegram.tgnet.TLRPC.TL_inputPhotoCropAuto;
import org.telegram.tgnet.TLRPC.TL_photos_photo;
import org.telegram.tgnet.TLRPC.TL_photos_uploadProfilePhoto;
import org.telegram.tgnet.TLRPC.TL_userProfilePhoto;
import org.telegram.tgnet.TLRPC.TL_userProfilePhotoEmpty;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.BottomSheet.BottomSheetCell;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Adapters.BaseFragmentAdapter;
import org.telegram.ui.Cells.CheckBoxCell;
import org.telegram.ui.Cells.EmptyCell;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.TextCheckCell;
import org.telegram.ui.Cells.TextDetailSettingsCell;
import org.telegram.ui.Cells.TextInfoCell;
import org.telegram.ui.Cells.TextSettingsCell;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.AvatarUpdater;
import org.telegram.ui.Components.AvatarUpdater.AvatarUpdaterDelegate;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.NumberPicker;
import org.telegram.ui.Components.VideoPlayer;
import org.telegram.ui.PhotoViewer.PhotoViewerProvider;
import org.telegram.ui.PhotoViewer.PlaceProviderObject;
import org.telegram.ui.StickersActivity.TouchHelperCallback;

public class SettingsActivity extends BaseFragment implements NotificationCenterDelegate, PhotoViewerProvider {
    private static final int edit_name = 1;
    private static final int logout = 2;
    private int askQuestionRow;
    private int autoplayGifsRow;
    private BackupImageView avatarImage;
    private AvatarUpdater avatarUpdater;
    private int backgroundRow;
    private int cacheRow;
    private int clearLogsRow;
    private int contactsReimportRow;
    private int contactsSectionRow;
    private int contactsSortRow;
    private int customTabsRow;
    private int directShareRow;
    private int emptyRow;
    private int enableAnimationsRow;
    private int extraHeight;
    private View extraHeightView;
    private int languageRow;
    private ListAdapter listAdapter;
    private ListView listView;
    private int mediaDownloadSection;
    private int mediaDownloadSection2;
    private int messagesSectionRow;
    private int messagesSectionRow2;
    private int mobileDownloadRow;
    private TextView nameTextView;
    private int notificationRow;
    private int numberRow;
    private int numberSectionRow;
    private TextView onlineTextView;
    private int overscrollRow;
    private int privacyPolicyRow;
    private int privacyRow;
    private int raiseToSpeakRow;
    private int roamingDownloadRow;
    private int rowCount;
    private int saveToGalleryRow;
    private int sendByEnterRow;
    private int sendLogsRow;
    private int settingsSectionRow;
    private int settingsSectionRow2;
    private View shadowView;
    private int stickersRow;
    private int supportSectionRow;
    private int supportSectionRow2;
    private int switchBackendButtonRow;
    private int telegramFaqRow;
    private int textSizeRow;
    private int usernameRow;
    private int versionRow;
    private int wifiDownloadRow;
    private ImageView writeButton;
    private AnimatorSet writeButtonAnimation;

    /* renamed from: org.telegram.ui.SettingsActivity.3 */
    class C14363 extends FrameLayout {
        C14363(Context x0) {
            super(x0);
        }

        protected boolean drawChild(@NonNull Canvas canvas, @NonNull View child, long drawingTime) {
            if (child != SettingsActivity.this.listView) {
                return super.drawChild(canvas, child, drawingTime);
            }
            boolean result = super.drawChild(canvas, child, drawingTime);
            if (SettingsActivity.this.parentLayout == null) {
                return result;
            }
            int actionBarHeight = 0;
            int childCount = getChildCount();
            for (int a = 0; a < childCount; a += SettingsActivity.edit_name) {
                View view = getChildAt(a);
                if (view != child && (view instanceof ActionBar) && view.getVisibility() == 0) {
                    if (((ActionBar) view).getCastShadows()) {
                        actionBarHeight = view.getMeasuredHeight();
                    }
                    SettingsActivity.this.parentLayout.drawHeaderShadow(canvas, actionBarHeight);
                    return result;
                }
            }
            SettingsActivity.this.parentLayout.drawHeaderShadow(canvas, actionBarHeight);
            return result;
        }
    }

    /* renamed from: org.telegram.ui.SettingsActivity.4 */
    class C14434 implements OnItemClickListener {

        /* renamed from: org.telegram.ui.SettingsActivity.4.1 */
        class C14371 implements OnClickListener {
            final /* synthetic */ NumberPicker val$numberPicker;

            C14371(NumberPicker numberPicker) {
                this.val$numberPicker = numberPicker;
            }

            public void onClick(DialogInterface dialog, int which) {
                Editor editor = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0).edit();
                editor.putInt("fons_size", this.val$numberPicker.getValue());
                MessagesController.getInstance().fontSize = this.val$numberPicker.getValue();
                editor.commit();
                if (SettingsActivity.this.listView != null) {
                    SettingsActivity.this.listView.invalidateViews();
                }
            }
        }

        /* renamed from: org.telegram.ui.SettingsActivity.4.2 */
        class C14382 implements OnClickListener {
            C14382() {
            }

            public void onClick(DialogInterface dialogInterface, int i) {
                SettingsActivity.this.performAskAQuestion();
            }
        }

        /* renamed from: org.telegram.ui.SettingsActivity.4.3 */
        class C14393 implements OnClickListener {
            C14393() {
            }

            public void onClick(DialogInterface dialogInterface, int i) {
                ConnectionsManager.getInstance().switchBackend();
            }
        }

        /* renamed from: org.telegram.ui.SettingsActivity.4.4 */
        class C14404 implements OnClickListener {
            C14404() {
            }

            public void onClick(DialogInterface dialog, int which) {
                Editor editor = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0).edit();
                editor.putInt("sortContactsBy", which);
                editor.commit();
                if (SettingsActivity.this.listView != null) {
                    SettingsActivity.this.listView.invalidateViews();
                }
            }
        }

        /* renamed from: org.telegram.ui.SettingsActivity.4.5 */
        class C14415 implements View.OnClickListener {
            final /* synthetic */ boolean[] val$maskValues;

            C14415(boolean[] zArr) {
                this.val$maskValues = zArr;
            }

            public void onClick(View v) {
                CheckBoxCell cell = (CheckBoxCell) v;
                int num = ((Integer) cell.getTag()).intValue();
                this.val$maskValues[num] = !this.val$maskValues[num];
                cell.setChecked(this.val$maskValues[num], true);
            }
        }

        /* renamed from: org.telegram.ui.SettingsActivity.4.6 */
        class C14426 implements View.OnClickListener {
            final /* synthetic */ int val$i;
            final /* synthetic */ boolean[] val$maskValues;

            C14426(boolean[] zArr, int i) {
                this.val$maskValues = zArr;
                this.val$i = i;
            }

            public void onClick(View v) {
                try {
                    if (SettingsActivity.this.visibleDialog != null) {
                        SettingsActivity.this.visibleDialog.dismiss();
                    }
                } catch (Throwable e) {
                    FileLog.m13e("tmessages", e);
                }
                int newMask = 0;
                for (int a = 0; a < 6; a += SettingsActivity.edit_name) {
                    if (this.val$maskValues[a]) {
                        if (a == 0) {
                            newMask |= SettingsActivity.edit_name;
                        } else if (a == SettingsActivity.edit_name) {
                            newMask |= SettingsActivity.logout;
                        } else if (a == SettingsActivity.logout) {
                            newMask |= 4;
                        } else if (a == 3) {
                            newMask |= 8;
                        } else if (a == 4) {
                            newMask |= 16;
                        } else if (a == 5) {
                            newMask |= 32;
                        }
                    }
                }
                Editor editor = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0).edit();
                if (this.val$i == SettingsActivity.this.mobileDownloadRow) {
                    editor.putInt("mobileDataDownloadMask", newMask);
                    MediaController.getInstance().mobileDataDownloadMask = newMask;
                } else if (this.val$i == SettingsActivity.this.wifiDownloadRow) {
                    editor.putInt("wifiDownloadMask", newMask);
                    MediaController.getInstance().wifiDownloadMask = newMask;
                } else if (this.val$i == SettingsActivity.this.roamingDownloadRow) {
                    editor.putInt("roamingDownloadMask", newMask);
                    MediaController.getInstance().roamingDownloadMask = newMask;
                }
                editor.commit();
                if (SettingsActivity.this.listView != null) {
                    SettingsActivity.this.listView.invalidateViews();
                }
            }
        }

        C14434() {
        }

        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            if (i == SettingsActivity.this.textSizeRow) {
                if (SettingsActivity.this.getParentActivity() != null) {
                    Builder builder = new Builder(SettingsActivity.this.getParentActivity());
                    builder.setTitle(LocaleController.getString("TextSize", C0691R.string.TextSize));
                    NumberPicker numberPicker = new NumberPicker(SettingsActivity.this.getParentActivity());
                    numberPicker.setMinValue(12);
                    numberPicker.setMaxValue(30);
                    numberPicker.setValue(MessagesController.getInstance().fontSize);
                    builder.setView(numberPicker);
                    builder.setNegativeButton(LocaleController.getString("Done", C0691R.string.Done), new C14371(numberPicker));
                    SettingsActivity.this.showDialog(builder.create());
                    return;
                }
                return;
            }
            if (i == SettingsActivity.this.enableAnimationsRow) {
                SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0);
                boolean animations = preferences.getBoolean("view_animations", true);
                Editor editor = preferences.edit();
                editor.putBoolean("view_animations", !animations);
                editor.commit();
                if (view instanceof TextCheckCell) {
                    ((TextCheckCell) view).setChecked(!animations);
                    return;
                }
                return;
            }
            if (i == SettingsActivity.this.notificationRow) {
                SettingsActivity.this.presentFragment(new NotificationsSettingsActivity());
                return;
            }
            if (i == SettingsActivity.this.backgroundRow) {
                SettingsActivity.this.presentFragment(new WallpapersActivity());
                return;
            }
            if (i == SettingsActivity.this.askQuestionRow) {
                if (SettingsActivity.this.getParentActivity() != null) {
                    TextView message = new TextView(SettingsActivity.this.getParentActivity());
                    message.setText(Html.fromHtml(LocaleController.getString("AskAQuestionInfo", C0691R.string.AskAQuestionInfo)));
                    message.setTextSize(18.0f);
                    message.setLinkTextColor(Theme.MSG_LINK_TEXT_COLOR);
                    message.setPadding(AndroidUtilities.dp(8.0f), AndroidUtilities.dp(5.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(6.0f));
                    message.setMovementMethod(new LinkMovementMethodMy());
                    builder = new Builder(SettingsActivity.this.getParentActivity());
                    builder.setView(message);
                    builder.setPositiveButton(LocaleController.getString("AskButton", C0691R.string.AskButton), new C14382());
                    builder.setNegativeButton(LocaleController.getString("Cancel", C0691R.string.Cancel), null);
                    SettingsActivity.this.showDialog(builder.create());
                    return;
                }
                return;
            }
            if (i == SettingsActivity.this.sendLogsRow) {
                SettingsActivity.this.sendLogs();
                return;
            }
            if (i == SettingsActivity.this.clearLogsRow) {
                FileLog.cleanupLogs();
                return;
            }
            if (i == SettingsActivity.this.sendByEnterRow) {
                preferences = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0);
                boolean send = preferences.getBoolean("send_by_enter", false);
                editor = preferences.edit();
                editor.putBoolean("send_by_enter", !send);
                editor.commit();
                if (view instanceof TextCheckCell) {
                    ((TextCheckCell) view).setChecked(!send);
                    return;
                }
                return;
            }
            if (i == SettingsActivity.this.raiseToSpeakRow) {
                MediaController.getInstance().toogleRaiseToSpeak();
                if (view instanceof TextCheckCell) {
                    ((TextCheckCell) view).setChecked(MediaController.getInstance().canRaiseToSpeak());
                    return;
                }
                return;
            }
            if (i == SettingsActivity.this.autoplayGifsRow) {
                MediaController.getInstance().toggleAutoplayGifs();
                if (view instanceof TextCheckCell) {
                    ((TextCheckCell) view).setChecked(MediaController.getInstance().canAutoplayGifs());
                    return;
                }
                return;
            }
            if (i == SettingsActivity.this.saveToGalleryRow) {
                MediaController.getInstance().toggleSaveToGallery();
                if (view instanceof TextCheckCell) {
                    ((TextCheckCell) view).setChecked(MediaController.getInstance().canSaveToGallery());
                    return;
                }
                return;
            }
            if (i == SettingsActivity.this.customTabsRow) {
                MediaController.getInstance().toggleCustomTabs();
                if (view instanceof TextCheckCell) {
                    ((TextCheckCell) view).setChecked(MediaController.getInstance().canCustomTabs());
                    return;
                }
                return;
            }
            if (i == SettingsActivity.this.directShareRow) {
                MediaController.getInstance().toggleDirectShare();
                if (view instanceof TextCheckCell) {
                    ((TextCheckCell) view).setChecked(MediaController.getInstance().canDirectShare());
                    return;
                }
                return;
            }
            if (i == SettingsActivity.this.privacyRow) {
                SettingsActivity.this.presentFragment(new PrivacySettingsActivity());
                return;
            }
            if (i == SettingsActivity.this.languageRow) {
                SettingsActivity.this.presentFragment(new LanguageSelectActivity());
                return;
            }
            if (i == SettingsActivity.this.switchBackendButtonRow) {
                if (SettingsActivity.this.getParentActivity() != null) {
                    builder = new Builder(SettingsActivity.this.getParentActivity());
                    builder.setMessage(LocaleController.getString("AreYouSure", C0691R.string.AreYouSure));
                    builder.setTitle(LocaleController.getString("AppName", C0691R.string.AppName));
                    builder.setPositiveButton(LocaleController.getString("OK", C0691R.string.OK), new C14393());
                    builder.setNegativeButton(LocaleController.getString("Cancel", C0691R.string.Cancel), null);
                    SettingsActivity.this.showDialog(builder.create());
                    return;
                }
                return;
            }
            if (i == SettingsActivity.this.telegramFaqRow) {
                Browser.openUrl(SettingsActivity.this.getParentActivity(), LocaleController.getString("TelegramFaqUrl", C0691R.string.TelegramFaqUrl));
                return;
            }
            if (i == SettingsActivity.this.privacyPolicyRow) {
                Browser.openUrl(SettingsActivity.this.getParentActivity(), LocaleController.getString("PrivacyPolicyUrl", C0691R.string.PrivacyPolicyUrl));
                return;
            }
            if (i != SettingsActivity.this.contactsReimportRow) {
                if (i == SettingsActivity.this.contactsSortRow) {
                    if (SettingsActivity.this.getParentActivity() != null) {
                        builder = new Builder(SettingsActivity.this.getParentActivity());
                        builder.setTitle(LocaleController.getString("SortBy", C0691R.string.SortBy));
                        builder.setItems(new CharSequence[]{LocaleController.getString("Default", C0691R.string.Default), LocaleController.getString("SortFirstName", C0691R.string.SortFirstName), LocaleController.getString("SortLastName", C0691R.string.SortLastName)}, new C14404());
                        builder.setNegativeButton(LocaleController.getString("Cancel", C0691R.string.Cancel), null);
                        SettingsActivity.this.showDialog(builder.create());
                        return;
                    }
                    return;
                }
                if (i != SettingsActivity.this.wifiDownloadRow) {
                    if (i != SettingsActivity.this.mobileDownloadRow) {
                        if (i != SettingsActivity.this.roamingDownloadRow) {
                            if (i == SettingsActivity.this.usernameRow) {
                                SettingsActivity.this.presentFragment(new ChangeUsernameActivity());
                                return;
                            }
                            if (i == SettingsActivity.this.numberRow) {
                                SettingsActivity.this.presentFragment(new ChangePhoneHelpActivity());
                                return;
                            }
                            if (i == SettingsActivity.this.stickersRow) {
                                SettingsActivity.this.presentFragment(new StickersActivity());
                                return;
                            }
                            if (i == SettingsActivity.this.cacheRow) {
                                SettingsActivity.this.presentFragment(new CacheControlActivity());
                                return;
                            }
                            return;
                        }
                    }
                }
                if (SettingsActivity.this.getParentActivity() != null) {
                    boolean[] maskValues = new boolean[6];
                    BottomSheet.Builder builder2 = new BottomSheet.Builder(SettingsActivity.this.getParentActivity());
                    int mask = 0;
                    if (i == SettingsActivity.this.mobileDownloadRow) {
                        mask = MediaController.getInstance().mobileDataDownloadMask;
                    } else {
                        if (i == SettingsActivity.this.wifiDownloadRow) {
                            mask = MediaController.getInstance().wifiDownloadMask;
                        } else {
                            if (i == SettingsActivity.this.roamingDownloadRow) {
                                mask = MediaController.getInstance().roamingDownloadMask;
                            }
                        }
                    }
                    builder2.setApplyTopPadding(false);
                    builder2.setApplyBottomPadding(false);
                    LinearLayout linearLayout = new LinearLayout(SettingsActivity.this.getParentActivity());
                    linearLayout.setOrientation(SettingsActivity.edit_name);
                    for (int a = 0; a < 6; a += SettingsActivity.edit_name) {
                        String name = null;
                        if (a == 0) {
                            maskValues[a] = (mask & SettingsActivity.edit_name) != 0;
                            name = LocaleController.getString("AttachPhoto", C0691R.string.AttachPhoto);
                        } else if (a == SettingsActivity.edit_name) {
                            maskValues[a] = (mask & SettingsActivity.logout) != 0;
                            name = LocaleController.getString("AttachAudio", C0691R.string.AttachAudio);
                        } else if (a == SettingsActivity.logout) {
                            maskValues[a] = (mask & 4) != 0;
                            name = LocaleController.getString("AttachVideo", C0691R.string.AttachVideo);
                        } else if (a == 3) {
                            maskValues[a] = (mask & 8) != 0;
                            name = LocaleController.getString("AttachDocument", C0691R.string.AttachDocument);
                        } else if (a == 4) {
                            maskValues[a] = (mask & 16) != 0;
                            name = LocaleController.getString("AttachMusic", C0691R.string.AttachMusic);
                        } else if (a == 5) {
                            maskValues[a] = (mask & 32) != 0;
                            name = LocaleController.getString("AttachGif", C0691R.string.AttachGif);
                        }
                        CheckBoxCell checkBoxCell = new CheckBoxCell(SettingsActivity.this.getParentActivity());
                        checkBoxCell.setTag(Integer.valueOf(a));
                        checkBoxCell.setBackgroundResource(C0691R.drawable.list_selector);
                        linearLayout.addView(checkBoxCell, LayoutHelper.createLinear(-1, 48));
                        checkBoxCell.setText(name, TtmlNode.ANONYMOUS_REGION_ID, maskValues[a], true);
                        checkBoxCell.setOnClickListener(new C14415(maskValues));
                    }
                    BottomSheetCell cell = new BottomSheetCell(SettingsActivity.this.getParentActivity(), SettingsActivity.edit_name);
                    cell.setBackgroundResource(C0691R.drawable.list_selector);
                    cell.setTextAndIcon(LocaleController.getString("Save", C0691R.string.Save).toUpperCase(), 0);
                    cell.setTextColor(Theme.AUTODOWNLOAD_SHEET_SAVE_TEXT_COLOR);
                    cell.setOnClickListener(new C14426(maskValues, i));
                    linearLayout.addView(cell, LayoutHelper.createLinear(-1, 48));
                    builder2.setCustomView(linearLayout);
                    SettingsActivity.this.showDialog(builder2.create());
                }
            }
        }
    }

    /* renamed from: org.telegram.ui.SettingsActivity.5 */
    class C14455 implements OnItemLongClickListener {
        private int pressCount;

        /* renamed from: org.telegram.ui.SettingsActivity.5.1 */
        class C14441 implements OnClickListener {
            C14441() {
            }

            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    ContactsController.getInstance().forceImportContacts();
                } else if (which == SettingsActivity.edit_name) {
                    ContactsController.getInstance().loadContacts(false, true);
                }
            }
        }

        C14455() {
            this.pressCount = 0;
        }

        public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
            if (position != SettingsActivity.this.versionRow) {
                return false;
            }
            this.pressCount += SettingsActivity.edit_name;
            if (this.pressCount >= SettingsActivity.logout) {
                Builder builder = new Builder(SettingsActivity.this.getParentActivity());
                builder.setTitle("Debug Menu");
                CharSequence[] charSequenceArr = new CharSequence[SettingsActivity.logout];
                charSequenceArr[0] = "Import Contacts";
                charSequenceArr[SettingsActivity.edit_name] = "Reload Contacts";
                builder.setItems(charSequenceArr, new C14441());
                builder.setNegativeButton(LocaleController.getString("Cancel", C0691R.string.Cancel), null);
                SettingsActivity.this.showDialog(builder.create());
                return true;
            }
            try {
                Toast.makeText(SettingsActivity.this.getParentActivity(), "\u00af\\_(\u30c4)_/\u00af", 0).show();
                return true;
            } catch (Throwable e) {
                FileLog.m13e("tmessages", e);
                return true;
            }
        }
    }

    /* renamed from: org.telegram.ui.SettingsActivity.6 */
    class C14466 implements View.OnClickListener {
        C14466() {
        }

        public void onClick(View v) {
            User user = MessagesController.getInstance().getUser(Integer.valueOf(UserConfig.getClientUserId()));
            if (user != null && user.photo != null && user.photo.photo_big != null) {
                PhotoViewer.getInstance().setParentActivity(SettingsActivity.this.getParentActivity());
                PhotoViewer.getInstance().openPhoto(user.photo.photo_big, SettingsActivity.this);
            }
        }
    }

    /* renamed from: org.telegram.ui.SettingsActivity.7 */
    class C14477 extends ViewOutlineProvider {
        C14477() {
        }

        @SuppressLint({"NewApi"})
        public void getOutline(View view, Outline outline) {
            outline.setOval(0, 0, AndroidUtilities.dp(56.0f), AndroidUtilities.dp(56.0f));
        }
    }

    /* renamed from: org.telegram.ui.SettingsActivity.8 */
    class C14498 implements View.OnClickListener {

        /* renamed from: org.telegram.ui.SettingsActivity.8.1 */
        class C14481 implements OnClickListener {
            C14481() {
            }

            public void onClick(DialogInterface dialogInterface, int i) {
                if (i == 0) {
                    SettingsActivity.this.avatarUpdater.openCamera();
                } else if (i == SettingsActivity.edit_name) {
                    SettingsActivity.this.avatarUpdater.openGallery();
                } else if (i == SettingsActivity.logout) {
                    MessagesController.getInstance().deleteUserPhoto(null);
                }
            }
        }

        C14498() {
        }

        public void onClick(View v) {
            if (SettingsActivity.this.getParentActivity() != null) {
                Builder builder = new Builder(SettingsActivity.this.getParentActivity());
                User user = MessagesController.getInstance().getUser(Integer.valueOf(UserConfig.getClientUserId()));
                if (user == null) {
                    user = UserConfig.getCurrentUser();
                }
                if (user != null) {
                    CharSequence[] items;
                    boolean fullMenu = false;
                    if (user.photo == null || user.photo.photo_big == null || (user.photo instanceof TL_userProfilePhotoEmpty)) {
                        items = new CharSequence[SettingsActivity.logout];
                        items[0] = LocaleController.getString("FromCamera", C0691R.string.FromCamera);
                        items[SettingsActivity.edit_name] = LocaleController.getString("FromGalley", C0691R.string.FromGalley);
                    } else {
                        items = new CharSequence[]{LocaleController.getString("FromCamera", C0691R.string.FromCamera), LocaleController.getString("FromGalley", C0691R.string.FromGalley), LocaleController.getString("DeletePhoto", C0691R.string.DeletePhoto)};
                        fullMenu = true;
                    }
                    boolean full = fullMenu;
                    builder.setItems(items, new C14481());
                    SettingsActivity.this.showDialog(builder.create());
                }
            }
        }
    }

    /* renamed from: org.telegram.ui.SettingsActivity.9 */
    class C14509 implements OnScrollListener {
        C14509() {
        }

        public void onScrollStateChanged(AbsListView view, int scrollState) {
        }

        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            int i = 0;
            if (totalItemCount != 0) {
                int height = 0;
                View child = view.getChildAt(0);
                if (child != null) {
                    if (firstVisibleItem == 0) {
                        int dp = AndroidUtilities.dp(88.0f);
                        if (child.getTop() < 0) {
                            i = child.getTop();
                        }
                        height = dp + i;
                    }
                    if (SettingsActivity.this.extraHeight != height) {
                        SettingsActivity.this.extraHeight = height;
                        SettingsActivity.this.needLayout();
                    }
                }
            }
        }
    }

    private static class LinkMovementMethodMy extends LinkMovementMethod {
        private LinkMovementMethodMy() {
        }

        public boolean onTouchEvent(@NonNull TextView widget, @NonNull Spannable buffer, @NonNull MotionEvent event) {
            try {
                return super.onTouchEvent(widget, buffer, event);
            } catch (Throwable e) {
                FileLog.m13e("tmessages", e);
                return false;
            }
        }
    }

    /* renamed from: org.telegram.ui.SettingsActivity.10 */
    class AnonymousClass10 implements RequestDelegate {
        final /* synthetic */ SharedPreferences val$preferences;
        final /* synthetic */ ProgressDialog val$progressDialog;

        /* renamed from: org.telegram.ui.SettingsActivity.10.1 */
        class C14331 implements Runnable {
            final /* synthetic */ TL_help_support val$res;

            C14331(TL_help_support tL_help_support) {
                this.val$res = tL_help_support;
            }

            public void run() {
                Editor editor = AnonymousClass10.this.val$preferences.edit();
                editor.putInt("support_id", this.val$res.user.id);
                SerializedData data = new SerializedData();
                this.val$res.user.serializeToStream(data);
                editor.putString("support_user", Base64.encodeToString(data.toByteArray(), 0));
                editor.commit();
                data.cleanup();
                try {
                    AnonymousClass10.this.val$progressDialog.dismiss();
                } catch (Throwable e) {
                    FileLog.m13e("tmessages", e);
                }
                ArrayList<User> users = new ArrayList();
                users.add(this.val$res.user);
                MessagesStorage.getInstance().putUsersAndChats(users, null, true, true);
                MessagesController.getInstance().putUser(this.val$res.user, false);
                Bundle args = new Bundle();
                args.putInt("user_id", this.val$res.user.id);
                SettingsActivity.this.presentFragment(new ChatActivity(args));
            }
        }

        /* renamed from: org.telegram.ui.SettingsActivity.10.2 */
        class C14342 implements Runnable {
            C14342() {
            }

            public void run() {
                try {
                    AnonymousClass10.this.val$progressDialog.dismiss();
                } catch (Throwable e) {
                    FileLog.m13e("tmessages", e);
                }
            }
        }

        AnonymousClass10(SharedPreferences sharedPreferences, ProgressDialog progressDialog) {
            this.val$preferences = sharedPreferences;
            this.val$progressDialog = progressDialog;
        }

        public void run(TLObject response, TL_error error) {
            if (error == null) {
                AndroidUtilities.runOnUIThread(new C14331((TL_help_support) response));
            } else {
                AndroidUtilities.runOnUIThread(new C14342());
            }
        }
    }

    /* renamed from: org.telegram.ui.SettingsActivity.11 */
    class AnonymousClass11 extends AnimatorListenerAdapterProxy {
        final /* synthetic */ boolean val$setVisible;

        AnonymousClass11(boolean z) {
            this.val$setVisible = z;
        }

        public void onAnimationEnd(Animator animation) {
            if (SettingsActivity.this.writeButtonAnimation != null && SettingsActivity.this.writeButtonAnimation.equals(animation)) {
                SettingsActivity.this.writeButton.setVisibility(this.val$setVisible ? 0 : 8);
                SettingsActivity.this.writeButtonAnimation = null;
            }
        }
    }

    /* renamed from: org.telegram.ui.SettingsActivity.1 */
    class C19481 implements AvatarUpdaterDelegate {

        /* renamed from: org.telegram.ui.SettingsActivity.1.1 */
        class C19471 implements RequestDelegate {

            /* renamed from: org.telegram.ui.SettingsActivity.1.1.1 */
            class C14321 implements Runnable {
                C14321() {
                }

                public void run() {
                    NotificationCenter instance = NotificationCenter.getInstance();
                    int i = NotificationCenter.updateInterfaces;
                    Object[] objArr = new Object[SettingsActivity.edit_name];
                    objArr[0] = Integer.valueOf(MessagesController.UPDATE_MASK_ALL);
                    instance.postNotificationName(i, objArr);
                    NotificationCenter.getInstance().postNotificationName(NotificationCenter.mainUserInfoChanged, new Object[0]);
                    UserConfig.saveConfig(true);
                }
            }

            C19471() {
            }

            public void run(TLObject response, TL_error error) {
                if (error == null) {
                    User user = MessagesController.getInstance().getUser(Integer.valueOf(UserConfig.getClientUserId()));
                    if (user == null) {
                        user = UserConfig.getCurrentUser();
                        if (user != null) {
                            MessagesController.getInstance().putUser(user, false);
                        } else {
                            return;
                        }
                    }
                    UserConfig.setCurrentUser(user);
                    TL_photos_photo photo = (TL_photos_photo) response;
                    ArrayList<PhotoSize> sizes = photo.photo.sizes;
                    PhotoSize smallSize = FileLoader.getClosestPhotoSizeWithSize(sizes, 100);
                    PhotoSize bigSize = FileLoader.getClosestPhotoSizeWithSize(sizes, 1000);
                    user.photo = new TL_userProfilePhoto();
                    user.photo.photo_id = photo.photo.id;
                    if (smallSize != null) {
                        user.photo.photo_small = smallSize.location;
                    }
                    if (bigSize != null) {
                        user.photo.photo_big = bigSize.location;
                    } else if (smallSize != null) {
                        user.photo.photo_small = smallSize.location;
                    }
                    MessagesStorage.getInstance().clearUserPhotos(user.id);
                    ArrayList<User> users = new ArrayList();
                    users.add(user);
                    MessagesStorage.getInstance().putUsersAndChats(users, null, false, true);
                    AndroidUtilities.runOnUIThread(new C14321());
                }
            }
        }

        C19481() {
        }

        public void didUploadedPhoto(InputFile file, PhotoSize small, PhotoSize big) {
            TL_photos_uploadProfilePhoto req = new TL_photos_uploadProfilePhoto();
            req.caption = TtmlNode.ANONYMOUS_REGION_ID;
            req.crop = new TL_inputPhotoCropAuto();
            req.file = file;
            req.geo_point = new TL_inputGeoPointEmpty();
            ConnectionsManager.getInstance().sendRequest(req, new C19471());
        }
    }

    /* renamed from: org.telegram.ui.SettingsActivity.2 */
    class C19492 extends ActionBarMenuOnItemClick {

        /* renamed from: org.telegram.ui.SettingsActivity.2.1 */
        class C14351 implements OnClickListener {
            C14351() {
            }

            public void onClick(DialogInterface dialogInterface, int i) {
                MessagesController.getInstance().performLogout(true);
            }
        }

        C19492() {
        }

        public void onItemClick(int id) {
            if (id == -1) {
                SettingsActivity.this.finishFragment();
            } else if (id == SettingsActivity.edit_name) {
                SettingsActivity.this.presentFragment(new ChangeNameActivity());
            } else if (id == SettingsActivity.logout && SettingsActivity.this.getParentActivity() != null) {
                Builder builder = new Builder(SettingsActivity.this.getParentActivity());
                builder.setMessage(LocaleController.getString("AreYouSureLogout", C0691R.string.AreYouSureLogout));
                builder.setTitle(LocaleController.getString("AppName", C0691R.string.AppName));
                builder.setPositiveButton(LocaleController.getString("OK", C0691R.string.OK), new C14351());
                builder.setNegativeButton(LocaleController.getString("Cancel", C0691R.string.Cancel), null);
                SettingsActivity.this.showDialog(builder.create());
            }
        }
    }

    private class ListAdapter extends BaseFragmentAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public boolean areAllItemsEnabled() {
            return false;
        }

        public boolean isEnabled(int i) {
            return i == SettingsActivity.this.textSizeRow || i == SettingsActivity.this.enableAnimationsRow || i == SettingsActivity.this.notificationRow || i == SettingsActivity.this.backgroundRow || i == SettingsActivity.this.numberRow || i == SettingsActivity.this.askQuestionRow || i == SettingsActivity.this.sendLogsRow || i == SettingsActivity.this.sendByEnterRow || i == SettingsActivity.this.autoplayGifsRow || i == SettingsActivity.this.privacyRow || i == SettingsActivity.this.wifiDownloadRow || i == SettingsActivity.this.mobileDownloadRow || i == SettingsActivity.this.clearLogsRow || i == SettingsActivity.this.roamingDownloadRow || i == SettingsActivity.this.languageRow || i == SettingsActivity.this.usernameRow || i == SettingsActivity.this.switchBackendButtonRow || i == SettingsActivity.this.telegramFaqRow || i == SettingsActivity.this.contactsSortRow || i == SettingsActivity.this.contactsReimportRow || i == SettingsActivity.this.saveToGalleryRow || i == SettingsActivity.this.stickersRow || i == SettingsActivity.this.cacheRow || i == SettingsActivity.this.raiseToSpeakRow || i == SettingsActivity.this.privacyPolicyRow || i == SettingsActivity.this.customTabsRow || i == SettingsActivity.this.directShareRow || i == SettingsActivity.this.versionRow;
        }

        public int getCount() {
            return SettingsActivity.this.rowCount;
        }

        public Object getItem(int i) {
            return null;
        }

        public long getItemId(int i) {
            return (long) i;
        }

        public boolean hasStableIds() {
            return false;
        }

        public View getView(int i, View view, ViewGroup viewGroup) {
            int type = getItemViewType(i);
            View emptyCell;
            if (type == 0) {
                if (view == null) {
                    emptyCell = new EmptyCell(this.mContext);
                }
                if (i == SettingsActivity.this.overscrollRow) {
                    ((EmptyCell) view).setHeight(AndroidUtilities.dp(88.0f));
                    return view;
                }
                ((EmptyCell) view).setHeight(AndroidUtilities.dp(16.0f));
                return view;
            } else if (type == SettingsActivity.edit_name) {
                if (view == null) {
                    return new ShadowSectionCell(this.mContext);
                }
                return view;
            } else if (type == SettingsActivity.logout) {
                if (view == null) {
                    emptyCell = new TextSettingsCell(this.mContext);
                }
                TextSettingsCell textCell = (TextSettingsCell) view;
                if (i == SettingsActivity.this.textSizeRow) {
                    int size = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0).getInt("fons_size", AndroidUtilities.isTablet() ? 18 : 16);
                    String string = LocaleController.getString("TextSize", C0691R.string.TextSize);
                    Object[] objArr = new Object[SettingsActivity.edit_name];
                    objArr[0] = Integer.valueOf(size);
                    textCell.setTextAndValue(string, String.format("%d", objArr), true);
                    return view;
                } else if (i == SettingsActivity.this.languageRow) {
                    textCell.setTextAndValue(LocaleController.getString("Language", C0691R.string.Language), LocaleController.getCurrentLanguageName(), true);
                    return view;
                } else if (i == SettingsActivity.this.contactsSortRow) {
                    int sort = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0).getInt("sortContactsBy", 0);
                    if (sort == 0) {
                        value = LocaleController.getString("Default", C0691R.string.Default);
                    } else if (sort == SettingsActivity.edit_name) {
                        value = LocaleController.getString("FirstName", C0691R.string.SortFirstName);
                    } else {
                        value = LocaleController.getString("LastName", C0691R.string.SortLastName);
                    }
                    textCell.setTextAndValue(LocaleController.getString("SortBy", C0691R.string.SortBy), value, true);
                    return view;
                } else if (i == SettingsActivity.this.notificationRow) {
                    textCell.setText(LocaleController.getString("NotificationsAndSounds", C0691R.string.NotificationsAndSounds), true);
                    return view;
                } else if (i == SettingsActivity.this.backgroundRow) {
                    textCell.setText(LocaleController.getString("ChatBackground", C0691R.string.ChatBackground), true);
                    return view;
                } else if (i == SettingsActivity.this.sendLogsRow) {
                    textCell.setText("Send Logs", true);
                    return view;
                } else if (i == SettingsActivity.this.clearLogsRow) {
                    textCell.setText("Clear Logs", true);
                    return view;
                } else if (i == SettingsActivity.this.askQuestionRow) {
                    textCell.setText(LocaleController.getString("AskAQuestion", C0691R.string.AskAQuestion), true);
                    return view;
                } else if (i == SettingsActivity.this.privacyRow) {
                    textCell.setText(LocaleController.getString("PrivacySettings", C0691R.string.PrivacySettings), true);
                    return view;
                } else if (i == SettingsActivity.this.switchBackendButtonRow) {
                    textCell.setText("Switch Backend", true);
                    return view;
                } else if (i == SettingsActivity.this.telegramFaqRow) {
                    textCell.setText(LocaleController.getString("TelegramFAQ", C0691R.string.TelegramFaq), true);
                    return view;
                } else if (i == SettingsActivity.this.contactsReimportRow) {
                    textCell.setText(LocaleController.getString("ImportContacts", C0691R.string.ImportContacts), true);
                    return view;
                } else if (i == SettingsActivity.this.stickersRow) {
                    textCell.setText(LocaleController.getString("Stickers", C0691R.string.Stickers), true);
                    return view;
                } else if (i == SettingsActivity.this.cacheRow) {
                    textCell.setText(LocaleController.getString("CacheSettings", C0691R.string.CacheSettings), true);
                    return view;
                } else if (i != SettingsActivity.this.privacyPolicyRow) {
                    return view;
                } else {
                    textCell.setText(LocaleController.getString("PrivacyPolicy", C0691R.string.PrivacyPolicy), true);
                    return view;
                }
            } else if (type == 3) {
                if (view == null) {
                    emptyCell = new TextCheckCell(this.mContext);
                }
                TextCheckCell textCell2 = (TextCheckCell) view;
                preferences = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0);
                if (i == SettingsActivity.this.enableAnimationsRow) {
                    textCell2.setTextAndCheck(LocaleController.getString("EnableAnimations", C0691R.string.EnableAnimations), preferences.getBoolean("view_animations", true), false);
                    return view;
                } else if (i == SettingsActivity.this.sendByEnterRow) {
                    textCell2.setTextAndCheck(LocaleController.getString("SendByEnter", C0691R.string.SendByEnter), preferences.getBoolean("send_by_enter", false), false);
                    return view;
                } else if (i == SettingsActivity.this.saveToGalleryRow) {
                    textCell2.setTextAndCheck(LocaleController.getString("SaveToGallerySettings", C0691R.string.SaveToGallerySettings), MediaController.getInstance().canSaveToGallery(), false);
                    return view;
                } else if (i == SettingsActivity.this.autoplayGifsRow) {
                    textCell2.setTextAndCheck(LocaleController.getString("AutoplayGifs", C0691R.string.AutoplayGifs), MediaController.getInstance().canAutoplayGifs(), true);
                    return view;
                } else if (i == SettingsActivity.this.raiseToSpeakRow) {
                    textCell2.setTextAndCheck(LocaleController.getString("RaiseToSpeak", C0691R.string.RaiseToSpeak), MediaController.getInstance().canRaiseToSpeak(), true);
                    return view;
                } else if (i == SettingsActivity.this.customTabsRow) {
                    textCell2.setTextAndValueAndCheck(LocaleController.getString("ChromeCustomTabs", C0691R.string.ChromeCustomTabs), LocaleController.getString("ChromeCustomTabsInfo", C0691R.string.ChromeCustomTabsInfo), MediaController.getInstance().canCustomTabs(), false, true);
                    return view;
                } else if (i != SettingsActivity.this.directShareRow) {
                    return view;
                } else {
                    textCell2.setTextAndValueAndCheck(LocaleController.getString("DirectShare", C0691R.string.DirectShare), LocaleController.getString("DirectShareInfo", C0691R.string.DirectShareInfo), MediaController.getInstance().canDirectShare(), false, true);
                    return view;
                }
            } else if (type == 4) {
                if (view == null) {
                    emptyCell = new HeaderCell(this.mContext);
                }
                if (i == SettingsActivity.this.settingsSectionRow2) {
                    ((HeaderCell) view).setText(LocaleController.getString("SETTINGS", C0691R.string.SETTINGS));
                    return view;
                } else if (i == SettingsActivity.this.supportSectionRow2) {
                    ((HeaderCell) view).setText(LocaleController.getString("Support", C0691R.string.Support));
                    return view;
                } else if (i == SettingsActivity.this.messagesSectionRow2) {
                    ((HeaderCell) view).setText(LocaleController.getString("MessagesSettings", C0691R.string.MessagesSettings));
                    return view;
                } else if (i == SettingsActivity.this.mediaDownloadSection2) {
                    ((HeaderCell) view).setText(LocaleController.getString("AutomaticMediaDownload", C0691R.string.AutomaticMediaDownload));
                    return view;
                } else if (i != SettingsActivity.this.numberSectionRow) {
                    return view;
                } else {
                    ((HeaderCell) view).setText(LocaleController.getString("Info", C0691R.string.Info));
                    return view;
                }
            } else if (type == 5) {
                if (view != null) {
                    return view;
                }
                emptyCell = new TextInfoCell(this.mContext);
                try {
                    PackageInfo pInfo = ApplicationLoader.applicationContext.getPackageManager().getPackageInfo(ApplicationLoader.applicationContext.getPackageName(), 0);
                    int code = pInfo.versionCode / 10;
                    String abi = TtmlNode.ANONYMOUS_REGION_ID;
                    switch (pInfo.versionCode % 10) {
                        case VideoPlayer.TRACK_DEFAULT /*0*/:
                            abi = "arm";
                            break;
                        case SettingsActivity.edit_name /*1*/:
                            abi = "arm-v7a";
                            break;
                        case SettingsActivity.logout /*2*/:
                            abi = "x86";
                            break;
                        case VideoPlayer.STATE_BUFFERING /*3*/:
                            abi = "universal";
                            break;
                    }
                    TextInfoCell textInfoCell = (TextInfoCell) emptyCell;
                    r5 = new Object[3];
                    r5[0] = pInfo.versionName;
                    r5[SettingsActivity.edit_name] = Integer.valueOf(code);
                    r5[SettingsActivity.logout] = abi;
                    textInfoCell.setText(String.format(Locale.US, "Telegram for Android v%s (%d) %s", r5));
                    return emptyCell;
                } catch (Throwable e) {
                    FileLog.m13e("tmessages", e);
                    return emptyCell;
                }
            } else if (type != 6) {
                return view;
            } else {
                if (view == null) {
                    emptyCell = new TextDetailSettingsCell(this.mContext);
                }
                TextDetailSettingsCell textCell3 = (TextDetailSettingsCell) view;
                if (i == SettingsActivity.this.mobileDownloadRow || i == SettingsActivity.this.wifiDownloadRow || i == SettingsActivity.this.roamingDownloadRow) {
                    int mask;
                    preferences = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0);
                    if (i == SettingsActivity.this.mobileDownloadRow) {
                        value = LocaleController.getString("WhenUsingMobileData", C0691R.string.WhenUsingMobileData);
                        mask = MediaController.getInstance().mobileDataDownloadMask;
                    } else if (i == SettingsActivity.this.wifiDownloadRow) {
                        value = LocaleController.getString("WhenConnectedOnWiFi", C0691R.string.WhenConnectedOnWiFi);
                        mask = MediaController.getInstance().wifiDownloadMask;
                    } else {
                        value = LocaleController.getString("WhenRoaming", C0691R.string.WhenRoaming);
                        mask = MediaController.getInstance().roamingDownloadMask;
                    }
                    String text = TtmlNode.ANONYMOUS_REGION_ID;
                    if ((mask & SettingsActivity.edit_name) != 0) {
                        text = text + LocaleController.getString("AttachPhoto", C0691R.string.AttachPhoto);
                    }
                    if ((mask & SettingsActivity.logout) != 0) {
                        if (text.length() != 0) {
                            text = text + ", ";
                        }
                        text = text + LocaleController.getString("AttachAudio", C0691R.string.AttachAudio);
                    }
                    if ((mask & 4) != 0) {
                        if (text.length() != 0) {
                            text = text + ", ";
                        }
                        text = text + LocaleController.getString("AttachVideo", C0691R.string.AttachVideo);
                    }
                    if ((mask & 8) != 0) {
                        if (text.length() != 0) {
                            text = text + ", ";
                        }
                        text = text + LocaleController.getString("AttachDocument", C0691R.string.AttachDocument);
                    }
                    if ((mask & 16) != 0) {
                        if (text.length() != 0) {
                            text = text + ", ";
                        }
                        text = text + LocaleController.getString("AttachMusic", C0691R.string.AttachMusic);
                    }
                    if ((mask & 32) != 0) {
                        if (text.length() != 0) {
                            text = text + ", ";
                        }
                        text = text + LocaleController.getString("AttachGif", C0691R.string.AttachGif);
                    }
                    if (text.length() == 0) {
                        text = LocaleController.getString("NoMediaAutoDownload", C0691R.string.NoMediaAutoDownload);
                    }
                    textCell3.setTextAndValue(value, text, true);
                    return view;
                } else if (i == SettingsActivity.this.numberRow) {
                    user = UserConfig.getCurrentUser();
                    if (user == null || user.phone == null || user.phone.length() == 0) {
                        value = LocaleController.getString("NumberUnknown", C0691R.string.NumberUnknown);
                    } else {
                        value = PhoneFormat.getInstance().format("+" + user.phone);
                    }
                    textCell3.setTextAndValue(value, LocaleController.getString("Phone", C0691R.string.Phone), true);
                    return view;
                } else if (i != SettingsActivity.this.usernameRow) {
                    return view;
                } else {
                    user = UserConfig.getCurrentUser();
                    if (user == null || user.username == null || user.username.length() == 0) {
                        value = LocaleController.getString("UsernameEmpty", C0691R.string.UsernameEmpty);
                    } else {
                        value = "@" + user.username;
                    }
                    textCell3.setTextAndValue(value, LocaleController.getString("Username", C0691R.string.Username), false);
                    return view;
                }
            }
        }

        public int getItemViewType(int i) {
            if (i == SettingsActivity.this.emptyRow || i == SettingsActivity.this.overscrollRow) {
                return 0;
            }
            if (i == SettingsActivity.this.settingsSectionRow || i == SettingsActivity.this.supportSectionRow || i == SettingsActivity.this.messagesSectionRow || i == SettingsActivity.this.mediaDownloadSection || i == SettingsActivity.this.contactsSectionRow) {
                return SettingsActivity.edit_name;
            }
            if (i == SettingsActivity.this.enableAnimationsRow || i == SettingsActivity.this.sendByEnterRow || i == SettingsActivity.this.saveToGalleryRow || i == SettingsActivity.this.autoplayGifsRow || i == SettingsActivity.this.raiseToSpeakRow || i == SettingsActivity.this.customTabsRow || i == SettingsActivity.this.directShareRow) {
                return 3;
            }
            if (i == SettingsActivity.this.notificationRow || i == SettingsActivity.this.backgroundRow || i == SettingsActivity.this.askQuestionRow || i == SettingsActivity.this.sendLogsRow || i == SettingsActivity.this.privacyRow || i == SettingsActivity.this.clearLogsRow || i == SettingsActivity.this.switchBackendButtonRow || i == SettingsActivity.this.telegramFaqRow || i == SettingsActivity.this.contactsReimportRow || i == SettingsActivity.this.textSizeRow || i == SettingsActivity.this.languageRow || i == SettingsActivity.this.contactsSortRow || i == SettingsActivity.this.stickersRow || i == SettingsActivity.this.cacheRow || i == SettingsActivity.this.privacyPolicyRow) {
                return SettingsActivity.logout;
            }
            if (i == SettingsActivity.this.versionRow) {
                return 5;
            }
            if (i == SettingsActivity.this.wifiDownloadRow || i == SettingsActivity.this.mobileDownloadRow || i == SettingsActivity.this.roamingDownloadRow || i == SettingsActivity.this.numberRow || i == SettingsActivity.this.usernameRow) {
                return 6;
            }
            if (i == SettingsActivity.this.settingsSectionRow2 || i == SettingsActivity.this.messagesSectionRow2 || i == SettingsActivity.this.supportSectionRow2 || i == SettingsActivity.this.numberSectionRow || i == SettingsActivity.this.mediaDownloadSection2) {
                return 4;
            }
            return SettingsActivity.logout;
        }

        public int getViewTypeCount() {
            return 7;
        }

        public boolean isEmpty() {
            return false;
        }
    }

    public SettingsActivity() {
        this.avatarUpdater = new AvatarUpdater();
    }

    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        this.avatarUpdater.parentFragment = this;
        this.avatarUpdater.delegate = new C19481();
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.updateInterfaces);
        this.rowCount = 0;
        int i = this.rowCount;
        this.rowCount = i + edit_name;
        this.overscrollRow = i;
        i = this.rowCount;
        this.rowCount = i + edit_name;
        this.emptyRow = i;
        i = this.rowCount;
        this.rowCount = i + edit_name;
        this.numberSectionRow = i;
        i = this.rowCount;
        this.rowCount = i + edit_name;
        this.numberRow = i;
        i = this.rowCount;
        this.rowCount = i + edit_name;
        this.usernameRow = i;
        i = this.rowCount;
        this.rowCount = i + edit_name;
        this.settingsSectionRow = i;
        i = this.rowCount;
        this.rowCount = i + edit_name;
        this.settingsSectionRow2 = i;
        i = this.rowCount;
        this.rowCount = i + edit_name;
        this.notificationRow = i;
        i = this.rowCount;
        this.rowCount = i + edit_name;
        this.privacyRow = i;
        i = this.rowCount;
        this.rowCount = i + edit_name;
        this.backgroundRow = i;
        i = this.rowCount;
        this.rowCount = i + edit_name;
        this.languageRow = i;
        i = this.rowCount;
        this.rowCount = i + edit_name;
        this.enableAnimationsRow = i;
        i = this.rowCount;
        this.rowCount = i + edit_name;
        this.mediaDownloadSection = i;
        i = this.rowCount;
        this.rowCount = i + edit_name;
        this.mediaDownloadSection2 = i;
        i = this.rowCount;
        this.rowCount = i + edit_name;
        this.mobileDownloadRow = i;
        i = this.rowCount;
        this.rowCount = i + edit_name;
        this.wifiDownloadRow = i;
        i = this.rowCount;
        this.rowCount = i + edit_name;
        this.roamingDownloadRow = i;
        i = this.rowCount;
        this.rowCount = i + edit_name;
        this.autoplayGifsRow = i;
        i = this.rowCount;
        this.rowCount = i + edit_name;
        this.saveToGalleryRow = i;
        i = this.rowCount;
        this.rowCount = i + edit_name;
        this.messagesSectionRow = i;
        i = this.rowCount;
        this.rowCount = i + edit_name;
        this.messagesSectionRow2 = i;
        i = this.rowCount;
        this.rowCount = i + edit_name;
        this.customTabsRow = i;
        if (VERSION.SDK_INT >= 23) {
            i = this.rowCount;
            this.rowCount = i + edit_name;
            this.directShareRow = i;
        }
        i = this.rowCount;
        this.rowCount = i + edit_name;
        this.textSizeRow = i;
        i = this.rowCount;
        this.rowCount = i + edit_name;
        this.stickersRow = i;
        i = this.rowCount;
        this.rowCount = i + edit_name;
        this.cacheRow = i;
        i = this.rowCount;
        this.rowCount = i + edit_name;
        this.raiseToSpeakRow = i;
        i = this.rowCount;
        this.rowCount = i + edit_name;
        this.sendByEnterRow = i;
        i = this.rowCount;
        this.rowCount = i + edit_name;
        this.supportSectionRow = i;
        i = this.rowCount;
        this.rowCount = i + edit_name;
        this.supportSectionRow2 = i;
        i = this.rowCount;
        this.rowCount = i + edit_name;
        this.askQuestionRow = i;
        i = this.rowCount;
        this.rowCount = i + edit_name;
        this.telegramFaqRow = i;
        i = this.rowCount;
        this.rowCount = i + edit_name;
        this.privacyPolicyRow = i;
        if (BuildVars.DEBUG_VERSION) {
            i = this.rowCount;
            this.rowCount = i + edit_name;
            this.sendLogsRow = i;
            i = this.rowCount;
            this.rowCount = i + edit_name;
            this.clearLogsRow = i;
            i = this.rowCount;
            this.rowCount = i + edit_name;
            this.switchBackendButtonRow = i;
        }
        i = this.rowCount;
        this.rowCount = i + edit_name;
        this.versionRow = i;
        MessagesController.getInstance().loadFullUser(UserConfig.getCurrentUser(), this.classGuid, true);
        return true;
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        if (this.avatarImage != null) {
            this.avatarImage.setImageDrawable(null);
        }
        MessagesController.getInstance().cancelLoadFullUser(UserConfig.getClientUserId());
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.updateInterfaces);
        this.avatarUpdater.clear();
    }

    public View createView(Context context) {
        this.actionBar.setBackgroundColor(AvatarDrawable.getProfileBackColorForId(5));
        this.actionBar.setItemsBackgroundColor(AvatarDrawable.getButtonColorForId(5));
        this.actionBar.setBackButtonImage(C0691R.drawable.ic_ab_back);
        this.actionBar.setAddToContainer(false);
        this.extraHeight = 88;
        if (AndroidUtilities.isTablet()) {
            this.actionBar.setOccupyStatusBar(false);
        }
        this.actionBar.setActionBarMenuOnItemClick(new C19492());
        ActionBarMenuItem item = this.actionBar.createMenu().addItem(0, (int) C0691R.drawable.ic_ab_other);
        item.addSubItem(edit_name, LocaleController.getString("EditName", C0691R.string.EditName), 0);
        item.addSubItem(logout, LocaleController.getString("LogOut", C0691R.string.LogOut), 0);
        this.listAdapter = new ListAdapter(context);
        this.fragmentView = new C14363(context);
        FrameLayout frameLayout = this.fragmentView;
        this.listView = new ListView(context);
        this.listView.setDivider(null);
        this.listView.setDividerHeight(0);
        this.listView.setBackgroundColor(getParentActivity().getResources().getColor(C0691R.color.app_background));
        this.listView.setVerticalScrollBarEnabled(false);
        AndroidUtilities.setListViewEdgeEffectColor(this.listView, AvatarDrawable.getProfileBackColorForId(5));
        frameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1, 51));
        this.listView.setAdapter(this.listAdapter);
        this.listView.setOnItemClickListener(new C14434());
        this.listView.setOnItemLongClickListener(new C14455());
        frameLayout.addView(this.actionBar);
        this.extraHeightView = new View(context);
        this.extraHeightView.setPivotY(0.0f);
        this.extraHeightView.setBackgroundColor(AvatarDrawable.getProfileBackColorForId(5));
        frameLayout.addView(this.extraHeightView, LayoutHelper.createFrame(-1, 88.0f));
        this.shadowView = new View(context);
        this.shadowView.setBackgroundResource(C0691R.drawable.header_shadow);
        frameLayout.addView(this.shadowView, LayoutHelper.createFrame(-1, 3.0f));
        this.avatarImage = new BackupImageView(context);
        this.avatarImage.setRoundRadius(AndroidUtilities.dp(21.0f));
        this.avatarImage.setPivotX(0.0f);
        this.avatarImage.setPivotY(0.0f);
        frameLayout.addView(this.avatarImage, LayoutHelper.createFrame(42, 42.0f, 51, 64.0f, 0.0f, 0.0f, 0.0f));
        this.avatarImage.setOnClickListener(new C14466());
        this.nameTextView = new TextView(context);
        this.nameTextView.setTextColor(-1);
        this.nameTextView.setTextSize(edit_name, 18.0f);
        this.nameTextView.setLines(edit_name);
        this.nameTextView.setMaxLines(edit_name);
        this.nameTextView.setSingleLine(true);
        this.nameTextView.setEllipsize(TruncateAt.END);
        this.nameTextView.setGravity(3);
        this.nameTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.nameTextView.setPivotX(0.0f);
        this.nameTextView.setPivotY(0.0f);
        frameLayout.addView(this.nameTextView, LayoutHelper.createFrame(-2, -2.0f, 51, 118.0f, 0.0f, 48.0f, 0.0f));
        this.onlineTextView = new TextView(context);
        this.onlineTextView.setTextColor(AvatarDrawable.getProfileTextColorForId(5));
        this.onlineTextView.setTextSize(edit_name, 14.0f);
        this.onlineTextView.setLines(edit_name);
        this.onlineTextView.setMaxLines(edit_name);
        this.onlineTextView.setSingleLine(true);
        this.onlineTextView.setEllipsize(TruncateAt.END);
        this.onlineTextView.setGravity(3);
        frameLayout.addView(this.onlineTextView, LayoutHelper.createFrame(-2, -2.0f, 51, 118.0f, 0.0f, 48.0f, 0.0f));
        this.writeButton = new ImageView(context);
        this.writeButton.setBackgroundResource(C0691R.drawable.floating_user_states);
        this.writeButton.setImageResource(C0691R.drawable.floating_camera);
        this.writeButton.setScaleType(ScaleType.CENTER);
        if (VERSION.SDK_INT >= 21) {
            StateListAnimator animator = new StateListAnimator();
            int[] iArr = new int[edit_name];
            iArr[0] = 16842919;
            float[] fArr = new float[logout];
            fArr[0] = (float) AndroidUtilities.dp(2.0f);
            fArr[edit_name] = (float) AndroidUtilities.dp(4.0f);
            animator.addState(iArr, ObjectAnimator.ofFloat(this.writeButton, "translationZ", fArr).setDuration(200));
            iArr = new int[0];
            fArr = new float[logout];
            fArr[0] = (float) AndroidUtilities.dp(4.0f);
            fArr[edit_name] = (float) AndroidUtilities.dp(2.0f);
            animator.addState(iArr, ObjectAnimator.ofFloat(this.writeButton, "translationZ", fArr).setDuration(200));
            this.writeButton.setStateListAnimator(animator);
            this.writeButton.setOutlineProvider(new C14477());
        }
        frameLayout.addView(this.writeButton, LayoutHelper.createFrame(-2, -2.0f, 53, 0.0f, 0.0f, 16.0f, 0.0f));
        this.writeButton.setOnClickListener(new C14498());
        needLayout();
        this.listView.setOnScrollListener(new C14509());
        return this.fragmentView;
    }

    protected void onDialogDismiss(Dialog dialog) {
        MediaController.getInstance().checkAutodownloadSettings();
    }

    public void updatePhotoAtIndex(int index) {
    }

    public PlaceProviderObject getPlaceForPhoto(MessageObject messageObject, FileLocation fileLocation, int index) {
        if (fileLocation == null) {
            return null;
        }
        User user = MessagesController.getInstance().getUser(Integer.valueOf(UserConfig.getClientUserId()));
        if (user == null || user.photo == null || user.photo.photo_big == null) {
            return null;
        }
        FileLocation photoBig = user.photo.photo_big;
        if (photoBig.local_id != fileLocation.local_id || photoBig.volume_id != fileLocation.volume_id || photoBig.dc_id != fileLocation.dc_id) {
            return null;
        }
        int[] coords = new int[logout];
        this.avatarImage.getLocationInWindow(coords);
        PlaceProviderObject object = new PlaceProviderObject();
        object.viewX = coords[0];
        object.viewY = coords[edit_name] - AndroidUtilities.statusBarHeight;
        object.parentView = this.avatarImage;
        object.imageReceiver = this.avatarImage.getImageReceiver();
        object.dialogId = UserConfig.getClientUserId();
        object.thumb = object.imageReceiver.getBitmap();
        object.size = -1;
        object.radius = this.avatarImage.getImageReceiver().getRoundRadius();
        object.scale = this.avatarImage.getScaleX();
        return object;
    }

    public Bitmap getThumbForPhoto(MessageObject messageObject, FileLocation fileLocation, int index) {
        return null;
    }

    public void willSwitchFromPhoto(MessageObject messageObject, FileLocation fileLocation, int index) {
    }

    public void willHidePhotoViewer() {
        this.avatarImage.getImageReceiver().setVisible(true, true);
    }

    public boolean isPhotoChecked(int index) {
        return false;
    }

    public void setPhotoChecked(int index) {
    }

    public boolean cancelButtonPressed() {
        return true;
    }

    public void sendButtonPressed(int index) {
    }

    public int getSelectedCount() {
        return 0;
    }

    private void performAskAQuestion() {
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0);
        int uid = preferences.getInt("support_id", 0);
        User supportUser = null;
        if (uid != 0) {
            supportUser = MessagesController.getInstance().getUser(Integer.valueOf(uid));
            if (supportUser == null) {
                String userString = preferences.getString("support_user", null);
                if (userString != null) {
                    try {
                        byte[] datacentersBytes = Base64.decode(userString, 0);
                        if (datacentersBytes != null) {
                            SerializedData data = new SerializedData(datacentersBytes);
                            supportUser = User.TLdeserialize(data, data.readInt32(false), false);
                            if (supportUser != null && supportUser.id == 333000) {
                                supportUser = null;
                            }
                            data.cleanup();
                        }
                    } catch (Throwable e) {
                        FileLog.m13e("tmessages", e);
                        supportUser = null;
                    }
                }
            }
        }
        if (supportUser == null) {
            ProgressDialog progressDialog = new ProgressDialog(getParentActivity());
            progressDialog.setMessage(LocaleController.getString("Loading", C0691R.string.Loading));
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setCancelable(false);
            progressDialog.show();
            ConnectionsManager.getInstance().sendRequest(new TL_help_getSupport(), new AnonymousClass10(preferences, progressDialog));
            return;
        }
        MessagesController.getInstance().putUser(supportUser, true);
        Bundle args = new Bundle();
        args.putInt("user_id", supportUser.id);
        presentFragment(new ChatActivity(args));
    }

    public void onActivityResultFragment(int requestCode, int resultCode, Intent data) {
        this.avatarUpdater.onActivityResult(requestCode, resultCode, data);
    }

    public void saveSelfArgs(Bundle args) {
        if (this.avatarUpdater != null && this.avatarUpdater.currentPicturePath != null) {
            args.putString("path", this.avatarUpdater.currentPicturePath);
        }
    }

    public void restoreSelfArgs(Bundle args) {
        if (this.avatarUpdater != null) {
            this.avatarUpdater.currentPicturePath = args.getString("path");
        }
    }

    public void didReceivedNotification(int id, Object... args) {
        if (id == NotificationCenter.updateInterfaces) {
            int mask = ((Integer) args[0]).intValue();
            if ((mask & logout) != 0 || (mask & edit_name) != 0) {
                updateUserData();
            }
        }
    }

    public void onResume() {
        super.onResume();
        if (this.listAdapter != null) {
            this.listAdapter.notifyDataSetChanged();
        }
        updateUserData();
        fixLayout();
    }

    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        fixLayout();
    }

    private void needLayout() {
        int newTop = (this.actionBar.getOccupyStatusBar() ? AndroidUtilities.statusBarHeight : 0) + ActionBar.getCurrentActionBarHeight();
        if (this.listView != null) {
            LayoutParams layoutParams = (LayoutParams) this.listView.getLayoutParams();
            if (layoutParams.topMargin != newTop) {
                layoutParams.topMargin = newTop;
                this.listView.setLayoutParams(layoutParams);
                this.extraHeightView.setTranslationY((float) newTop);
            }
        }
        if (this.avatarImage != null) {
            float diff = ((float) this.extraHeight) / ((float) AndroidUtilities.dp(88.0f));
            this.extraHeightView.setScaleY(diff);
            this.shadowView.setTranslationY((float) (this.extraHeight + newTop));
            this.writeButton.setTranslationY((float) ((((this.actionBar.getOccupyStatusBar() ? AndroidUtilities.statusBarHeight : 0) + ActionBar.getCurrentActionBarHeight()) + this.extraHeight) - AndroidUtilities.dp(29.5f)));
            boolean setVisible = diff > DefaultLoadControl.DEFAULT_LOW_BUFFER_LOAD;
            if (setVisible != (this.writeButton.getTag() == null)) {
                if (setVisible) {
                    this.writeButton.setTag(null);
                    this.writeButton.setVisibility(0);
                } else {
                    this.writeButton.setTag(Integer.valueOf(0));
                }
                if (this.writeButtonAnimation != null) {
                    AnimatorSet old = this.writeButtonAnimation;
                    this.writeButtonAnimation = null;
                    old.cancel();
                }
                this.writeButtonAnimation = new AnimatorSet();
                AnimatorSet animatorSet;
                Animator[] animatorArr;
                float[] fArr;
                if (setVisible) {
                    this.writeButtonAnimation.setInterpolator(new DecelerateInterpolator());
                    animatorSet = this.writeButtonAnimation;
                    animatorArr = new Animator[3];
                    fArr = new float[edit_name];
                    fArr[0] = TouchHelperCallback.ALPHA_FULL;
                    animatorArr[0] = ObjectAnimator.ofFloat(this.writeButton, "scaleX", fArr);
                    fArr = new float[edit_name];
                    fArr[0] = TouchHelperCallback.ALPHA_FULL;
                    animatorArr[edit_name] = ObjectAnimator.ofFloat(this.writeButton, "scaleY", fArr);
                    fArr = new float[edit_name];
                    fArr[0] = TouchHelperCallback.ALPHA_FULL;
                    animatorArr[logout] = ObjectAnimator.ofFloat(this.writeButton, "alpha", fArr);
                    animatorSet.playTogether(animatorArr);
                } else {
                    this.writeButtonAnimation.setInterpolator(new AccelerateInterpolator());
                    animatorSet = this.writeButtonAnimation;
                    animatorArr = new Animator[3];
                    fArr = new float[edit_name];
                    fArr[0] = DefaultLoadControl.DEFAULT_LOW_BUFFER_LOAD;
                    animatorArr[0] = ObjectAnimator.ofFloat(this.writeButton, "scaleX", fArr);
                    fArr = new float[edit_name];
                    fArr[0] = DefaultLoadControl.DEFAULT_LOW_BUFFER_LOAD;
                    animatorArr[edit_name] = ObjectAnimator.ofFloat(this.writeButton, "scaleY", fArr);
                    fArr = new float[edit_name];
                    fArr[0] = 0.0f;
                    animatorArr[logout] = ObjectAnimator.ofFloat(this.writeButton, "alpha", fArr);
                    animatorSet.playTogether(animatorArr);
                }
                this.writeButtonAnimation.setDuration(150);
                this.writeButtonAnimation.addListener(new AnonymousClass11(setVisible));
                this.writeButtonAnimation.start();
            }
            this.avatarImage.setScaleX((42.0f + (18.0f * diff)) / 42.0f);
            this.avatarImage.setScaleY((42.0f + (18.0f * diff)) / 42.0f);
            float avatarY = ((((float) (this.actionBar.getOccupyStatusBar() ? AndroidUtilities.statusBarHeight : 0)) + ((((float) ActionBar.getCurrentActionBarHeight()) / 2.0f) * (TouchHelperCallback.ALPHA_FULL + diff))) - (21.0f * AndroidUtilities.density)) + ((27.0f * AndroidUtilities.density) * diff);
            this.avatarImage.setTranslationX(((float) (-AndroidUtilities.dp(47.0f))) * diff);
            this.avatarImage.setTranslationY((float) Math.ceil((double) avatarY));
            this.nameTextView.setTranslationX((-21.0f * AndroidUtilities.density) * diff);
            this.nameTextView.setTranslationY((((float) Math.floor((double) avatarY)) - ((float) Math.ceil((double) AndroidUtilities.density))) + ((float) Math.floor((double) ((7.0f * AndroidUtilities.density) * diff))));
            this.onlineTextView.setTranslationX((-21.0f * AndroidUtilities.density) * diff);
            this.onlineTextView.setTranslationY((((float) Math.floor((double) avatarY)) + ((float) AndroidUtilities.dp(22.0f))) + (((float) Math.floor((double) (11.0f * AndroidUtilities.density))) * diff));
            this.nameTextView.setScaleX(TouchHelperCallback.ALPHA_FULL + (0.12f * diff));
            this.nameTextView.setScaleY(TouchHelperCallback.ALPHA_FULL + (0.12f * diff));
        }
    }

    private void fixLayout() {
        if (this.fragmentView != null) {
            this.fragmentView.getViewTreeObserver().addOnPreDrawListener(new OnPreDrawListener() {
                public boolean onPreDraw() {
                    if (SettingsActivity.this.fragmentView != null) {
                        SettingsActivity.this.needLayout();
                        SettingsActivity.this.fragmentView.getViewTreeObserver().removeOnPreDrawListener(this);
                    }
                    return true;
                }
            });
        }
    }

    private void updateUserData() {
        boolean z = true;
        User user = MessagesController.getInstance().getUser(Integer.valueOf(UserConfig.getClientUserId()));
        TLObject photo = null;
        FileLocation photoBig = null;
        if (user.photo != null) {
            photo = user.photo.photo_small;
            photoBig = user.photo.photo_big;
        }
        Drawable avatarDrawable = new AvatarDrawable(user, true);
        avatarDrawable.setColor(Theme.ACTION_BAR_MAIN_AVATAR_COLOR);
        if (this.avatarImage != null) {
            this.avatarImage.setImage(photo, "50_50", avatarDrawable);
            this.avatarImage.getImageReceiver().setVisible(!PhotoViewer.getInstance().isShowingImage(photoBig), false);
            this.nameTextView.setText(UserObject.getUserName(user));
            this.onlineTextView.setText(LocaleController.getString("Online", C0691R.string.Online));
            ImageReceiver imageReceiver = this.avatarImage.getImageReceiver();
            if (PhotoViewer.getInstance().isShowingImage(photoBig)) {
                z = false;
            }
            imageReceiver.setVisible(z, false);
        }
    }

    private void sendLogs() {
        try {
            ArrayList<Uri> uris = new ArrayList();
            File[] arr$ = new File(ApplicationLoader.applicationContext.getExternalFilesDir(null).getAbsolutePath() + "/logs").listFiles();
            int len$ = arr$.length;
            for (int i$ = 0; i$ < len$; i$ += edit_name) {
                uris.add(Uri.fromFile(arr$[i$]));
            }
            if (!uris.isEmpty()) {
                Intent i = new Intent("android.intent.action.SEND_MULTIPLE");
                i.setType("message/rfc822");
                String[] strArr = new String[edit_name];
                strArr[0] = BuildVars.SEND_LOGS_EMAIL;
                i.putExtra("android.intent.extra.EMAIL", strArr);
                i.putExtra("android.intent.extra.SUBJECT", "last logs");
                i.putParcelableArrayListExtra("android.intent.extra.STREAM", uris);
                getParentActivity().startActivityForResult(Intent.createChooser(i, "Select email application."), 500);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
