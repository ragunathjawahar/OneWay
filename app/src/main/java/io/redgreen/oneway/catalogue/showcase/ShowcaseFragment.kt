package io.redgreen.oneway.catalogue.showcase

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.redgreen.oneway.catalogue.R
import io.redgreen.oneway.catalogue.bmi.BmiFragment
import io.redgreen.oneway.catalogue.budapest.BudapestFragment
import io.redgreen.oneway.catalogue.counter.CounterFragment
import io.redgreen.oneway.catalogue.placards.PlacardsFragment
import io.redgreen.oneway.catalogue.showcase.ShowpieceAdapter.ShowpieceListener
import io.redgreen.oneway.catalogue.signup.SignUpFragment
import kotlinx.android.synthetic.main.showcase_fragment.*

class ShowcaseFragment : Fragment(), ShowpieceListener {
  private val showpieces = listOf(
      Showpiece("Budapest") { BudapestFragment() },
      Showpiece("BMI Calculator") { BmiFragment() },
      Showpiece("Counter") { CounterFragment() },
      Showpiece("Sign Up Form") { SignUpFragment() },
      Showpiece("Placards") { PlacardsFragment() }
  )

  override fun onCreateView(
      inflater: LayoutInflater,
      container: ViewGroup?,
      savedInstanceState: Bundle?
  ): View =
      inflater.inflate(R.layout.showcase_fragment, container, false)

  override fun onActivityCreated(savedInstanceState: Bundle?) {
    super.onActivityCreated(savedInstanceState)
    val layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
    with(showpiecesRecyclerView) {
      setLayoutManager(layoutManager)
      adapter = ShowpieceAdapter(showpieces, this@ShowcaseFragment)
    }
  }

  override fun display(showpieceFragment: Fragment) {
    fragmentManager?.apply {
      beginTransaction()
          .replace(R.id.container, showpieceFragment)
          .addToBackStack(null)
          .commit()
    }
  }
}
