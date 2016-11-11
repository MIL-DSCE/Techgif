package org.telegram.ui.Components;

import android.graphics.Path;
import android.graphics.Path.Direction;
import android.text.StaticLayout;
import com.google.android.gms.maps.model.GroundOverlayOptions;

public class LinkPath extends Path {
    private StaticLayout currentLayout;
    private int currentLine;
    private float heightOffset;
    private float lastTop;

    public LinkPath() {
        this.lastTop = GroundOverlayOptions.NO_DIMENSION;
    }

    public void setCurrentLayout(StaticLayout layout, int start, float yOffset) {
        this.currentLayout = layout;
        this.currentLine = layout.getLineForOffset(start);
        this.lastTop = GroundOverlayOptions.NO_DIMENSION;
        this.heightOffset = yOffset;
    }

    public void addRect(float left, float top, float right, float bottom, Direction dir) {
        top += this.heightOffset;
        bottom += this.heightOffset;
        if (this.lastTop == GroundOverlayOptions.NO_DIMENSION) {
            this.lastTop = top;
        } else if (this.lastTop != top) {
            this.lastTop = top;
            this.currentLine++;
        }
        float lineRight = this.currentLayout.getLineRight(this.currentLine);
        float lineLeft = this.currentLayout.getLineLeft(this.currentLine);
        if (left < lineRight) {
            if (right > lineRight) {
                right = lineRight;
            }
            if (left < lineLeft) {
                left = lineLeft;
            }
            super.addRect(left, top, right, bottom, dir);
        }
    }
}
