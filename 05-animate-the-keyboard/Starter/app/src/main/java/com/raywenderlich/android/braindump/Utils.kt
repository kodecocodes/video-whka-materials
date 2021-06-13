/*
 * Copyright (c) 2020 Razeware LLC
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * Notwithstanding the foregoing, you may not use, copy, modify, merge, publish,
 * distribute, sublicense, create a derivative work, and/or sell copies of the
 * Software in any work that is designed, intended, or marketed for pedagogical or
 * instructional purposes related to programming, coding, application development,
 * or information technology.  Permission for such use, copying, modification,
 * merger, publication, distribution, sublicensing, creation of derivative works,
 * or sale is expressly withheld.
 *
 * This project and source code may use libraries or frameworks that are
 * released under various Open-Source licenses. Use of those libraries and
 * frameworks are governed by their own individual licenses.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.raywenderlich.android.braindump

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import androidx.preference.PreferenceManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.raywenderlich.android.braindump.model.Note
import java.text.SimpleDateFormat
import java.util.*


@SuppressLint("ConstantLocale")
private val dataFormat = SimpleDateFormat("dd/MM/yy hh:mm a", Locale.getDefault())

private const val ALL_NOTES = "all_notes"

fun isAtLeastAndroid11() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.R

fun timestampToDateConversion(timestamp: Long): String {
  val calendar = Calendar.getInstance()
  calendar.timeInMillis = timestamp
  return dataFormat.format(calendar.timeInMillis)
}

fun saveAllNotes(context: Context, list: List<Note>) {
  val json = Gson().toJson(list)

  val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
  sharedPreferences.edit().putString(ALL_NOTES, json).apply()
}

fun getAllNotes(context: Context): List<Note> {
  val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
  val json = sharedPreferences.getString(ALL_NOTES, "")

  if (json.isNullOrEmpty()) {
    return emptyList()
  }

  val type = object : TypeToken<ArrayList<Note>>() {}.type
  return Gson().fromJson(json, type)
}