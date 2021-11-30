package com.jobegiar99.dotc.fragments.category

import android.os.Bundle
import android.view.ContextThemeWrapper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.room.Room
import com.google.android.material.textfield.TextInputEditText
import com.google.android.play.core.internal.al
import com.jobegiar99.dotc.R
import com.jobegiar99.dotc.databinding.FragmentCategoryContainerBinding
import com.jobegiar99.dotc.databinding.FragmentCreateEditCategoryBinding
import com.jobegiar99.dotc.db.appDatabase
import com.jobegiar99.dotc.fragments.game.CreateGameFragmentArgs
import com.jobegiar99.dotc.helperClasses.EncoderDecoder
import com.jobegiar99.dotc.model.Category
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class CreateEditCategoryFragment : Fragment() {

    private var _binding: FragmentCreateEditCategoryBinding? = null
    private val binding get() = _binding !!
    private val args: CreateEditCategoryFragmentArgs by navArgs()
    private var createdElements = mutableListOf<TextInputEditText>()
    private val invalidCategoryTag = "<DotC.InvalidCategoryElement>"
    private val encoder = EncoderDecoder()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentCreateEditCategoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if( args.modify ){

            binding.createEditCategoryConstraintLayout.removeView(binding.buttonAddCategoryElement)
            binding.createEditCategoryConstraintLayout.removeView(binding.buttonRemoveCategoryElement)
            binding.buttonCategoryEditorCreateCategory.text = "Update category"
            binding.categoryEditorInputName.setText( args.categoryTitle )
            loadCategoryItems()
            binding.buttonCategoryEditorCreateCategory.setOnClickListener(View.OnClickListener {
                updateCategory()
            })

        }else{

            binding.buttonAddCategoryElement.setOnClickListener(View.OnClickListener {
                addCategoryElement()
            })

            binding.buttonRemoveCategoryElement.setOnClickListener(View.OnClickListener {
                removeCategoryElement()
            })

            binding.buttonCategoryEditorCreateCategory.setOnClickListener(View.OnClickListener {
                createCategory()
            })

        }

        binding.categoryEditorReturnButton.setOnClickListener {

            var action = CreateEditCategoryFragmentDirections.actionCreateEditCategoryFragmentToCategoryContainerFragment(args.globalUsername,args.gameID)
            findNavController().navigate(action)

        }

    }

    private fun addCategoryElement(){
        var element = TextInputEditText(ContextThemeWrapper(context,R.style.categoryElement))
        binding.categoryEditorLinearLayout.addView(element)
        createdElements.add(element)
    }

    private fun removeCategoryElement(){
        if(createdElements.size > 0){
            binding.categoryEditorLinearLayout.removeView(createdElements.removeLast())
        }
    }

    private fun createCategory(){
        if( createdElements.size == 0) {
            Toast.makeText(context, "The category needs to have at least one element", Toast.LENGTH_SHORT).show()
            return
        }

        val db = Room.databaseBuilder(this.requireContext(), appDatabase::class.java,"dbTest").build()

        lifecycleScope.launch {
            withContext(Dispatchers.IO){

                val currentCategoryName = binding.categoryEditorInputName.text.toString()
                if(currentCategoryName == ""){

                    withContext(Dispatchers.Main){
                        Toast.makeText(context, "The category's name can't be empty", Toast.LENGTH_SHORT).show()
                    }

                }else {

                    val category = db.DaoCategory().getCategory(currentCategoryName,args.gameID)
                    if(category != null){
                        withContext(Dispatchers.Main){
                            Toast.makeText(context, "A category with that name already exists", Toast.LENGTH_SHORT).show()
                        }
                    }else{

                        var categoryElements = getCategoryElements()

                        if(categoryElements[0] == invalidCategoryTag){

                            withContext(Dispatchers.Main){
                                Toast.makeText(context, "Category elements can't be empty", Toast.LENGTH_SHORT).show()
                            }

                        }else {



                            val encodedCategory = encoder.encodeCategory(categoryElements)
                            val newCategory = Category(currentCategoryName,args.gameID,encodedCategory)
                            db.DaoCategory().insertCategory(newCategory)


                           withContext(Dispatchers.Main){
                                val action = CreateEditCategoryFragmentDirections.actionCreateEditCategoryFragmentToCategoryContainerFragment(args.globalUsername,args.gameID)
                               findNavController().navigate(action)

                           }

                        }


                    }
                }

            }
        }
    }

    private fun getCategoryElements(): Array<String>{
        var categoryElements = Array<String>(createdElements.size){ i -> "" }
        var index = 0
        while( index < createdElements.size ){
            val currentText = createdElements[index].text.toString()
            if( currentText == ""){
                return Array<String>(1,{invalidCategoryTag})
            }else{
                categoryElements[index] = currentText
            }
            index ++
        }

        return categoryElements
    }

    private fun loadCategoryItems(){
        val db = Room.databaseBuilder(this.requireContext(), appDatabase::class.java,"dbTest").build()
        lifecycleScope.launch {
            withContext(Dispatchers.IO){

                var category = db.DaoCategory().getCategory(args.categoryTitle,args.gameID)
                val decodedCategory = encoder.decodeCategory(category.encodedCategory)
                withContext(Dispatchers.Main){
                    var index = 0
                    decodedCategory.forEach{
                        var currentElement = TextInputEditText(ContextThemeWrapper(context,R.style.categoryElement))
                        currentElement.setText(it.toString())
                        binding.categoryEditorLinearLayout.addView(currentElement)
                        createdElements.add(currentElement)
                    }
                }

            }
        }
    }

    private fun updateCategory(){
        val db = Room.databaseBuilder(this.requireContext(), appDatabase::class.java,"dbTest").build()
        lifecycleScope.launch {
            withContext(Dispatchers.IO){

                val currentCategoryName = binding.categoryEditorInputName.text.toString()
                if(currentCategoryName == ""){

                    withContext(Dispatchers.Main){
                        Toast.makeText(context, "The category's name can't be empty", Toast.LENGTH_SHORT).show()
                    }

                }else {

                    val category = db.DaoCategory().getCategory(currentCategoryName,args.gameID)
                    if(category != null){
                        withContext(Dispatchers.Main){
                            Toast.makeText(context, "A category with that name already exists", Toast.LENGTH_SHORT).show()
                        }
                    }else{

                        var categoryElements = getCategoryElements()

                        if(categoryElements[0] == invalidCategoryTag){

                            withContext(Dispatchers.Main){
                                Toast.makeText(context, "Category elements can't be empty", Toast.LENGTH_SHORT).show()
                            }

                        }else {

                            var updatedCategory = db.DaoCategory().getCategory(args.categoryTitle,args.gameID)
                            var encodedCategory = encoder.encodeCategory(getCategoryElements())
                            updatedCategory.encodedCategory = encodedCategory
                            updatedCategory.categoryTitle = binding.categoryEditorInputName.text.toString()
                            db.DaoCategory().updateCategory(updatedCategory)


                            withContext(Dispatchers.Main){
                                val action = CreateEditCategoryFragmentDirections.actionCreateEditCategoryFragmentToCategoryContainerFragment(args.globalUsername,args.gameID)
                                findNavController().navigate(action)

                            }

                        }


                    }
                }

            }
        }
    }


}