package com.jobegiar99.dotc.fragments.game

import android.R.attr
import android.graphics.pdf.PdfDocument
import android.os.Bundle
import android.util.AttributeSet
import android.view.ContextThemeWrapper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.jobegiar99.dotc.databinding.FragmentGameContainerBinding
import org.w3c.dom.Text
import androidx.core.view.marginTop
import androidx.fragment.app.FragmentContainer
import androidx.fragment.app.FragmentContainerView
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.room.Room
import com.jobegiar99.dotc.fragments.game.GameContainerFragmentArgs
import com.jobegiar99.dotc.fragments.game.GameContainerFragmentDirections
import com.jobegiar99.dotc.db.appDatabase
import com.jobegiar99.dotc.model.Game
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.w3c.dom.Document
import android.os.Environment
import android.R.attr.path
import android.graphics.Paint
import android.graphics.fonts.Font
import java.io.File
import java.io.FileOutputStream
import android.graphics.pdf.PdfDocument.PageInfo





class GameContainerFragment : Fragment() {

    private var _binding: FragmentGameContainerBinding? = null
    private val binding get() = _binding !!
    private val args: GameContainerFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentGameContainerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)
        val db = Room.databaseBuilder(this.requireContext(), appDatabase::class.java,"dbTest").build()
        updateScrollView(db)

        binding.buttonCreateGame.setOnClickListener(View.OnClickListener {
            var action = GameContainerFragmentDirections.actionGameContainerFragmentToCreateGameFragment(args.globalUsername,false,-1,"")
            findNavController().navigate(action)

        })

        binding.buttonEditGame.setOnClickListener(View.OnClickListener {
            tryToEditGame(db)
        })

        binding.buttonDeleteGame.setOnClickListener(View.OnClickListener {
            tryToDeleteGame(db)
        })

        binding.buttonLoadGame.setOnClickListener(View.OnClickListener {
            tryToOpenGame(db)
        })

    }

    private fun updateScrollView(db:appDatabase){
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {

                val gameTitles = db.DaoGame().getGameTitles(args.globalUsername)

                withContext(Dispatchers.Main) {
                    addGamesToList(gameTitles)
                }
            }
        }
    }

    private fun addGamesToList(  gameTitles: List<String>  ){
        var gameDropDown = binding.gameDropList
        val adapter = ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_item, gameTitles )

        gameDropDown.adapter = adapter

    }

    private fun tryToEditGame(db:appDatabase){
        val dropListItem = binding.gameDropList.selectedItem
        if( dropListItem == null) {
            Toast.makeText(
                context,
                "You must select a game to be able to edit one!",
                Toast.LENGTH_SHORT
            ).show()
            return
        }
        val selectedElement = dropListItem.toString()

        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                val game = db.DaoGame().getGame(args.globalUsername,selectedElement)
                withContext(Dispatchers.Main) {
                    val action =
                        GameContainerFragmentDirections.actionGameContainerFragmentToCreateGameFragment(
                            args.globalUsername,
                            true,
                            game.gameID,
                            game.gameTitle
                        )
                    findNavController().navigate(action)
                }
            }
        }

    }

    private fun tryToDeleteGame(db: appDatabase){
        val dropListItem = binding.gameDropList.selectedItem
        if( dropListItem == null) {
            Toast.makeText(
                context,
                "You must select a game to be able to delete it!",
                Toast.LENGTH_SHORT
            ).show()
            return
        }
        val selectedElement = dropListItem.toString()

        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                val game = db.DaoGame().getGame(args.globalUsername,selectedElement)
                db.DaoGame().deleteGame(game)
                withContext(Dispatchers.Main) {
                    updateScrollView(db)
                }
            }
        }
    }

    private fun tryToOpenGame(db: appDatabase){
        val dropListItem = binding.gameDropList.selectedItem
        if( dropListItem == null) {
            Toast.makeText(
                context,
                "You must select a game to be able to access the game's categories!",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        val selectedElement = dropListItem.toString()
        Toast.makeText(context,selectedElement,Toast.LENGTH_SHORT).show()


        lifecycleScope.launch {
            withContext(Dispatchers.IO) {

                val game = db.DaoGame().getGame(args.globalUsername,selectedElement)

                withContext(Dispatchers.Main) {

                    val action =
                        GameContainerFragmentDirections.actionGameContainerFragmentToCategoryContainerFragment(
                            args.globalUsername,
                            game.gameID
                        )
                    findNavController().navigate(action)
                }
            }
        }
    }



}