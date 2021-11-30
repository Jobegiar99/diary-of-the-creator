package com.jobegiar99.dotc.fragments.entry

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.room.Room
import com.jobegiar99.dotc.R
import com.jobegiar99.dotc.databinding.FragmentCategoryContainerBinding
import com.jobegiar99.dotc.databinding.FragmentEntryContainerBinding
import com.jobegiar99.dotc.db.appDatabase
import com.jobegiar99.dotc.fragments.category.CategoryContainerFragmentArgs
import com.jobegiar99.dotc.fragments.category.CategoryContainerFragmentDirections
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EntryContainerFragment : Fragment() {

    private var _binding: FragmentEntryContainerBinding ? = null
    private val binding get() = _binding !!
    private val args: EntryContainerFragmentArgs by navArgs()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        _binding = FragmentEntryContainerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var db = Room.databaseBuilder(this.requireContext(), appDatabase::class.java,"dbTest").build()
        addEntriesToList(db)

        binding.buttonCreateEntry.setOnClickListener(View.OnClickListener{
            tryToCreateEntry(db)
        })

        binding.buttonEditEntry.setOnClickListener(View.OnClickListener{
            tryToEditEntry(db)
        })

        binding.buttonDeleteEntry.setOnClickListener(View.OnClickListener{
            tryToDeleteEntry(db)
        })

        binding.buttonOpenEntry.setOnClickListener(View.OnClickListener{
            tryToOpenEntry(db)
        })

        binding.entryContainerReturnButton.setOnClickListener {
            val action = EntryContainerFragmentDirections.actionEntryContainerFragmentToCategoryContainerFragment(args.globalUsername,args.gameID)
            findNavController().navigate(action)
        }
    }

    private fun addEntriesToList( db: appDatabase ){

        lifecycleScope.launch {
            withContext(Dispatchers.IO) {

                var entryTitles = db.DaoEntry().getEntryTitles(args.categoryID)

                withContext(Dispatchers.Main) {

                    binding.entryDropDownList.adapter = ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_item, entryTitles)

                }
            }
        }

    }

    private fun tryToCreateEntry(db:appDatabase) {

        var action =
            EntryContainerFragmentDirections.actionEntryContainerFragmentToEntryViewerFragment(
                args.globalUsername,
                args.categoryID,
                -1,
                false,
                true,
                false,
                "Create Entry",
                args.gameID
            )
        findNavController().navigate(action)
    }

    private fun tryToDeleteEntry(db:appDatabase){
        val dropListItem = binding.entryDropDownList.selectedItem
        if( dropListItem == null) {
            Toast.makeText(
                context,
                "You must select an entry to be able to delete it!",
                Toast.LENGTH_SHORT
            ).show()
            return
        }
        val selectedElement = dropListItem.toString()

        lifecycleScope.launch {

            withContext(Dispatchers.IO) {


                val entry = db.DaoEntry().getEntryByName(args.categoryID,selectedElement)
                db.DaoEntry().deleteEntry(entry)

                withContext(Dispatchers.Main) {
                    addEntriesToList(db)
                }

            }
        }
    }

    private fun tryToEditEntry(db:appDatabase){
        val dropListItem = binding.entryDropDownList.selectedItem
        if( dropListItem == null) {
            Toast.makeText(
                context,
                "You must select an entry to be able to edit it!",
                Toast.LENGTH_SHORT
            ).show()
            return
        }
        val selectedElement = dropListItem.toString()

        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                val entry = db.DaoEntry().getEntryByName(args.categoryID,selectedElement)

                val titles:List<String> = db.DaoEntry().getEntryTitles(args.categoryID)

                withContext(Dispatchers.Main) {

                    var action =
                        EntryContainerFragmentDirections.actionEntryContainerFragmentToEntryViewerFragment(
                            args.globalUsername,
                            args.categoryID,
                            entry.entryID,
                            true,
                            false,
                            false,
                            "",
                            args.gameID
                        )

                    findNavController().navigate(action)

                }
            }
        }
    }

    private fun tryToOpenEntry(db:appDatabase){
        val dropListItem = binding.entryDropDownList.selectedItem
        if( dropListItem == null) {
            Toast.makeText(
                context,
                "You must select an entry to be able to see it!",
                Toast.LENGTH_SHORT
            ).show()
            return
        }
        val selectedElement = dropListItem.toString()

        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                val entry = db.DaoEntry().getEntryByName(args.categoryID,selectedElement)

                val titles:List<String> = db.DaoEntry().getEntryTitles(args.categoryID)
                withContext(Dispatchers.Main) {

                    var action =
                        EntryContainerFragmentDirections.actionEntryContainerFragmentToEntryViewerFragment(
                            args.globalUsername,
                            args.categoryID,
                            entry.entryID,
                            false,
                            false,
                            true,
                            selectedElement,
                            args.gameID
                        )

                    findNavController().navigate(action)

                }
            }
        }
    }
}