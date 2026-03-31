package com.example.rampu2506_padeler.fragments

import android.app.Dialog
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import com.example.rampu2506_padeler.R
import com.example.rampu2506_padeler.repositories.RepoProvider
import kotlinx.coroutines.launch


class ReportDialogFragment : DialogFragment() {
    companion object {
        private const val ARG_USER_ID = "reported_user_id"
        fun newInstance(reportedUserId: Int): ReportDialogFragment{
            val f = ReportDialogFragment()
            f.arguments = Bundle().apply {
                putInt(ARG_USER_ID, reportedUserId)
            }
            return f

        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val reportedUserId = arguments?.getInt(ARG_USER_ID) ?: throw IllegalStateException("Reported user id missing!")

        val view = requireActivity().layoutInflater.inflate(R.layout.fragment_report_dialog, null)

        val etReason = view.findViewById<EditText>(R.id.etReportReason)

        return AlertDialog.Builder(requireContext())
            .setTitle(R.string.report_user)
            .setView(view)
            .setNegativeButton(android.R.string.cancel, null)
            .setPositiveButton(android.R.string.ok) { _, _ ->
                val appContext = requireActivity().applicationContext
                val text = etReason.text.toString()

                val msgOk = appContext.getString(R.string.report_sent)
                val msgErr = appContext.getString(R.string.report_failed)

                lifecycleScope.launch {
                    try {
                        RepoProvider.reports.createReport(reportedUserId, text)
                        Toast.makeText(
                            appContext,
                            msgOk,
                            Toast.LENGTH_SHORT
                        ).show()
                    } catch (_: Exception) {
                        Toast.makeText(
                            appContext,
                            msgErr,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
            .create()
    }
}