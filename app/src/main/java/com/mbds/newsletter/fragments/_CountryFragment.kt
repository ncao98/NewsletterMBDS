package com.mbds.newsletter.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mbds.newsletter.NavigationListener
import com.mbds.newsletter.R
import com.mbds.newsletter.data.adapters.ListCountriesAdapter
import com.mbds.newsletter.data.adapters.ListSourcesHandler
import com.mbds.newsletter.models.Article
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class _CountryFragment: Fragment(), ListSourcesHandler {
    private lateinit var recyclerView: RecyclerView
    /**
     * Fonction permettant de définir une vue à attacher à un fragment
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.list_chips_fragment, container, false)
        recyclerView = view.findViewById(R.id.chips_list)
        recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getSources()
    }

    /**
     * Appelle la fonction bindData
     */
    override fun getSources() {
        bindData()
    }

    override fun showArticles(query: String) {
        (activity as? NavigationListener)?.let {
            it.showFragment(ArticleListFragment(query, "_CountryFragment"))
        }
    }

    /**
     * Rempli le recyclerview avec les données récupérées dans le web service
     * Cette action doit s'effectuer sur le thread principale
     * Car on ne peut mas modifier les éléments de vue dans un thread secondaire
     */
    private fun bindData() {
        lifecycleScope.launch(Dispatchers.Main) {
            val adapter = ListCountriesAdapter(this@_CountryFragment)
            recyclerView.adapter = adapter
        }
    }

}