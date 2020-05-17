package me.alberto.googledevelopergroup.add

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import com.google.android.material.snackbar.Snackbar

import me.alberto.googledevelopergroup.R
import me.alberto.googledevelopergroup.databinding.FragmentAddGdgBinding

/**
 * A simple [Fragment] subclass.
 */
class AddGdgFragment : Fragment() {

    private val viewModel: AddGdgViewModel by lazy {
        ViewModelProvider(this).get(AddGdgViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding = FragmentAddGdgBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        viewModel.showSnackbarEvent.observe(viewLifecycleOwner, Observer {
            it.let {
                if (it){
                    Snackbar.make(
                        activity!!.findViewById(android.R.id.content),
                        getString(R.string.application_submitted),
                        Snackbar.LENGTH_SHORT
                    ).show()
                    viewModel.doneShowingSnackbar()
                }
            }
        })


        setHasOptionsMenu(true)

        return inflater.inflate(R.layout.fragment_add_gdg, container, false)
    }

}
