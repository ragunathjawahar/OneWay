package io.redgreen.oneway.catalogue.placards

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.redgreen.oneway.catalogue.R

class PlacardsFragment : Fragment() {
  override fun onCreateView(
      inflater: LayoutInflater,
      container: ViewGroup?,
      savedInstanceState: Bundle?
  ): View = inflater.inflate(R.layout.placards_fragment, container, false)
}
