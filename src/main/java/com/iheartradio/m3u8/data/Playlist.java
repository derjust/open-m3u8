package com.iheartradio.m3u8.data;

/**
 * Creating a new {@link Playlist} works via <code>Builder</code>s 
 * and their fluent <code>with*()</code> methods. On each <code>build()</code> 
 * method the provided parameters are validated:
<pre>TrackData trackData = new TrackData.Builder()
    .withTrackInfo(new TrackInfo(3.0f, "Example Song"))
    .withPath("example.mp3")
    .build();

List<TrackData> tracks = new ArrayList<TrackData>();
tracks.add(trackData);

MediaPlaylist mediaPlaylist = new MediaPlaylist.Builder()
    .withMediaSequenceNumber(1)
    .withTargetDuration(3)
    .withTracks(tracks)
    .build();

Playlist playlist = new Playlist.Builder()
    .withCompatibilityVersion(5)
    .withMediaPlaylist(mediaPlaylist)
    .build();</pre>
 * <p>The Playlist is similar to a C style union of a {@link MasterPlaylist} 
 * and {@link MediaPlaylist} in that it has one or the other but not both.  
 * You can check with {@link #hasMasterPlaylist()} or {@link #hasMediaPlaylist()}
 * which type you got.</p>
 * <p>Modifying an existing {@link Playlist} work similar to creating via the 
 * <code>Builder</code>s: Also each data class provides an <code>buildUpon()</code>
 *  method to generate a new <code>Builder</code> with all the data from the object itself:</p>
<pre>TrackData additionalTrack = new TrackData.Builder()
    .withTrackInfo(new TrackInfo(3.0f, "Additional Song"))
    .withPath("additional.mp3")
    .build();

List<TrackData> updatedTracks = new ArrayList<TrackData>(playlist.getMediaPlaylist().getTracks());
updatedTracks.add(additionalTrack);

MediaPlaylist updatedMediaPlaylist = playlist.getMediaPlaylist()
    .buildUpon()
    .withTracks(updatedTracks)
    .build();

Playlist updatedPlaylist = playlist.buildUpon()
    .withMediaPlaylist(updatedMediaPlaylist)
    .build();</pre>
 */
public class Playlist {
    public static final int MIN_COMPATIBILITY_VERSION = 1;

    private final MasterPlaylist mMasterPlaylist;
    private final MediaPlaylist mMediaPlaylist;
    private final boolean mIsExtended;
    private final boolean mIsIframesOnly;
    private final int mCompatibilityVersion;

    private Playlist(MasterPlaylist masterPlaylist, MediaPlaylist mediaPlaylist, boolean isExtended, boolean isIframesOnly, int compatibilityVersion) {
        mMasterPlaylist = masterPlaylist;
        mMediaPlaylist = mediaPlaylist;
        mIsExtended = isExtended;
        mIsIframesOnly = isIframesOnly;
        mCompatibilityVersion = compatibilityVersion;
    }

    public boolean hasMasterPlaylist() {
        return mMasterPlaylist != null;
    }

    public boolean hasMediaPlaylist() {
        return mMediaPlaylist != null;
    }

    public MasterPlaylist getMasterPlaylist() {
        return mMasterPlaylist;
    }

    public MediaPlaylist getMediaPlaylist() {
        return mMediaPlaylist;
    }

    public boolean isExtended() {
        return mIsExtended;
    }
    
    public boolean isIframesOnly() {
        return mIsIframesOnly;
    }

    public int getCompatibilityVersion() {
        return mCompatibilityVersion;
    }

    public Builder buildUpon() {
        return new Builder(mMasterPlaylist, mMediaPlaylist, mIsExtended, mCompatibilityVersion);
    }

    public static class Builder {
        private MasterPlaylist mMasterPlaylist;
        private MediaPlaylist mMediaPlaylist;
        private boolean mIsExtended;
        private boolean mIsIframesOnly;
        private int mCompatibilityVersion = MIN_COMPATIBILITY_VERSION;

        public Builder() {
        }

        private Builder(MasterPlaylist masterPlaylist, MediaPlaylist mediaPlaylist, boolean isExtended, int compatibilityVersion) {
            mMasterPlaylist = masterPlaylist;
            mMediaPlaylist = mediaPlaylist;
            mIsExtended = isExtended;
            mCompatibilityVersion = compatibilityVersion;
        }

        public Builder withMasterPlaylist(MasterPlaylist masterPlaylist) {
            if (mMediaPlaylist != null) {
                throw new IllegalStateException("cannot build Playlist with both a MasterPlaylist and a MediaPlaylist");
            }

            mMasterPlaylist = masterPlaylist;
            return withExtended(true);
        }

        public Builder withMediaPlaylist(MediaPlaylist mediaPlaylist) {
            if (mMasterPlaylist != null) {
                throw new IllegalStateException("cannot build Playlist with both a MasterPlaylist and a MediaPlaylist");
            }

            mMediaPlaylist = mediaPlaylist;
            return this;
        }

        public Builder withIframesOnly(boolean isIframesOnly) {
            mIsIframesOnly = isIframesOnly;
            return this;
        }

        public Builder withExtended(boolean isExtended) {
            if (mMasterPlaylist != null && !isExtended) {
                throw new IllegalStateException("a Playlist with a MasterPlaylist must be extended");
            }

            mIsExtended = isExtended;
            return this;
        }

        public Builder withCompatibilityVersion(int version) {
            if (version < MIN_COMPATIBILITY_VERSION) {
                throw new IllegalArgumentException("compatibility version must be >= " + MIN_COMPATIBILITY_VERSION);
            }

            mCompatibilityVersion = version;
            return this;
        }

        public Playlist build() {
            if (mMasterPlaylist != null || mMediaPlaylist != null) {
                return new Playlist(mMasterPlaylist, mMediaPlaylist, mIsExtended, mIsIframesOnly, mCompatibilityVersion);
            } else {
                throw new IllegalStateException("a Playlist must have a MasterPlaylist or a MediaPlaylist");
            }
        }
    }
}
