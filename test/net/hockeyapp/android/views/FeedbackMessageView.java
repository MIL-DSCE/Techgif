package net.hockeyapp.android.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import net.hockeyapp.android.C0388R;
import net.hockeyapp.android.objects.FeedbackAttachment;
import net.hockeyapp.android.objects.FeedbackMessage;
import net.hockeyapp.android.tasks.AttachmentDownloader;

public class FeedbackMessageView extends LinearLayout {
    @SuppressLint({"SimpleDateFormat"})
    private static final SimpleDateFormat DATE_FORMAT_IN;
    @SuppressLint({"SimpleDateFormat"})
    private static final SimpleDateFormat DATE_FORMAT_OUT;
    private AttachmentListView mAttachmentListView;
    private TextView mAuthorTextView;
    private final Context mContext;
    private TextView mDateTextView;
    private FeedbackMessage mFeedbackMessage;
    private TextView mMessageTextView;
    @Deprecated
    private boolean ownMessage;

    static {
        DATE_FORMAT_IN = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        DATE_FORMAT_OUT = new SimpleDateFormat("d MMM h:mm a");
    }

    public FeedbackMessageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        LayoutInflater.from(context).inflate(C0388R.layout.hockeyapp_view_feedback_message, this);
        this.mAuthorTextView = (TextView) findViewById(C0388R.id.label_author);
        this.mDateTextView = (TextView) findViewById(C0388R.id.label_date);
        this.mMessageTextView = (TextView) findViewById(C0388R.id.label_text);
        this.mAttachmentListView = (AttachmentListView) findViewById(C0388R.id.list_attachments);
    }

    public void setFeedbackMessage(FeedbackMessage feedbackMessage) {
        this.mFeedbackMessage = feedbackMessage;
        try {
            this.mDateTextView.setText(DATE_FORMAT_OUT.format(DATE_FORMAT_IN.parse(this.mFeedbackMessage.getCreatedAt())));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        this.mAuthorTextView.setText(this.mFeedbackMessage.getName());
        this.mMessageTextView.setText(this.mFeedbackMessage.getText());
        this.mAttachmentListView.removeAllViews();
        for (FeedbackAttachment feedbackAttachment : this.mFeedbackMessage.getFeedbackAttachments()) {
            AttachmentView attachmentView = new AttachmentView(this.mContext, this.mAttachmentListView, feedbackAttachment, false);
            AttachmentDownloader.getInstance().download(feedbackAttachment, attachmentView);
            this.mAttachmentListView.addView(attachmentView);
        }
    }

    public void setIndex(int index) {
        if (index % 2 == 0) {
            setBackgroundColor(getResources().getColor(C0388R.color.hockeyapp_background_light));
            this.mAuthorTextView.setTextColor(getResources().getColor(C0388R.color.hockeyapp_text_white));
            this.mDateTextView.setTextColor(getResources().getColor(C0388R.color.hockeyapp_text_white));
        } else {
            setBackgroundColor(getResources().getColor(C0388R.color.hockeyapp_background_white));
            this.mAuthorTextView.setTextColor(getResources().getColor(C0388R.color.hockeyapp_text_light));
            this.mDateTextView.setTextColor(getResources().getColor(C0388R.color.hockeyapp_text_light));
        }
        this.mMessageTextView.setTextColor(getResources().getColor(C0388R.color.hockeyapp_text_black));
    }
}
