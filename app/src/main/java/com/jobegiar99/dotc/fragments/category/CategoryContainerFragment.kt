package com.jobegiar99.dotc.fragments.category

import android.R
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
import com.jobegiar99.dotc.databinding.FragmentCategoryContainerBinding
import com.jobegiar99.dotc.databinding.FragmentGameContainerBinding
import com.jobegiar99.dotc.db.appDatabase
import com.jobegiar99.dotc.fragments.game.GameContainerFragmentDirections
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class CategoryContainerFragment : Fragment() {


    private var _binding: FragmentCategoryContainerBinding? = null
    private val binding get() = _binding !!
    private val args: CategoryContainerFragmentArgs by navArgs()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentCategoryContainerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)

        val db = Room.databaseBuilder(this.requireContext(), appDatabase::class.java,"dbTest").build()

        lifecycleScope.launch {
            withContext( Dispatchers.IO ){

                val categoryTitles = db.DaoCategory().getCategories(args.gameID)

                withContext( Dispatchers.Main ){
                    addCategoriesToList( categoryTitles )
                }
            }
        }

        binding.buttonCreateCategory.setOnClickListener(View.OnClickListener{
            var action = CategoryContainerFragmentDirections.actionCategoryContainerFragmentToCreateEditCategoryFragment(args.globalUsername,args.gameID,false,"")
            findNavController().navigate(action)
        })

        binding.buttonEditCategory.setOnClickListener(View.OnClickListener {
            if( binding.categoryDropDownList.selectedItem != null){
                lifecycleScope.launch {
                    withContext(Dispatchers.IO) {
                        val category = db.DaoCategory().getCategory(
                            binding.categoryDropDownList.selectedItem.toString(),
                            args.gameID
                        )

                        withContext(Dispatchers.Main) {

                            var action =
                                CategoryContainerFragmentDirections.actionCategoryContainerFragmentToCreateEditCategoryFragment(
                                    args.globalUsername,
                                    args.gameID,
                                    true,
                                    category.categoryTitle
                                )
                            findNavController().navigate(action)

                        }
                    }
                }
            }
        })

        binding.buttonDeleteCategory.setOnClickListener(View.OnClickListener {
            tryToDeleteCategory()
        })

        binding.buttonOpenCategory.setOnClickListener(View.OnClickListener {
            tryToOpenCategory()
        })

        binding.categoryContainerReturnButton.setOnClickListener {
            val action = CategoryContainerFragmentDirections.actionCategoryContainerFragmentToGameContainerFragment(args.globalUsername)
            findNavController().navigate(action)
        }

    }

    private fun addCategoriesToList(  categoryTitles: List<String>  ){

        binding.categoryDropDownList.adapter =
            ArrayAdapter<String>(requireContext(), R.layout.simple_spinner_item, categoryTitles )

    }

    private fun tryToDeleteCategory(){
        val dropListItem = binding.categoryDropDownList.selectedItem
        if( dropListItem == null) {
            Toast.makeText(
                context,
                "You must select a category to be able to delete it!",
                Toast.LENGTH_SHORT
            ).show()
            return
        }
        val selectedElement = dropListItem.toString()
        val db = Room.databaseBuilder(this.requireContext(), appDatabase::class.java,"dbTest").build()
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                val category = db.DaoCategory().getCategory(selectedElement,args.gameID)
                db.DaoCategory().deleteCategory(category)
                val titles:List<String> = db.DaoCategory().getCategories(args.gameID)
                withContext(Dispatchers.Main) {

                    addCategoriesToList(titles)
                }
            }
        }
    }


    private fun tryToOpenCategory(){
        val dropListItem = binding.categoryDropDownList.selectedItem
        if( dropListItem == null) {
            Toast.makeText(
                context,
                "You must select a category to be able to access the category's entries!",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        val selectedElement = dropListItem.toString()
        Toast.makeText(context,selectedElement,Toast.LENGTH_SHORT).show()
        val db = Room.databaseBuilder(this.requireContext(), appDatabase::class.java,"dbTest").build()

        lifecycleScope.launch {
            withContext(Dispatchers.IO) {

                val category = db.DaoCategory().getCategory(selectedElement,args.gameID)

                withContext(Dispatchers.Main) {

                    val action = CategoryContainerFragmentDirections.actionCategoryContainerFragmentToEntryContainerFragment(
                        args.globalUsername,category.categoryID,args.gameID
                    )
                    findNavController().navigate(action)
                }
            }
        }
    }
}