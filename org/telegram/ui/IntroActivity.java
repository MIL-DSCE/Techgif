package org.telegram.ui;

import android.animation.ObjectAnimator;
import android.animation.StateListAnimator;
import android.app.Activity;
import android.content.Intent;
import android.database.DataSetObserver;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.C0691R;
import org.telegram.messenger.LocaleController;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.ui.ActionBar.Theme;

public class IntroActivity extends Activity {
    private ViewGroup bottomPages;
    private int[] icons;
    private boolean justCreated;
    private int lastPage;
    private int[] messages;
    private boolean startPressed;
    private int[] titles;
    private ImageView topImage1;
    private ImageView topImage2;
    private ViewPager viewPager;

    /* renamed from: org.telegram.ui.IntroActivity.2 */
    class C12532 implements OnClickListener {
        C12532() {
        }

        public void onClick(View view) {
            if (!IntroActivity.this.startPressed) {
                IntroActivity.this.startPressed = true;
                Intent intent2 = new Intent(IntroActivity.this, LaunchActivity.class);
                intent2.putExtra("fromIntro", true);
                IntroActivity.this.startActivity(intent2);
                IntroActivity.this.finish();
            }
        }
    }

    /* renamed from: org.telegram.ui.IntroActivity.3 */
    class C12543 implements OnLongClickListener {
        C12543() {
        }

        public boolean onLongClick(View v) {
            ConnectionsManager.getInstance().switchBackend();
            return true;
        }
    }

    /* renamed from: org.telegram.ui.IntroActivity.1 */
    class C18651 implements OnPageChangeListener {

        /* renamed from: org.telegram.ui.IntroActivity.1.1 */
        class C12511 implements AnimationListener {
            final /* synthetic */ ImageView val$fadeoutImage;

            C12511(ImageView imageView) {
                this.val$fadeoutImage = imageView;
            }

            public void onAnimationStart(Animation animation) {
            }

            public void onAnimationEnd(Animation animation) {
                this.val$fadeoutImage.setVisibility(8);
            }

            public void onAnimationRepeat(Animation animation) {
            }
        }

        /* renamed from: org.telegram.ui.IntroActivity.1.2 */
        class C12522 implements AnimationListener {
            final /* synthetic */ ImageView val$fadeinImage;

            C12522(ImageView imageView) {
                this.val$fadeinImage = imageView;
            }

            public void onAnimationStart(Animation animation) {
                this.val$fadeinImage.setVisibility(0);
            }

            public void onAnimationEnd(Animation animation) {
            }

            public void onAnimationRepeat(Animation animation) {
            }
        }

        C18651() {
        }

        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        public void onPageSelected(int i) {
        }

        public void onPageScrollStateChanged(int i) {
            if ((i == 0 || i == 2) && IntroActivity.this.lastPage != IntroActivity.this.viewPager.getCurrentItem()) {
                ImageView fadeoutImage;
                ImageView fadeinImage;
                IntroActivity.this.lastPage = IntroActivity.this.viewPager.getCurrentItem();
                if (IntroActivity.this.topImage1.getVisibility() == 0) {
                    fadeoutImage = IntroActivity.this.topImage1;
                    fadeinImage = IntroActivity.this.topImage2;
                } else {
                    fadeoutImage = IntroActivity.this.topImage2;
                    fadeinImage = IntroActivity.this.topImage1;
                }
                fadeinImage.bringToFront();
                fadeinImage.setImageResource(IntroActivity.this.icons[IntroActivity.this.lastPage]);
                fadeinImage.clearAnimation();
                fadeoutImage.clearAnimation();
                Animation outAnimation = AnimationUtils.loadAnimation(IntroActivity.this, C0691R.anim.icon_anim_fade_out);
                outAnimation.setAnimationListener(new C12511(fadeoutImage));
                Animation inAnimation = AnimationUtils.loadAnimation(IntroActivity.this, C0691R.anim.icon_anim_fade_in);
                inAnimation.setAnimationListener(new C12522(fadeinImage));
                fadeoutImage.startAnimation(outAnimation);
                fadeinImage.startAnimation(inAnimation);
            }
        }
    }

    private class IntroAdapter extends PagerAdapter {
        private IntroAdapter() {
        }

        public int getCount() {
            return 7;
        }

        public Object instantiateItem(ViewGroup container, int position) {
            View view = View.inflate(container.getContext(), C0691R.layout.intro_view_layout, null);
            TextView headerTextView = (TextView) view.findViewById(C0691R.id.header_text);
            TextView messageTextView = (TextView) view.findViewById(C0691R.id.message_text);
            container.addView(view, 0);
            headerTextView.setText(IntroActivity.this.getString(IntroActivity.this.titles[position]));
            messageTextView.setText(AndroidUtilities.replaceTags(IntroActivity.this.getString(IntroActivity.this.messages[position])));
            return view;
        }

        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            super.setPrimaryItem(container, position, object);
            int count = IntroActivity.this.bottomPages.getChildCount();
            for (int a = 0; a < count; a++) {
                View child = IntroActivity.this.bottomPages.getChildAt(a);
                if (a == position) {
                    child.setBackgroundColor(-13851168);
                } else {
                    child.setBackgroundColor(-4473925);
                }
            }
        }

        public boolean isViewFromObject(View view, Object object) {
            return view.equals(object);
        }

        public void restoreState(Parcelable arg0, ClassLoader arg1) {
        }

        public Parcelable saveState() {
            return null;
        }

        public void unregisterDataSetObserver(DataSetObserver observer) {
            if (observer != null) {
                super.unregisterDataSetObserver(observer);
            }
        }
    }

    public IntroActivity() {
        this.lastPage = 0;
        this.justCreated = false;
        this.startPressed = false;
    }

    protected void onCreate(Bundle savedInstanceState) {
        setTheme(C0691R.style.Theme_TMessages);
        super.onCreate(savedInstanceState);
        Theme.loadRecources(this);
        requestWindowFeature(1);
        if (AndroidUtilities.isTablet()) {
            setContentView(C0691R.layout.intro_layout_tablet);
        } else {
            setRequestedOrientation(1);
            setContentView(C0691R.layout.intro_layout);
        }
        if (LocaleController.isRTL) {
            this.icons = new int[]{C0691R.drawable.intro7, C0691R.drawable.intro6, C0691R.drawable.intro5, C0691R.drawable.intro4, C0691R.drawable.intro3, C0691R.drawable.intro2, C0691R.drawable.intro1};
            this.titles = new int[]{C0691R.string.Page7Title, C0691R.string.Page6Title, C0691R.string.Page5Title, C0691R.string.Page4Title, C0691R.string.Page3Title, C0691R.string.Page2Title, C0691R.string.Page1Title};
            this.messages = new int[]{C0691R.string.Page7Message, C0691R.string.Page6Message, C0691R.string.Page5Message, C0691R.string.Page4Message, C0691R.string.Page3Message, C0691R.string.Page2Message, C0691R.string.Page1Message};
        } else {
            this.icons = new int[]{C0691R.drawable.intro1, C0691R.drawable.intro2, C0691R.drawable.intro3, C0691R.drawable.intro4, C0691R.drawable.intro5, C0691R.drawable.intro6, C0691R.drawable.intro7};
            this.titles = new int[]{C0691R.string.Page1Title, C0691R.string.Page2Title, C0691R.string.Page3Title, C0691R.string.Page4Title, C0691R.string.Page5Title, C0691R.string.Page6Title, C0691R.string.Page7Title};
            this.messages = new int[]{C0691R.string.Page1Message, C0691R.string.Page2Message, C0691R.string.Page3Message, C0691R.string.Page4Message, C0691R.string.Page5Message, C0691R.string.Page6Message, C0691R.string.Page7Message};
        }
        this.viewPager = (ViewPager) findViewById(C0691R.id.intro_view_pager);
        TextView startMessagingButton = (TextView) findViewById(C0691R.id.start_messaging_button);
        startMessagingButton.setText(LocaleController.getString("StartMessaging", C0691R.string.StartMessaging).toUpperCase());
        if (VERSION.SDK_INT >= 21) {
            StateListAnimator animator = new StateListAnimator();
            animator.addState(new int[]{16842919}, ObjectAnimator.ofFloat(startMessagingButton, "translationZ", new float[]{(float) AndroidUtilities.dp(2.0f), (float) AndroidUtilities.dp(4.0f)}).setDuration(200));
            animator.addState(new int[0], ObjectAnimator.ofFloat(startMessagingButton, "translationZ", new float[]{(float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(2.0f)}).setDuration(200));
            startMessagingButton.setStateListAnimator(animator);
        }
        this.topImage1 = (ImageView) findViewById(C0691R.id.icon_image1);
        this.topImage2 = (ImageView) findViewById(C0691R.id.icon_image2);
        this.bottomPages = (ViewGroup) findViewById(C0691R.id.bottom_pages);
        this.topImage2.setVisibility(8);
        this.viewPager.setAdapter(new IntroAdapter());
        this.viewPager.setPageMargin(0);
        this.viewPager.setOffscreenPageLimit(1);
        this.viewPager.addOnPageChangeListener(new C18651());
        startMessagingButton.setOnClickListener(new C12532());
        if (BuildVars.DEBUG_VERSION) {
            startMessagingButton.setOnLongClickListener(new C12543());
        }
        this.justCreated = true;
    }

    protected void onResume() {
        super.onResume();
        if (this.justCreated) {
            if (LocaleController.isRTL) {
                this.viewPager.setCurrentItem(6);
                this.lastPage = 6;
            } else {
                this.viewPager.setCurrentItem(0);
                this.lastPage = 0;
            }
            this.justCreated = false;
        }
        AndroidUtilities.checkForCrashes(this);
        AndroidUtilities.checkForUpdates(this);
    }

    protected void onPause() {
        super.onPause();
        AndroidUtilities.unregisterUpdates();
    }
}
