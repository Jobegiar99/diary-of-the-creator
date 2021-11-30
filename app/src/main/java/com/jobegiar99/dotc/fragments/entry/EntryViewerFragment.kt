package com.jobegiar99.dotc.fragments.entry

import android.content.Context
import android.os.Bundle
import android.view.ContextThemeWrapper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.room.Room
import com.google.android.material.textfield.TextInputEditText
import com.google.android.play.core.splitinstall.d
import com.jobegiar99.dotc.R
import com.jobegiar99.dotc.databinding.FragmentCategoryContainerBinding
import com.jobegiar99.dotc.databinding.FragmentEntryContainerBinding
import com.jobegiar99.dotc.databinding.FragmentEntryViewerBinding
import com.jobegiar99.dotc.db.appDatabase
import com.jobegiar99.dotc.fragments.category.CategoryContainerFragmentArgs
import com.jobegiar99.dotc.helperClasses.EncoderDecoder
import com.jobegiar99.dotc.model.Entry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.w3c.dom.Text


class EntryViewerFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var _binding: FragmentEntryViewerBinding? = null
    private val binding get() = _binding !!
    private val args: EntryViewerFragmentArgs by navArgs()
    private var createdElements = mutableListOf<TextInputEditText>()
    private var createdElementsView = mutableListOf<TextView>()
    private val invalidCategoryTag = "<DotC.InvalidCategoryElement>"
    private val encoderDecoder = EncoderDecoder()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentEntryViewerBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val db = Room.databaseBuilder(this.requireContext(), appDatabase::class.java,"dbTest").build()

        buildEntryBody(db)

        binding.entryTypeText.setText(args.entryTitle)

        if(!args.create){
            binding.entryNameInput.setText(args.entryTitle)
        }

        if( args.create ){
            lifecycleScope.launch {
                withContext(Dispatchers.IO){
                    var category = db.DaoCategory().getCategoryByCID(args.categoryID)
                    withContext(Dispatchers.Main){
                        binding.entryTypeText.setText("Create a New " + category.categoryTitle)
                    }
                }
            }
            binding.entryViewerActionButton.setText("Create")
            binding.entryViewerActionButton.setOnClickListener( View.OnClickListener {
                actionButtonHelper(db, false)
            })

        }
        else if ( args.modify){

            binding.entryViewerActionButton.setText("Update Entry")
            lifecycleScope.launch {
                withContext(Dispatchers.IO){
                    var category = db.DaoCategory().getCategoryByCID(args.categoryID)
                    withContext(Dispatchers.Main){
                        binding.entryTypeText.setText("Modify Your " + category.categoryTitle)
                    }
                }
            }
            binding.entryViewerActionButton.setText("Modify")



            binding.entryViewerActionButton.setOnClickListener( View.OnClickListener {
                actionButtonHelper(db, true)
            })

        }
        else if ( args.viewEntry){

            binding.entryConstraintLayout.removeView(binding.entryViewerActionButton)
            binding.entryContainerLinearLayout.removeView((binding.entryNameInputLayout))
            binding.entryContainerLinearLayout.removeView(binding.entryNameInput)


        }

        binding.buttonEntryViewerReturn.setOnClickListener{
            val action = EntryViewerFragmentDirections.actionEntryViewerFragmentToEntryContainerFragment(args.globalUsername,args.categoryID, args.gameID)
            findNavController().navigate(action)
        }

    }

    private fun actionButtonHelper( db: appDatabase, updateEntry: Boolean){

        val userInput = getEntryElements()
        if( userInput[0] == invalidCategoryTag || binding.entryNameInput.text.toString() == ""){

            Toast.makeText(context, "A field can't be empty", Toast.LENGTH_SHORT).show()
            return
        }

        val encodedEntry = encoderDecoder.encodeEntry(userInput)

        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                if (updateEntry) {

                    var entry = db.DaoEntry().getEntry(args.categoryID,args.entryID)
                    entry.encodedEntry = encodedEntry
                    entry.entryName = binding.entryNameInput.text.toString()
                    db.DaoEntry().updateEntry(entry)

                } else {

                    var entry = Entry( binding.entryNameInput.text.toString(),
                                        args.categoryID,
                                        encodedEntry)

                    db.DaoEntry().insertEntry(entry)

                }
                withContext(Dispatchers.Main){
                    val action = EntryViewerFragmentDirections.actionEntryViewerFragmentToEntryContainerFragment(args.globalUsername,args.categoryID,args.gameID)
                    findNavController().navigate(action)
                }
            }
        }
    }

    private fun getEntryElements(): Array<String>{
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

    private fun buildEntryBody(db: appDatabase){

        lifecycleScope.launch {

            withContext(Dispatchers.IO){

                val category = db.DaoCategory().getCategoryByCID(args.categoryID)

                val encodedCategory = encoderDecoder.decodeCategory(category.encodedCategory)

                withContext(Dispatchers.Main) {

                    encodedCategory.forEach {

                        var elementTitle = TextView(ContextThemeWrapper(context, R.style.entryCategoryHeader))
                        elementTitle.setText(it.toString())
                        binding.entryContainerLinearLayout.addView(elementTitle)

                        if(args.viewEntry) {

                            var elementBody =TextView(ContextThemeWrapper(context, R.style.entryTextViewStyle))
                            createdElementsView.add(elementBody)
                            binding.entryContainerLinearLayout.addView(elementBody)

                        }else {

                            var elementBody =TextInputEditText(ContextThemeWrapper(context, R.style.categoryElement))
                            elementBody.setHint("Enter the field's content here")
                            createdElements.add(elementBody)
                            binding.entryContainerLinearLayout.addView(elementBody)

                        }
                    }
                    if( !args.create){
                        fillEntryBody(db)
                    }
                }
            }
        }
    }

    private fun fillEntryBody(db:appDatabase){
        lifecycleScope.launch {

            withContext(Dispatchers.IO) {
                val entry = db.DaoEntry().getEntry(args.categoryID, args.entryID)
                val decodedEntry = encoderDecoder.decodeEntry(entry.encodedEntry)


                if (decodedEntry.count() > 0){

                    withContext(Dispatchers.Main) {

                        var index = 0
                        while (index < decodedEntry.count()) {

                            if (args.viewEntry) {
                                if( index < createdElementsView.count())
                                    createdElementsView[index].setText(decodedEntry[index])

                            } else {
                                if( index < createdElements.count())
                                    createdElements[index].setText(decodedEntry[index])
                            }
                            index++
                        }
                    }
                }
            }
        }
    }
}