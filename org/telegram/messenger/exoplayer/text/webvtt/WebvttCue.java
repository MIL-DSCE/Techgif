package org.telegram.messenger.exoplayer.text.webvtt;

import android.text.Layout.Alignment;
import android.util.Log;
import org.telegram.messenger.exoplayer.MediaCodecAudioTrackRenderer;
import org.telegram.messenger.exoplayer.MediaCodecVideoTrackRenderer;
import org.telegram.messenger.exoplayer.text.Cue;
import org.telegram.messenger.support.widget.LinearLayoutManager;
import org.telegram.ui.Components.VideoPlayer;

final class WebvttCue extends Cue {
    public final long endTime;
    public final long startTime;

    /* renamed from: org.telegram.messenger.exoplayer.text.webvtt.WebvttCue.1 */
    static /* synthetic */ class C07861 {
        static final /* synthetic */ int[] $SwitchMap$android$text$Layout$Alignment;

        static {
            $SwitchMap$android$text$Layout$Alignment = new int[Alignment.values().length];
            try {
                $SwitchMap$android$text$Layout$Alignment[Alignment.ALIGN_NORMAL.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$android$text$Layout$Alignment[Alignment.ALIGN_CENTER.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$android$text$Layout$Alignment[Alignment.ALIGN_OPPOSITE.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
        }
    }

    public static final class Builder {
        private static final String TAG = "WebvttCueBuilder";
        private long endTime;
        private float line;
        private int lineAnchor;
        private int lineType;
        private float position;
        private int positionAnchor;
        private long startTime;
        private CharSequence text;
        private Alignment textAlignment;
        private float width;

        public Builder() {
            reset();
        }

        public void reset() {
            this.startTime = 0;
            this.endTime = 0;
            this.text = null;
            this.textAlignment = null;
            this.line = Cue.DIMEN_UNSET;
            this.lineType = LinearLayoutManager.INVALID_OFFSET;
            this.lineAnchor = LinearLayoutManager.INVALID_OFFSET;
            this.position = Cue.DIMEN_UNSET;
            this.positionAnchor = LinearLayoutManager.INVALID_OFFSET;
            this.width = Cue.DIMEN_UNSET;
        }

        public WebvttCue build() {
            if (this.position != Cue.DIMEN_UNSET && this.positionAnchor == LinearLayoutManager.INVALID_OFFSET) {
                derivePositionAnchorFromAlignment();
            }
            return new WebvttCue(this.startTime, this.endTime, this.text, this.textAlignment, this.line, this.lineType, this.lineAnchor, this.position, this.positionAnchor, this.width);
        }

        public Builder setStartTime(long time) {
            this.startTime = time;
            return this;
        }

        public Builder setEndTime(long time) {
            this.endTime = time;
            return this;
        }

        public Builder setText(CharSequence aText) {
            this.text = aText;
            return this;
        }

        public Builder setTextAlignment(Alignment textAlignment) {
            this.textAlignment = textAlignment;
            return this;
        }

        public Builder setLine(float line) {
            this.line = line;
            return this;
        }

        public Builder setLineType(int lineType) {
            this.lineType = lineType;
            return this;
        }

        public Builder setLineAnchor(int lineAnchor) {
            this.lineAnchor = lineAnchor;
            return this;
        }

        public Builder setPosition(float position) {
            this.position = position;
            return this;
        }

        public Builder setPositionAnchor(int positionAnchor) {
            this.positionAnchor = positionAnchor;
            return this;
        }

        public Builder setWidth(float width) {
            this.width = width;
            return this;
        }

        private Builder derivePositionAnchorFromAlignment() {
            if (this.textAlignment != null) {
                switch (C07861.$SwitchMap$android$text$Layout$Alignment[this.textAlignment.ordinal()]) {
                    case MediaCodecVideoTrackRenderer.MSG_SET_SURFACE /*1*/:
                        this.positionAnchor = 0;
                        break;
                    case MediaCodecAudioTrackRenderer.MSG_SET_PLAYBACK_PARAMS /*2*/:
                        this.positionAnchor = 1;
                        break;
                    case VideoPlayer.STATE_BUFFERING /*3*/:
                        this.positionAnchor = 2;
                        break;
                    default:
                        Log.w(TAG, "Unrecognized alignment: " + this.textAlignment);
                        this.positionAnchor = 0;
                        break;
                }
            }
            this.positionAnchor = LinearLayoutManager.INVALID_OFFSET;
            return this;
        }
    }

    public WebvttCue(CharSequence text) {
        this(0, 0, text);
    }

    public WebvttCue(long startTime, long endTime, CharSequence text) {
        this(startTime, endTime, text, null, Cue.DIMEN_UNSET, LinearLayoutManager.INVALID_OFFSET, LinearLayoutManager.INVALID_OFFSET, Cue.DIMEN_UNSET, LinearLayoutManager.INVALID_OFFSET, Cue.DIMEN_UNSET);
    }

    public WebvttCue(long startTime, long endTime, CharSequence text, Alignment textAlignment, float line, int lineType, int lineAnchor, float position, int positionAnchor, float width) {
        super(text, textAlignment, line, lineType, lineAnchor, position, positionAnchor, width);
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public boolean isNormalCue() {
        return this.line == Cue.DIMEN_UNSET && this.position == Cue.DIMEN_UNSET;
    }
}
