package org.telegram.messenger.exoplayer.extractor;

public interface SeekMap {
    public static final SeekMap UNSEEKABLE;

    /* renamed from: org.telegram.messenger.exoplayer.extractor.SeekMap.1 */
    static class C16991 implements SeekMap {
        C16991() {
        }

        public boolean isSeekable() {
            return false;
        }

        public long getPosition(long timeUs) {
            return 0;
        }
    }

    long getPosition(long j);

    boolean isSeekable();

    static {
        UNSEEKABLE = new C16991();
    }
}
