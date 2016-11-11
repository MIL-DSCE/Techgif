package org.telegram.ui.Components;

import android.content.Context;
import android.content.res.TypedArray;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import java.lang.reflect.Field;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.support.widget.RecyclerView;
import org.telegram.messenger.support.widget.RecyclerView.Adapter;
import org.telegram.messenger.support.widget.RecyclerView.AdapterDataObserver;
import org.telegram.messenger.support.widget.RecyclerView.OnItemTouchListener;
import org.telegram.messenger.support.widget.RecyclerView.OnScrollListener;

public class RecyclerListView extends RecyclerView {
    private static int[] attributes;
    private static boolean gotAttributes;
    private Runnable clickRunnable;
    private int currentChildPosition;
    private View currentChildView;
    private boolean disallowInterceptTouchEvents;
    private View emptyView;
    private boolean instantClick;
    private boolean interceptedByChild;
    private GestureDetector mGestureDetector;
    private AdapterDataObserver observer;
    private OnInterceptTouchListener onInterceptTouchListener;
    private OnItemClickListener onItemClickListener;
    private OnItemLongClickListener onItemLongClickListener;
    private OnScrollListener onScrollListener;
    private Runnable selectChildRunnable;
    private boolean wasPressed;

    public interface OnInterceptTouchListener {
        boolean onInterceptTouchEvent(MotionEvent motionEvent);
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int i);
    }

    public interface OnItemLongClickListener {
        boolean onItemClick(View view, int i);
    }

    /* renamed from: org.telegram.ui.Components.RecyclerListView.1 */
    class C18391 extends AdapterDataObserver {
        C18391() {
        }

        public void onChanged() {
            RecyclerListView.this.checkIfEmpty();
        }

        public void onItemRangeInserted(int positionStart, int itemCount) {
            RecyclerListView.this.checkIfEmpty();
        }

        public void onItemRangeRemoved(int positionStart, int itemCount) {
            RecyclerListView.this.checkIfEmpty();
        }
    }

    /* renamed from: org.telegram.ui.Components.RecyclerListView.2 */
    class C18402 extends OnScrollListener {
        C18402() {
        }

        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            if (!(newState == 0 || RecyclerListView.this.currentChildView == null)) {
                if (RecyclerListView.this.selectChildRunnable != null) {
                    AndroidUtilities.cancelRunOnUIThread(RecyclerListView.this.selectChildRunnable);
                    RecyclerListView.this.selectChildRunnable = null;
                }
                MotionEvent event = MotionEvent.obtain(0, 0, 3, 0.0f, 0.0f, 0);
                try {
                    RecyclerListView.this.mGestureDetector.onTouchEvent(event);
                } catch (Throwable e) {
                    FileLog.m13e("tmessages", e);
                }
                RecyclerListView.this.currentChildView.onTouchEvent(event);
                event.recycle();
                RecyclerListView.this.currentChildView.setPressed(false);
                RecyclerListView.this.currentChildView = null;
                RecyclerListView.this.interceptedByChild = false;
            }
            if (RecyclerListView.this.onScrollListener != null) {
                RecyclerListView.this.onScrollListener.onScrollStateChanged(recyclerView, newState);
            }
        }

        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            if (RecyclerListView.this.onScrollListener != null) {
                RecyclerListView.this.onScrollListener.onScrolled(recyclerView, dx, dy);
            }
        }
    }

    private class RecyclerListViewItemClickListener implements OnItemTouchListener {

        /* renamed from: org.telegram.ui.Components.RecyclerListView.RecyclerListViewItemClickListener.1 */
        class C11711 extends SimpleOnGestureListener {
            final /* synthetic */ RecyclerListView val$this$0;

            /* renamed from: org.telegram.ui.Components.RecyclerListView.RecyclerListViewItemClickListener.1.1 */
            class C11701 implements Runnable {
                final /* synthetic */ View val$view;

                C11701(View view) {
                    this.val$view = view;
                }

                public void run() {
                    if (this == RecyclerListView.this.clickRunnable) {
                        RecyclerListView.this.clickRunnable = null;
                    }
                    if (this.val$view != null) {
                        this.val$view.setPressed(false);
                        if (!RecyclerListView.this.instantClick) {
                            this.val$view.playSoundEffect(0);
                            if (RecyclerListView.this.onItemClickListener != null) {
                                RecyclerListView.this.onItemClickListener.onItemClick(this.val$view, RecyclerListView.this.currentChildPosition);
                            }
                        }
                    }
                }
            }

            C11711(RecyclerListView recyclerListView) {
                this.val$this$0 = recyclerListView;
            }

            public boolean onSingleTapUp(MotionEvent e) {
                if (!(RecyclerListView.this.currentChildView == null || RecyclerListView.this.onItemClickListener == null)) {
                    RecyclerListView.this.currentChildView.setPressed(true);
                    View view = RecyclerListView.this.currentChildView;
                    if (RecyclerListView.this.instantClick) {
                        view.playSoundEffect(0);
                        RecyclerListView.this.onItemClickListener.onItemClick(view, RecyclerListView.this.currentChildPosition);
                    }
                    AndroidUtilities.runOnUIThread(RecyclerListView.this.clickRunnable = new C11701(view), (long) ViewConfiguration.getPressedStateDuration());
                    if (RecyclerListView.this.selectChildRunnable != null) {
                        AndroidUtilities.cancelRunOnUIThread(RecyclerListView.this.selectChildRunnable);
                        RecyclerListView.this.selectChildRunnable = null;
                        RecyclerListView.this.currentChildView = null;
                        RecyclerListView.this.interceptedByChild = false;
                    }
                }
                return true;
            }

            public void onLongPress(MotionEvent event) {
                if (RecyclerListView.this.currentChildView != null && RecyclerListView.this.onItemLongClickListener != null && RecyclerListView.this.onItemLongClickListener.onItemClick(RecyclerListView.this.currentChildView, RecyclerListView.this.currentChildPosition)) {
                    RecyclerListView.this.currentChildView.performHapticFeedback(0);
                }
            }
        }

        /* renamed from: org.telegram.ui.Components.RecyclerListView.RecyclerListViewItemClickListener.2 */
        class C11722 implements Runnable {
            C11722() {
            }

            public void run() {
                if (RecyclerListView.this.selectChildRunnable != null && RecyclerListView.this.currentChildView != null) {
                    RecyclerListView.this.currentChildView.setPressed(true);
                    RecyclerListView.this.selectChildRunnable = null;
                }
            }
        }

        public RecyclerListViewItemClickListener(Context context) {
            RecyclerListView.this.mGestureDetector = new GestureDetector(context, new C11711(RecyclerListView.this));
        }

        public boolean onInterceptTouchEvent(RecyclerView view, MotionEvent event) {
            int action = event.getActionMasked();
            boolean isScrollIdle = RecyclerListView.this.getScrollState() == 0;
            if ((action == 0 || action == 5) && RecyclerListView.this.currentChildView == null && isScrollIdle) {
                RecyclerListView.this.currentChildView = view.findChildViewUnder(event.getX(), event.getY());
                if (RecyclerListView.this.currentChildView instanceof ViewGroup) {
                    ViewGroup viewGroup = (ViewGroup) RecyclerListView.this.currentChildView;
                    float x = event.getX() - ((float) RecyclerListView.this.currentChildView.getLeft());
                    float y = event.getY() - ((float) RecyclerListView.this.currentChildView.getTop());
                    for (int i = viewGroup.getChildCount() - 1; i >= 0; i--) {
                        View child = viewGroup.getChildAt(i);
                        if (x >= ((float) child.getLeft()) && x <= ((float) child.getRight()) && y >= ((float) child.getTop()) && y <= ((float) child.getBottom()) && child.isClickable()) {
                            RecyclerListView.this.currentChildView = null;
                            break;
                        }
                    }
                }
                RecyclerListView.this.currentChildPosition = -1;
                if (RecyclerListView.this.currentChildView != null) {
                    RecyclerListView.this.currentChildPosition = view.getChildPosition(RecyclerListView.this.currentChildView);
                    MotionEvent childEvent = MotionEvent.obtain(0, 0, event.getActionMasked(), event.getX() - ((float) RecyclerListView.this.currentChildView.getLeft()), event.getY() - ((float) RecyclerListView.this.currentChildView.getTop()), 0);
                    if (RecyclerListView.this.currentChildView.onTouchEvent(childEvent)) {
                        RecyclerListView.this.interceptedByChild = true;
                    }
                    childEvent.recycle();
                }
            }
            if (!(RecyclerListView.this.currentChildView == null || RecyclerListView.this.interceptedByChild || event == null)) {
                try {
                    RecyclerListView.this.mGestureDetector.onTouchEvent(event);
                } catch (Throwable e) {
                    FileLog.m13e("tmessages", e);
                }
            }
            if (action == 0 || action == 5) {
                if (!(RecyclerListView.this.interceptedByChild || RecyclerListView.this.currentChildView == null)) {
                    RecyclerListView.this.selectChildRunnable = new C11722();
                    AndroidUtilities.runOnUIThread(RecyclerListView.this.selectChildRunnable, (long) ViewConfiguration.getTapTimeout());
                }
            } else if (RecyclerListView.this.currentChildView != null && (action == 1 || action == 6 || action == 3 || !isScrollIdle)) {
                if (RecyclerListView.this.selectChildRunnable != null) {
                    AndroidUtilities.cancelRunOnUIThread(RecyclerListView.this.selectChildRunnable);
                    RecyclerListView.this.selectChildRunnable = null;
                }
                RecyclerListView.this.currentChildView.setPressed(false);
                RecyclerListView.this.currentChildView = null;
                RecyclerListView.this.interceptedByChild = false;
            }
            return false;
        }

        public void onTouchEvent(RecyclerView view, MotionEvent event) {
        }

        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
            RecyclerListView.this.cancelClickRunnables(true);
        }
    }

    public void cancelClickRunnables(boolean uncheck) {
        if (this.selectChildRunnable != null) {
            AndroidUtilities.cancelRunOnUIThread(this.selectChildRunnable);
            this.selectChildRunnable = null;
        }
        if (this.currentChildView != null) {
            if (uncheck) {
                this.currentChildView.setPressed(false);
            }
            this.currentChildView = null;
        }
        if (this.clickRunnable != null) {
            AndroidUtilities.cancelRunOnUIThread(this.clickRunnable);
            this.clickRunnable = null;
        }
        this.interceptedByChild = false;
    }

    public int[] getResourceDeclareStyleableIntArray(String packageName, String name) {
        try {
            Field f = Class.forName(packageName + ".R$styleable").getField(name);
            if (f != null) {
                return (int[]) f.get(null);
            }
        } catch (Throwable th) {
        }
        return null;
    }

    public RecyclerListView(Context context) {
        super(context);
        this.observer = new C18391();
        try {
            if (!gotAttributes) {
                attributes = getResourceDeclareStyleableIntArray("com.android.internal", "View");
                gotAttributes = true;
            }
            TypedArray a = context.getTheme().obtainStyledAttributes(attributes);
            View.class.getDeclaredMethod("initializeScrollbars", new Class[]{TypedArray.class}).invoke(this, new Object[]{a});
            a.recycle();
        } catch (Throwable e) {
            FileLog.m13e("tmessages", e);
        }
        super.setOnScrollListener(new C18402());
        addOnItemTouchListener(new RecyclerListViewItemClickListener(context));
    }

    public void setVerticalScrollBarEnabled(boolean verticalScrollBarEnabled) {
        if (attributes != null) {
            super.setVerticalScrollBarEnabled(verticalScrollBarEnabled);
        }
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        this.onItemLongClickListener = listener;
    }

    public void setEmptyView(View view) {
        if (this.emptyView != view) {
            this.emptyView = view;
            checkIfEmpty();
        }
    }

    public View getEmptyView() {
        return this.emptyView;
    }

    public void invalidateViews() {
        int count = getChildCount();
        for (int a = 0; a < count; a++) {
            getChildAt(a).invalidate();
        }
    }

    public boolean onInterceptTouchEvent(MotionEvent e) {
        if (this.disallowInterceptTouchEvents) {
            requestDisallowInterceptTouchEvent(true);
        }
        if ((this.onInterceptTouchListener == null || !this.onInterceptTouchListener.onInterceptTouchEvent(e)) && !super.onInterceptTouchEvent(e)) {
            return false;
        }
        return true;
    }

    private void checkIfEmpty() {
        int i = 0;
        if (this.emptyView != null && getAdapter() != null) {
            boolean emptyViewVisible;
            if (getAdapter().getItemCount() == 0) {
                emptyViewVisible = true;
            } else {
                emptyViewVisible = false;
            }
            this.emptyView.setVisibility(emptyViewVisible ? 0 : 8);
            if (emptyViewVisible) {
                i = 4;
            }
            setVisibility(i);
        }
    }

    public void setOnScrollListener(OnScrollListener listener) {
        this.onScrollListener = listener;
    }

    public void setOnInterceptTouchListener(OnInterceptTouchListener listener) {
        this.onInterceptTouchListener = listener;
    }

    public void setInstantClick(boolean value) {
        this.instantClick = value;
    }

    public void setDisallowInterceptTouchEvents(boolean value) {
        this.disallowInterceptTouchEvents = value;
    }

    public void setAdapter(Adapter adapter) {
        Adapter oldAdapter = getAdapter();
        if (oldAdapter != null) {
            oldAdapter.unregisterAdapterDataObserver(this.observer);
        }
        super.setAdapter(adapter);
        if (adapter != null) {
            adapter.registerAdapterDataObserver(this.observer);
        }
        checkIfEmpty();
    }

    public void stopScroll() {
        try {
            super.stopScroll();
        } catch (NullPointerException e) {
        }
    }

    public boolean hasOverlappingRendering() {
        return false;
    }
}
