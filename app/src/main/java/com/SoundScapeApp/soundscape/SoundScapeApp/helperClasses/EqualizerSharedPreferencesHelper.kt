package com.SoundScapeApp.soundscape.SoundScapeApp.helperClasses

import android.content.Context
import android.content.SharedPreferences
import com.SoundScapeApp.soundscape.SoundScapeApp.MainViewModel.Preset
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.qualifiers.ApplicationContext

class EqualizerSharedPreferencesHelper(
    @ApplicationContext private val context: Context,
    private val sharedPreferences: SharedPreferences
){
    private val BASS_LEVEL_KEY = "basslevelkey"
    private val VIRTUALIZER_LEVEL_KEY = "virtualizerlevelkey"
    private val LOUDNESS_LEVEL_KEY = "loudnesslevelkey"
    private val PRESET_KEY = "presetkey"
    private val gson = Gson()
    private val CUSTOM_BAND_LEVELS_KEY = "CUSTOM_BAND_LEVELS"

    fun setBaseLevel(bassLevel:Float){
        sharedPreferences.edit().putFloat(BASS_LEVEL_KEY,bassLevel).apply()
    }
    fun getBaseLevel():Float{
        return sharedPreferences.getFloat(BASS_LEVEL_KEY,0f)
    }

    fun setVirtualizerLevel(virtualizerLevel:Float){
        sharedPreferences.edit().putFloat(VIRTUALIZER_LEVEL_KEY,virtualizerLevel).apply()
    }
    fun getVirtualizerLevel():Float{
        return sharedPreferences.getFloat(VIRTUALIZER_LEVEL_KEY,0f)
    }

    fun setLoudnessLevel(loudnessLevel:Float){
        sharedPreferences.edit().putFloat(LOUDNESS_LEVEL_KEY,loudnessLevel).apply()
    }
    fun getLoudnessLevel():Float{
        return sharedPreferences.getFloat(LOUDNESS_LEVEL_KEY,0f)
    }

    fun setCurrentPreset(preset:Preset){
        sharedPreferences.edit().putString(PRESET_KEY,preset.name).apply()
    }

    fun getCurrentPreset(): Preset {
        val presetName = sharedPreferences.getString(PRESET_KEY, Preset.NORMAL.name)
        return Preset.valueOf(presetName ?: Preset.NORMAL.name)
    }

    fun saveCustomEqualizerBandLevels(levels: List<Float>) {
        val json = gson.toJson(levels)
        sharedPreferences.edit().putString(CUSTOM_BAND_LEVELS_KEY, json).apply()
    }

    fun getCustomEqualizerBandLevels(): List<Float> {
        val json = sharedPreferences.getString(CUSTOM_BAND_LEVELS_KEY, null)
        return if (json != null) {
            val type = object : TypeToken<List<Float>>() {}.type
            gson.fromJson(json, type)
        } else {
            listOf(0f, 0f, 0f, 0f, 0f) // Default value if no levels are saved
        }
    }

}