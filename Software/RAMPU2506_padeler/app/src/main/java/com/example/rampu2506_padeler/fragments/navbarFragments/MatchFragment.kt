package com.example.rampu2506_padeler.fragments.navbarFragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.rampu2506_padeler.R
import com.example.rampu2506_padeler.adapters.MatchesAdapter
import com.example.rampu2506_padeler.fragments.RatePlayerDialogFragment
import com.example.rampu2506_padeler.fragments.ReportDialogFragment
import com.example.rampu2506_padeler.repositories.RepoProvider
import kotlinx.coroutines.launch

class MatchFragment : Fragment() {

    private lateinit var rvMatches: RecyclerView
    private lateinit var tvEmpty: TextView
    private lateinit var adapter: MatchesAdapter

    private fun loggedUserId(): Int {
        val prefs = requireActivity().getSharedPreferences("user_prefs", 0)
        return prefs.getInt("logged_user_id", 0)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_match, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rvMatches = view.findViewById(R.id.rvMatches)
        tvEmpty = view.findViewById(R.id.tvMatchEmpty)

        adapter = MatchesAdapter(
            context = requireContext(),
            onRateClick = { commentedId, position ->
                val meId = loggedUserId()
                RatePlayerDialogFragment
                    .newInstance(meId, commentedId) { success ->
                        if (success) {
                            adapter.markRated(commentedId, position)
                        }
                    }
                    .show(parentFragmentManager, "rate_player")
            },
            onReportClick = {
                reportUserId ->
                ReportDialogFragment.newInstance(reportUserId).show(parentFragmentManager, "report_dialog")
            }
        )


        rvMatches.layoutManager = LinearLayoutManager(requireContext())
        rvMatches.adapter = adapter
    }

    override fun onResume() {
        super.onResume()
        loadMatches()
    }

    private fun loadMatches() {
        val userId = loggedUserId()

        if (userId <= 0) {
            tvEmpty.visibility = View.VISIBLE
            adapter.submit(emptyList())
            return
        }

        viewLifecycleOwner.lifecycleScope.launch {
            val matches = RepoProvider.matches.fetchMyMatches(userId)
            val rated = RepoProvider.comments.getMyRatedIds(userId)
            adapter.setRatedIds(rated)

            adapter.submit(matches)
            tvEmpty.visibility = if (matches.isEmpty()) View.VISIBLE else View.GONE


            if (matches.isEmpty()) {
                tvEmpty.visibility = View.VISIBLE
                adapter.submit(emptyList())
            } else {
                tvEmpty.visibility = View.GONE
                adapter.submit(matches)
            }
        }
    }
}
