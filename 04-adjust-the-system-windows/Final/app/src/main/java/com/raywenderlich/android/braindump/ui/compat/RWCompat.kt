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

package com.raywenderlich.android.braindump.ui.compat

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.raywenderlich.android.braindump.isAtLeastAndroid11

class RWCompat(private val view: View, private val container: View) : RWCompatActions {

  private val rwCompat10 by lazy { RWCompat10() }
  private val rwCompat11 by lazy { RWCompat11(view, container) }

  /**
   * This method should be called when [androidx.core.view.WindowCompat.setDecorFitsSystemWindows]
   * is set as false. Which means it's the app responsibility to handle the system UI. Since
   * keyboard animations are only available for devices that are running Android 11. Only
   * [RWCompat11] defines this behaviour.
   */
  override fun setupUiWindowInsets() {
    if (isAtLeastAndroid11()) {
      rwCompat11.setUiWindowInsets()
    }
  }

  /**
   * In case the app is running on a device with Android 11 the keyboard will animate seamlessly
   * with the rest of the UI. If not, the behaviour is just popup the screen and afterwards the
   * UI is updated.
   */
  override fun setupKeyboardAnimations() {
    if (isAtLeastAndroid11()) {
      rwCompat11.animateKeyboardDisplay()
    }
  }

  /**
   * Returns an instance of [androidx.recyclerview.widget.LinearLayoutManager] to be used in a
   * [androidx.recyclerview.widget.RecyclerView]. In case the app is running on an Android 11
   * device it will also set a pre-built animation with the keyboard (ime).
   *
   * If user scrolls the list to the top, the keyboard will start to be displayed following the
   * same speed as the user movement. If the scroll is done in the opposite direction, it will
   * close the keyboard.
   *
   */
  override fun createLinearLayoutManager(context: Context): LinearLayoutManager {
    return if (isAtLeastAndroid11()) {
      rwCompat11.createLinearLayoutManager(context, view)
    } else {
      rwCompat10.createLinearLayoutManager(context)
    }
  }

  override fun closeKeyboard(view: View) {
    if (isAtLeastAndroid11()) {
      rwCompat11.closeKeyboard(view)
    } else {
      rwCompat10.closeKeyboard(view)
    }
  }
}