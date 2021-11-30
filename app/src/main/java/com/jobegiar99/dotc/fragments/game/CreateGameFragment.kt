package com.jobegiar99.dotc.fragments.game

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.room.Room
import com.jobegiar99.dotc.databinding.FragmentCreateGameBinding
import com.jobegiar99.dotc.db.appDatabase
import com.jobegiar99.dotc.model.Game
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CreateGameFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var _binding: FragmentCreateGameBinding? = null
    private val binding get() = _binding!!
    val args : CreateGameFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentCreateGameBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if( args.modifyGame ) {
            binding.fragmentCreateGameButton.text = "Update Game"
            binding.fragmentCreateGameGameTitle.setText(args.gameTitle)
        }


        binding.fragmentCreateGameButton.setOnClickListener(View.OnClickListener {

            handleButtonClick()

        })

        binding.createGameReturnButton.setOnClickListener {

            var action = CreateGameFragmentDirections.actionCreateGameFragmentToGameContainerFragment(args.globalUsername)
            findNavController().navigate(action)

        }
    }

    private fun handleButtonClick(){
        val gameTitleInput = binding.fragmentCreateGameGameTitle.text.toString()

        if(gameTitleInput == null || gameTitleInput == ""){
            Toast.makeText(context,"Please enter a valid input",Toast.LENGTH_SHORT).show()
            return
        }
        if(args.modifyGame)
            modifyGame(gameTitleInput)
        else
            createGame(gameTitleInput)
    }

    private fun modifyGame(gameTitleInput: String){
        val db = Room.databaseBuilder(this.requireContext(), appDatabase::class.java,"dbTest").build()
        lifecycleScope.launch {
            withContext(Dispatchers.IO){

                var game = db.DaoGame().getGameByID(args.gameID)
                game.gameTitle = gameTitleInput

                db.DaoGame().updateGame(game)
                withContext(Dispatchers.Main) {
                    val action =
                        CreateGameFragmentDirections.actionCreateGameFragmentToGameContainerFragment(
                            args.globalUsername
                        )
                    findNavController().navigate(action)
                }
            }
        }
    }

    private fun createGame(gameTitleInput: String){
        val db = Room.databaseBuilder(this.requireContext(), appDatabase::class.java,"dbTest").build()
        var validOperation = true
        lifecycleScope.launch {
            withContext(Dispatchers.IO){

                var game = db.DaoGame().getGame(args.globalUsername,gameTitleInput)

                if(game != null){

                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "The game already exists", Toast.LENGTH_SHORT)
                            .show()
                    }

                }else {

                    var newGame = Game(gameTitleInput, args.globalUsername)
                    db.DaoGame().insertGame(newGame)
                    withContext(Dispatchers.Main) {
                        val action =
                            CreateGameFragmentDirections.actionCreateGameFragmentToGameContainerFragment(
                                args.globalUsername
                            )
                        findNavController().navigate(action)
                    }
                }

            }
        }
    }
}