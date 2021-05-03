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

package com.raywenderlich.android.braindump.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.selection.SelectionPredicates
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.selection.StorageStrategy
import androidx.recyclerview.widget.RecyclerView
import com.raywenderlich.android.braindump.R
import com.raywenderlich.android.braindump.databinding.FragmentMainBinding
import com.raywenderlich.android.braindump.getAllNotes
import com.raywenderlich.android.braindump.model.Note
import com.raywenderlich.android.braindump.saveAllNotes
import com.raywenderlich.android.braindump.ui.adapters.MainAdapter
import com.raywenderlich.android.braindump.ui.adapters.NoteItemDetailsLookup
import com.raywenderlich.android.braindump.ui.adapters.NoteKeyProvider
import com.raywenderlich.android.braindump.ui.compat.RWCompat
import kotlinx.android.synthetic.main.fragment_main.*


class MainFragment : Fragment(), ActionMode.Callback {

  private lateinit var binding: FragmentMainBinding
  private lateinit var tracker: SelectionTracker<Long>
  private lateinit var rwCompat: RWCompat

  private var actionMode: ActionMode? = null

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
      state: Bundle?): View? {
    binding = FragmentMainBinding.inflate(inflater, container, false)
    return binding.root
  }

  override fun onActivityCreated(savedInstanceState: Bundle?) {
    super.onActivityCreated(savedInstanceState)

    rwCompat = RWCompat(requireView(), binding.root)
    rwCompat.setupUiWindowInsets()
    rwCompat.setupKeyboardAnimations()

    setupToolbar()
    setupUiComponents()
  }

  private fun setupToolbar() {
    val appCompatActivity = activity as AppCompatActivity
    appCompatActivity.setSupportActionBar(binding.toolbar)
    appCompatActivity.setTitle(R.string.app_header)
  }

  @SuppressLint("ClickableViewAccessibility")
  private fun setupUiComponents() {
    val mainAdapter = MainAdapter()
    mainAdapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {

      override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
        super.onItemRangeInserted(positionStart, itemCount)
        binding.rvContent.scrollToPosition(positionStart)
      }
    })

    val manager = rwCompat.createLinearLayoutManager(requireContext())
    manager.stackFromEnd = true

    binding.rvContent.apply {
      setHasFixedSize(true)
      layoutManager = manager
      adapter = mainAdapter
      setOnTouchListener { _, _ ->
        rwCompat.closeKeyboard(binding.etContent)
        false
      }
    }

    binding.etContent.addTextChangedListener(object : TextWatcher {
      override fun beforeTextChanged(text: CharSequence?, start: Int, count: Int, after: Int) {
        // Do nothing
      }

      override fun onTextChanged(text: CharSequence?, start: Int, before: Int, count: Int) {
        // Do nothing
      }

      override fun afterTextChanged(text: Editable?) {
        binding.btnSend.isEnabled = text.toString().isNotEmpty()
      }
    })

    binding.etContent.requestFocus()

    binding.btnSend.isEnabled = false
    binding.btnSend.setOnClickListener {
      val notes = mainAdapter.currentList.toMutableList()
      notes.add(Note(et_content.text.toString(), System.currentTimeMillis()))

      updateList(notes)
      et_content.text.clear()

      saveAllNotes(requireContext(), notes)
    }

    tracker = SelectionTracker.Builder(
        "selectionNote",
        binding.rvContent,
        NoteKeyProvider(mainAdapter),
        NoteItemDetailsLookup(binding.rvContent),
        StorageStrategy.createLongStorage()
    ).withSelectionPredicate(
        SelectionPredicates.createSelectAnything()
    ).build()

    tracker.addObserver(
        object : SelectionTracker.SelectionObserver<Long>() {
          override fun onSelectionChanged() {
            super.onSelectionChanged()

            if (actionMode == null) {
              actionMode = activity?.startActionMode(this@MainFragment)
            }

            val items = tracker.selection!!.size()
            if (items > 0) {
              actionMode?.title = getString(R.string.action_selected, items)
            } else {
              actionMode?.finish()
            }
          }
        })

    mainAdapter.tracker = tracker

    updateList(getAllNotes(requireContext()))

    // Scroll to last note
    if (mainAdapter.currentList.size > 0) {
      binding.rvContent.smoothScrollToPosition(mainAdapter.currentList.size - 1)
    }
  }

  private fun updateList(notes: List<Note>) {
    val adapter = binding.rvContent.adapter as MainAdapter
    adapter.submitList(notes)
  }

  //region ActionMode.Callback

  override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
    mode?.menuInflater?.inflate(R.menu.menu_main, menu)
    return true
  }

  override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?) = true

  override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
    return when (item!!.itemId) {
      R.id.action_delete -> {
        val mainAdapter = binding.rvContent.adapter as MainAdapter

        val selected = mainAdapter.currentList.filter {
          tracker.selection.contains(it.timestamp)
        }

        val notes = mainAdapter.currentList.toMutableList()
        notes.removeAll(selected)
        updateList(notes)

        saveAllNotes(requireContext(), notes)

        actionMode?.finish()

        true
      }
      else -> {
        false
      }
    }
  }

  override fun onDestroyActionMode(mode: ActionMode?) {
    tracker.clearSelection()
    actionMode = null
  }

  //endregion ActionMode.Callback
}