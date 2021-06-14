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

package com.raywenderlich.android.braindump.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.raywenderlich.android.braindump.R
import com.raywenderlich.android.braindump.databinding.ItemNoteBinding
import com.raywenderlich.android.braindump.model.Note
import com.raywenderlich.android.braindump.timestampToDateConversion

class MainAdapter : ListAdapter<Note, MainAdapter.NotesViewHolder>(
    DiffCallback()) {

  private lateinit var binding: ItemNoteBinding
  var tracker: SelectionTracker<Long>? = null

  override fun onCreateViewHolder(viewGroup: ViewGroup, position: Int): NotesViewHolder {
    binding = ItemNoteBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)
    return NotesViewHolder(binding)
  }

  override fun onBindViewHolder(viewHolder: NotesViewHolder, position: Int) {
    val note = getItem(position)
    viewHolder.bind(note)
  }

  override fun getItemCount(): Int {
    return currentList.size
  }

  private class DiffCallback : DiffUtil.ItemCallback<Note>() {

    override fun areItemsTheSame(oldItem: Note, newItem: Note) =
        oldItem.timestamp == newItem.timestamp

    override fun areContentsTheSame(oldItem: Note, newItem: Note) =
        oldItem == newItem
  }

  inner class NotesViewHolder(private val itemBinding: ItemNoteBinding) : RecyclerView.ViewHolder
  (itemBinding.root) {

    fun bind(note: Note) {
      itemBinding.tvNote.text = note.content
      itemBinding.tvTimestamp.text = timestampToDateConversion(note.timestamp)

      tracker?.let {

        if (it.isSelected(note.timestamp)) {
          itemBinding.rlContainer.setBackgroundColor(
              ContextCompat.getColor(itemBinding.rlContainer.context, R.color.colorPrimary60))
        } else {
          itemBinding.rlContainer.background = null
        }
      }
    }

    fun getNote(): ItemDetailsLookup.ItemDetails<Long> =

        object : ItemDetailsLookup.ItemDetails<Long>() {

          override fun getPosition(): Int = adapterPosition

          override fun getSelectionKey(): Long = getItem(adapterPosition).timestamp
        }
  }
}