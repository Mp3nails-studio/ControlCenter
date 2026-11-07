package com.controlcenter.app.system

import android.content.ComponentName
import android.content.Context
import android.media.MediaMetadata
import android.media.session.MediaController as SessionController
import android.media.session.MediaSessionManager
import android.media.session.PlaybackState
import android.service.notification.NotificationListenerService

/**
 * Pusty serwis. Sam dostep do powiadomien (przyznany w ustawieniach)
 * pozwala odczytac aktywne sesje multimedialne przez MediaSessionManager.
 */
class MediaListener : NotificationListenerService()

/** Informacje o grajacym utworze. Kontroler jest poza konstruktorem, zeby nie psul equals(). */
data class MediaInfo(val title: String, val artist: String, val playing: Boolean) {
    var controller: SessionController? = null
}

object MediaState {
    fun current(c: Context): MediaInfo? {
        return try {
            val msm = c.getSystemService(MediaSessionManager::class.java) ?: return null
            val sessions = msm.getActiveSessions(ComponentName(c, MediaListener::class.java))
            val s = sessions.firstOrNull { it.playbackState?.state == PlaybackState.STATE_PLAYING }
                ?: sessions.firstOrNull()
                ?: return null
            val md = s.metadata
            val title = md?.getString(MediaMetadata.METADATA_KEY_TITLE)
                ?: md?.getString(MediaMetadata.METADATA_KEY_DISPLAY_TITLE)
                ?: "Nieznany utwor"
            val artist = md?.getString(MediaMetadata.METADATA_KEY_ARTIST)
                ?: md?.getString(MediaMetadata.METADATA_KEY_ALBUM_ARTIST)
                ?: ""
            MediaInfo(title, artist, s.playbackState?.state == PlaybackState.STATE_PLAYING)
                .also { it.controller = s }
        } catch (e: SecurityException) {
            null // brak dostepu do powiadomien
        } catch (e: Exception) {
            null
        }
    }
}
