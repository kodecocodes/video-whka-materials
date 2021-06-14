/*
 * Copyright (c) 2021 Razeware LLC
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

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.raywenderlich.android.braindump.compose.R
import com.raywenderlich.android.braindump.getAllNotes
import com.raywenderlich.android.braindump.model.Note
import com.raywenderlich.android.braindump.saveAllNotes
import com.raywenderlich.android.braindump.timestampToDateConversion
import com.raywenderlich.android.braindump.ui.theme.colorAccent
import com.raywenderlich.android.braindump.ui.theme.colorPrimary
import kotlinx.coroutines.launch

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun Chat() {
  val scope = rememberCoroutineScope()

  val context = LocalContext.current
  val notes = remember { mutableStateOf(getAllNotes(context)) }

  val scrollState = rememberLazyListState()

  Column(
      modifier = Modifier.fillMaxSize()
  ) {

    LazyColumn(
        modifier = Modifier
          .fillMaxSize()
          .weight(1f)
          .padding(start = 16.dp, end = 16.dp),
        state = scrollState,
        reverseLayout = true,
        content = {

          items(notes.value) {
            AddChatBubble(it)
          }
        })

    Row(
        modifier = Modifier.padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.End
    ) {

      val active = remember { mutableStateOf(false) }
      val newNote = remember { mutableStateOf("") }

      Row(
          modifier = Modifier
            .fillMaxWidth()
            .weight(1f)

      ) {

        TextField(
            value = newNote.value,
            onValueChange = { newNote.value = it },
            textStyle = MaterialTheme.typography.h1,
            modifier = Modifier
              .fillMaxWidth()
              .background(color = Color.Transparent, shape = CircleShape)
              .border(BorderStroke(2.dp, color = colorAccent), shape = CircleShape)
              .onFocusChanged { focusState ->
                active.value = focusState.isFocused
              },
            label = {
              if (!active.value) {
                Text(
                    text = stringResource(id = R.string.note_add_note)
                )
              }
            },
            colors = TextFieldDefaults.textFieldColors(
                focusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                backgroundColor = Color.Transparent,
                cursorColor = colorAccent
            ),
            shape = CircleShape
        )
      }

      Spacer(modifier = Modifier.width(8.dp))

      Row(modifier = Modifier
          .clickable {
            notes.value =
                listOf(Note(newNote.value, System.currentTimeMillis())) + notes.value

            newNote.value = ""
            scope.launch {
              scrollState.scrollToItem(0)
            }

            saveAllNotes(context, notes.value)
          }

      ) {
        val image = if (active.value && newNote.value.isNotEmpty()) {
          painterResource(R.drawable.ic_send_enabled)
        } else {
          painterResource(R.drawable.ic_send_disabled)
        }

        val description = stringResource(id = R.string.description_create)

        Image(
            painter = image,
            contentDescription = description,
            modifier = Modifier.size(35.dp)
        )
      }
    }
  }
}

@Composable
fun AddChatBubble(note: Note) {
  Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.End
  ) {
    Card(
        backgroundColor = colorPrimary,
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.clip(RoundedCornerShape(8.dp))
    ) {
      Column(modifier = Modifier.padding(8.dp)) {
        Text(
            text = note.content
        )

        Text(
            text = timestampToDateConversion(note.timestamp)
        )
      }
    }
  }

  Spacer(modifier = Modifier.height(4.dp))
}