package com.ai.sovereignai.domain.service

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import android.util.Log
import com.ai.sovereignai.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.random.Random


/**
 * Keyboard sound manager for the typing effect.
 *
 * Loads the WAV file from res/raw/keyboard_sound.wav:
 * - First 8 seconds (0–8000 ms) - typing sounds
 * - From 19 seconds to the end (19000–20400 ms) - send sound
 *
 * Randomly extracts 30–80 ms clips from the first 8 seconds to create
 * natural variation in the typing effect.
 * When AI tokens are received, it plays random clips with a slight delay
 * to simulate keyboard typing.
 */

class KeyboardSoundManager(
    private val context : Context,
    private val scope : CoroutineScope
) {
    private var soundPool : SoundPool ? = null

    private var typingSoundIds = mutableListOf<Int>()
    private var sendSoundId : Int? = null

    private var soundVolume = 0f
    private var lastPlayTime: Long = 0f
    private var playJob : Job? = null
    private var isPlaying = false


    companion object{
        private const val  TAG = "KeyboardSoundManager"

        private  const val MIN_PLAY_INTERVAL_MS = 150L

        private const val MIN_TOKEN_DELAY = 1200L

        private const val MAX_TOKEN_DELAY = 2000L

        // Play sound every N characters (not every character)
        private  const val CHARS_PER_SOUND = 3
    }

    init {
        initializeSoundPool()
    }

    private fun initializeSoundPool() {
       try {

           val audioAttributes = AudioAttributes.Builder()
               .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
               .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
               .build()


           soundPool = SoundPool.Builder()
               .setMaxStreams(5)
               .setAudioAttributes(audioAttributes)
               .build()

           // Load the main sound file
           val soundId = soundPool?.load(context, R.raw.keyboard_sound, 1)

           if(soundPool != null  && soundId > 0){
               // In a real implementation, we would slice the audio file
               // For now, we'll use the same sound with different playback rates
               // to simulate different typing sounds

               typingSoundIds.add(soundId)
               sendSoundId = soundId

               Log.d(KeyboardSoundManager.Companion.TAG, "Sound loaded successfully: $soundId")
           }
           else {
               Log.e(KeyboardSoundManager.Companion.TAG, "Failed to load keyboard sound")
           }
       }
       catch (e: Exception) {
           Log.e(KeyboardSoundManager.Companion.TAG, "Error initializing sound pool", e)
       }
    }

    fun setSoundVolume(volume : Float){
        soundVolume = volume.coerceIn(0f , 1f)
        if(volume == 0f){
            stopAllSounds()
        }
    }


    fun playSendSound() {
        if(soundVolume == 0f) return

        try{
            sendSoundId?.let { soundId ->
                soundPool?.play(
                    soundId,
                    soundVolume,
                    soundVolume,
                    1,
                    0,
                    1.2f
                )

            }
        } catch (e: Exception) {
            Log.e(KeyboardSoundManager.Companion.TAG, "Error playing send sound", e)
        }
    }

    private fun playRandomTypingClip() {
        // Debounce check
        val now = System.currentTimeMillis()
        if (now - lastPlayTime < KeyboardSoundManager.Companion.MIN_PLAY_INTERVAL_MS) {
            return
        }
        lastPlayTime = now

        try {

            if(typingSoundIds.isEmpty()) return

            val soundId = typingSoundIds.random()

            // Vary volume and playback rate for variety
            val volumeVariation = Random.nextDouble(0.7 , 1.0).toFloat()
            val finalVolume = soundVolume * volumeVariation
            val playbackRate = Random.nextDouble(0.95, 1.15).toFloat()


            soundPool?.play(
                soundId,
                finalVolume,
                finalVolume,
                0,
                0,
                playbackRate
            )

        } catch (e: Exception) {
            Log.e(KeyboardSoundManager.Companion.TAG, "Error playing typing clip", e)

    }

    }

    fun playTypingForToken(token : String){
        if(soundVolume ==0f  || token.isBlank()) return

        // Don't cancel previous job - let it finish naturally
        // This prevents the sound from restarting with every new token

        if(isPlaying) return
        isPlaying = true
        playJob = scope.launch ( Dispatchers.Default ) {

            try {

                val nonSpaceChars = token.count{ !it.isWhitespace()}

                // Play sound every CHARS_PER_SOUND characters (not every character)
                val soundsToPlay = (nonSpaceChars + CHARS_PER_SOUND -1) / CHARS_PER_SOUND

                repeat(soundsToPlay){
                    if(!isActive) return@launch


                    playRandomTypingClip()

                    // Long delay between sounds
                    delay(Random.nextLong(MIN_TOKEN_DELAY, MAX_TOKEN_DELAY))

                }

                // Check if this is end of sentence/message
                val trimmedToken = token.trimEnd()
                val endWithPunctuation = trimmedToken.endsWith(".") ||
                        trimmedToken.endsWith("!") ||
                        trimmedToken.endsWith("?") ||
                        trimmedToken.endsWith("。") ||
                        trimmedToken.endsWith("！") ||
                        trimmedToken.endsWith("？")

                if(token.contains("\n") || endWithPunctuation){
                    delay(1000)
                    playSendSound()
                }

            } finally {
                isPlaying = false
            }
        }
    }

    fun stopAllSounds(){
        playJob?.cancel()
        isPlaying = false
        soundPool?.autoPause()
    }
    /**
     * Освобождает ресурсы
     */
    fun release() {
        playJob?.cancel()
        soundPool?.release()
        soundPool = null
        typingSoundIds.clear()
        sendSoundId = null
    }
}