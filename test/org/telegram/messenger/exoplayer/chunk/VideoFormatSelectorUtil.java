package org.telegram.messenger.exoplayer.chunk;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Point;
import android.view.Display;
import android.view.Display.Mode;
import android.view.WindowManager;
import java.util.ArrayList;
import java.util.List;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.exoplayer.MediaCodecUtil;
import org.telegram.messenger.exoplayer.MediaCodecUtil.DecoderQueryException;
import org.telegram.messenger.exoplayer.util.MimeTypes;
import org.telegram.messenger.exoplayer.util.Util;
import org.telegram.tgnet.ConnectionsManager;

public final class VideoFormatSelectorUtil {
    private static final float FRACTION_TO_CONSIDER_FULLSCREEN = 0.98f;

    public static int[] selectVideoFormatsForDefaultDisplay(Context context, List<? extends FormatWrapper> formatWrappers, String[] allowedContainerMimeTypes, boolean filterHdFormats) throws DecoderQueryException {
        Point viewportSize = getViewportSize(context);
        return selectVideoFormats(formatWrappers, allowedContainerMimeTypes, filterHdFormats, true, viewportSize.x, viewportSize.y);
    }

    public static int[] selectVideoFormats(List<? extends FormatWrapper> formatWrappers, String[] allowedContainerMimeTypes, boolean filterHdFormats, boolean orientationMayChange, int viewportWidth, int viewportHeight) throws DecoderQueryException {
        int i;
        int maxVideoPixelsToRetain = ConnectionsManager.DEFAULT_DATACENTER_ID;
        ArrayList<Integer> selectedIndexList = new ArrayList();
        int formatWrapperCount = formatWrappers.size();
        for (i = 0; i < formatWrapperCount; i++) {
            Format format = ((FormatWrapper) formatWrappers.get(i)).getFormat();
            if (isFormatPlayable(format, allowedContainerMimeTypes, filterHdFormats)) {
                selectedIndexList.add(Integer.valueOf(i));
                if (format.width > 0 && format.height > 0 && viewportWidth > 0 && viewportHeight > 0) {
                    Point maxVideoSizeInViewport = getMaxVideoSizeInViewport(orientationMayChange, viewportWidth, viewportHeight, format.width, format.height);
                    int videoPixels = format.width * format.height;
                    if (format.width >= ((int) (((float) maxVideoSizeInViewport.x) * FRACTION_TO_CONSIDER_FULLSCREEN)) && format.height >= ((int) (((float) maxVideoSizeInViewport.y) * FRACTION_TO_CONSIDER_FULLSCREEN)) && videoPixels < maxVideoPixelsToRetain) {
                        maxVideoPixelsToRetain = videoPixels;
                    }
                }
            }
        }
        if (maxVideoPixelsToRetain != ConnectionsManager.DEFAULT_DATACENTER_ID) {
            for (i = selectedIndexList.size() - 1; i >= 0; i--) {
                format = ((FormatWrapper) formatWrappers.get(((Integer) selectedIndexList.get(i)).intValue())).getFormat();
                if (format.width > 0 && format.height > 0 && format.width * format.height > maxVideoPixelsToRetain) {
                    selectedIndexList.remove(i);
                }
            }
        }
        return Util.toArray(selectedIndexList);
    }

    private static boolean isFormatPlayable(Format format, String[] allowedContainerMimeTypes, boolean filterHdFormats) throws DecoderQueryException {
        if (allowedContainerMimeTypes != null && !Util.contains(allowedContainerMimeTypes, format.mimeType)) {
            return false;
        }
        if (filterHdFormats && (format.width >= 1280 || format.height >= 720)) {
            return false;
        }
        if (format.width > 0 && format.height > 0) {
            if (Util.SDK_INT >= 21) {
                String videoMediaMimeType = MimeTypes.getVideoMediaMimeType(format.codecs);
                if (MimeTypes.VIDEO_UNKNOWN.equals(videoMediaMimeType)) {
                    videoMediaMimeType = MediaController.MIME_TYPE;
                }
                if (format.frameRate > 0.0f) {
                    return MediaCodecUtil.isSizeAndRateSupportedV21(videoMediaMimeType, false, format.width, format.height, (double) format.frameRate);
                }
                return MediaCodecUtil.isSizeSupportedV21(videoMediaMimeType, false, format.width, format.height);
            } else if (format.width * format.height > MediaCodecUtil.maxH264DecodableFrameSize()) {
                return false;
            }
        }
        return true;
    }

    private static Point getMaxVideoSizeInViewport(boolean orientationMayChange, int viewportWidth, int viewportHeight, int videoWidth, int videoHeight) {
        Object obj = 1;
        if (orientationMayChange) {
            Object obj2 = videoWidth > videoHeight ? 1 : null;
            if (viewportWidth <= viewportHeight) {
                obj = null;
            }
            if (obj2 != obj) {
                int tempViewportWidth = viewportWidth;
                viewportWidth = viewportHeight;
                viewportHeight = tempViewportWidth;
            }
        }
        if (videoWidth * viewportHeight >= videoHeight * viewportWidth) {
            return new Point(viewportWidth, Util.ceilDivide(viewportWidth * videoHeight, videoWidth));
        }
        return new Point(Util.ceilDivide(viewportHeight * videoWidth, videoHeight), viewportHeight);
    }

    private static Point getViewportSize(Context context) {
        if (Util.SDK_INT >= 23 || Util.MODEL == null || !Util.MODEL.startsWith("BRAVIA") || !context.getPackageManager().hasSystemFeature("com.sony.dtv.hardware.panel.qfhd")) {
            return getDisplaySize(((WindowManager) context.getSystemService("window")).getDefaultDisplay());
        }
        return new Point(3840, 2160);
    }

    private static Point getDisplaySize(Display display) {
        Point displaySize = new Point();
        if (Util.SDK_INT >= 23) {
            getDisplaySizeV23(display, displaySize);
        } else if (Util.SDK_INT >= 17) {
            getDisplaySizeV17(display, displaySize);
        } else if (Util.SDK_INT >= 16) {
            getDisplaySizeV16(display, displaySize);
        } else {
            getDisplaySizeV9(display, displaySize);
        }
        return displaySize;
    }

    @TargetApi(23)
    private static void getDisplaySizeV23(Display display, Point outSize) {
        Mode mode = display.getMode();
        outSize.x = mode.getPhysicalWidth();
        outSize.y = mode.getPhysicalHeight();
    }

    @TargetApi(17)
    private static void getDisplaySizeV17(Display display, Point outSize) {
        display.getRealSize(outSize);
    }

    @TargetApi(16)
    private static void getDisplaySizeV16(Display display, Point outSize) {
        display.getSize(outSize);
    }

    private static void getDisplaySizeV9(Display display, Point outSize) {
        outSize.x = display.getWidth();
        outSize.y = display.getHeight();
    }

    private VideoFormatSelectorUtil() {
    }
}
