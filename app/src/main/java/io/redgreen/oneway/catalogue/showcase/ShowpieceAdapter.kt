package io.redgreen.oneway.catalogue.showcase

import android.support.v4.app.Fragment
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import io.redgreen.oneway.catalogue.R
import io.redgreen.oneway.catalogue.showcase.ShowpieceAdapter.ShowpieceListener

class ShowpieceAdapter(
    private val showpieces: List<Showpiece>,
    private val showpieceListener: ShowpieceListener
) : RecyclerView.Adapter<ShowpieceViewHolder>() {
  private lateinit var layoutInflater: LayoutInflater

  interface ShowpieceListener {
    fun display(showpieceFragment: Fragment)
  }

  override fun onCreateViewHolder(
      parent: ViewGroup,
      viewType: Int
  ): ShowpieceViewHolder {
    if (!::layoutInflater.isInitialized) {
      layoutInflater = LayoutInflater.from(parent.context)
    }
    val view = layoutInflater
        .inflate(R.layout.showpiece_recycler_view_item, parent, false)

    return ShowpieceViewHolder(view, showpieceListener)
  }

  override fun onBindViewHolder(holder: ShowpieceViewHolder, position: Int) {
    holder.bind(showpieces[position])
  }

  override fun getItemCount(): Int =
      showpieces.size
}

class ShowpieceViewHolder(
    private val rootView: View,
    private val showpieceListener: ShowpieceListener
): RecyclerView.ViewHolder(rootView) {
  private val titleTextView = rootView.findViewById<TextView>(R.id.titleTextView)

  fun bind(showpiece: Showpiece) {
    titleTextView.text = showpiece.title

    rootView.setOnClickListener {
      showpieceListener.display(showpiece.fragmentCreator())
    }
  }
}
