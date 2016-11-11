package org.telegram.ui.Adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.C0691R;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.UserConfig;
import org.telegram.ui.Cells.DividerCell;
import org.telegram.ui.Cells.DrawerActionCell;
import org.telegram.ui.Cells.DrawerProfileCell;
import org.telegram.ui.Cells.EmptyCell;

public class DrawerLayoutAdapter extends BaseAdapter {
    private Context mContext;

    public DrawerLayoutAdapter(Context context) {
        this.mContext = context;
    }

    public boolean areAllItemsEnabled() {
        return false;
    }

    public boolean isEnabled(int i) {
        return (i == 0 || i == 1 || i == 5) ? false : true;
    }

    public int getCount() {
        return UserConfig.isClientActivated() ? 10 : 0;
    }

    public Object getItem(int i) {
        return null;
    }

    public long getItemId(int i) {
        return (long) i;
    }

    public boolean hasStableIds() {
        return true;
    }

    public View getView(int i, View view, ViewGroup viewGroup) {
        int type = getItemViewType(i);
        if (type == 0) {
            if (view == null) {
                view = new DrawerProfileCell(this.mContext);
            }
            ((DrawerProfileCell) view).setUser(MessagesController.getInstance().getUser(Integer.valueOf(UserConfig.getClientUserId())));
            return view;
        } else if (type == 1) {
            if (view == null) {
                return new EmptyCell(this.mContext, AndroidUtilities.dp(8.0f));
            }
            return view;
        } else if (type == 2) {
            if (view == null) {
                return new DividerCell(this.mContext);
            }
            return view;
        } else if (type != 3) {
            return view;
        } else {
            if (view == null) {
                view = new DrawerActionCell(this.mContext);
            }
            DrawerActionCell actionCell = (DrawerActionCell) view;
            if (i == 2) {
                actionCell.setTextAndIcon(LocaleController.getString("NewGroup", C0691R.string.NewGroup), C0691R.drawable.menu_newgroup);
                return view;
            } else if (i == 3) {
                actionCell.setTextAndIcon(LocaleController.getString("NewSecretChat", C0691R.string.NewSecretChat), C0691R.drawable.menu_secret);
                return view;
            } else if (i == 4) {
                actionCell.setTextAndIcon(LocaleController.getString("NewChannel", C0691R.string.NewChannel), C0691R.drawable.menu_broadcast);
                return view;
            } else if (i == 6) {
                actionCell.setTextAndIcon(LocaleController.getString("Contacts", C0691R.string.Contacts), C0691R.drawable.menu_contacts);
                return view;
            } else if (i == 7) {
                actionCell.setTextAndIcon(LocaleController.getString("InviteFriends", C0691R.string.InviteFriends), C0691R.drawable.menu_invite);
                return view;
            } else if (i == 8) {
                actionCell.setTextAndIcon(LocaleController.getString("Settings", C0691R.string.Settings), C0691R.drawable.menu_settings);
                return view;
            } else if (i != 9) {
                return view;
            } else {
                actionCell.setTextAndIcon(LocaleController.getString("TelegramFaq", C0691R.string.TelegramFaq), C0691R.drawable.menu_help);
                return view;
            }
        }
    }

    public int getItemViewType(int i) {
        if (i == 0) {
            return 0;
        }
        if (i == 1) {
            return 1;
        }
        if (i == 5) {
            return 2;
        }
        return 3;
    }

    public int getViewTypeCount() {
        return 4;
    }

    public boolean isEmpty() {
        return !UserConfig.isClientActivated();
    }
}
