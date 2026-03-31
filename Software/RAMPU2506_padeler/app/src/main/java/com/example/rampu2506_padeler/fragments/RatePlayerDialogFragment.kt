package com.example.rampu2506_padeler.fragments

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.RatingBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import com.example.rampu2506_padeler.R
import com.example.rampu2506_padeler.repositories.RepoProvider
import kotlinx.coroutines.launch

class RatePlayerDialogFragment : DialogFragment() {

    private var onDone: ((Boolean) -> Unit)? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val v = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_rate_player, null)

        val rbGrade = v.findViewById<RatingBar>(R.id.rbGrade)
        val etComment = v.findViewById<EditText>(R.id.etComment)
        val btnSend = v.findViewById<Button>(R.id.btnSendRating)

        val commenterId = requireArguments().getInt(ARG_COMMENTER_ID)
        val commentedId = requireArguments().getInt(ARG_COMMENTED_ID)

        val dialog = AlertDialog.Builder(requireContext())
            .setView(v)
            .create()

        btnSend.setOnClickListener {
            val grade = rbGrade.rating.toDouble()
            val comment = etComment.text?.toString()?.trim()?.takeIf { it.isNotBlank() }

            if (grade < 1.0) {
                Toast.makeText(requireContext(), "Pick a grade 1-5", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                val ok = RepoProvider.comments.addComment(
                    commenterId = commenterId,
                    commentedId = commentedId,
                    grade = grade,
                    comment = comment
                )

                if (ok) {
                    Toast.makeText(requireContext(), "Grade saved!", Toast.LENGTH_SHORT).show()
                    onDone?.invoke(true)
                    dialog.dismiss()
                } else {
                    Toast.makeText(requireContext(), "You cant grade again or there was an error.", Toast.LENGTH_SHORT).show()
                    onDone?.invoke(false)
                }
            }
        }

        return dialog
    }

    companion object {
        private const val ARG_COMMENTER_ID = "commenter_id"
        private const val ARG_COMMENTED_ID = "commented_id"

        fun newInstance(
            commenterId: Int,
            commentedId: Int,
            onDone: (Boolean) -> Unit
        ): RatePlayerDialogFragment {
            return RatePlayerDialogFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_COMMENTER_ID, commenterId)
                    putInt(ARG_COMMENTED_ID, commentedId)
                }
                this.onDone = onDone
            }
        }
    }
}
