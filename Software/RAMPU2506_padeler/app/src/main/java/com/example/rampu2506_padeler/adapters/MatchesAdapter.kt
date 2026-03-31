package com.example.rampu2506_padeler.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.example.rampu2506_padeler.R
import com.example.rampu2506_padeler.entities.MatchItem

class MatchesAdapter(
    private val context: Context,
    private val onRateClick: (commentedId: Int, position: Int) -> Unit,
    private val onReportClick: (reportedUserId: Int) -> Unit,
    private var items: List<MatchItem> = emptyList()
) : RecyclerView.Adapter<MatchesAdapter.VH>() {
    private val ratedIds = mutableSetOf<Int>()

    class VH(v: View) : RecyclerView.ViewHolder(v) {
        val tvName: TextView = v.findViewById(R.id.tvMatchName)
        val btnRate: ImageButton = v.findViewById(R.id.btnRate)
        val btnWhatsapp: ImageButton = v.findViewById(R.id.btnWhatsapp)
        val btnReport: ImageButton = v.findViewById(R.id.btnReport)
    }

    fun submit(newItems: List<MatchItem>) {
        items = newItems
        notifyDataSetChanged()
    }
    fun setRatedIds(ids: Set<Int>) {
        ratedIds.clear()
        ratedIds.addAll(ids)
        notifyDataSetChanged()
    }
    fun markRated(commentedId: Int, position: Int) {
        ratedIds.add(commentedId)
        notifyItemChanged(position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_match, parent, false)
        return VH(v)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = items[position]

        holder.tvName.text = "${item.otherName} ${item.otherSurname}"

        holder.btnRate.visibility = if (ratedIds.contains(item.otherUserId)) View.GONE else View.VISIBLE

        holder.btnWhatsapp.setOnClickListener {
            openWhatsApp(item.otherPhone)
        }

        holder.btnRate.setOnClickListener {
            val pos = holder.bindingAdapterPosition
            if (pos == RecyclerView.NO_POSITION) return@setOnClickListener

            onRateClick(item.otherUserId, pos)
        }

        holder.btnReport.setOnClickListener {
            onReportClick(item.otherUserId)
        }
    }

    private fun openWhatsApp(rawPhone: String) {
        val digits = rawPhone.filter { it.isDigit() }

        val phone = when {
            digits.startsWith("385") -> digits
            digits.length >= 10 && !digits.startsWith("0") -> digits
            digits.startsWith("0") -> "385" + digits.drop(1)
            else -> return
        }

        val uri = "https://wa.me/$phone".toUri()
        val intent = Intent(Intent.ACTION_VIEW, uri)

        try {
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "Ne mogu otvoriti WhatsApp.", Toast.LENGTH_SHORT).show()
        }
    }
}
