package com.jobegiar99.dotc

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.withCreated
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.room.Room
import com.jobegiar99.dotc.databinding.FragmentLoginBinding
import com.jobegiar99.dotc.db.appDatabase
import com.jobegiar99.dotc.loginFragmentDirections
import com.jobegiar99.dotc.model.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class loginFragment : Fragment() {
    private var _binding: FragmentLoginBinding ? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoginBinding.inflate(inflater, container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)
        val db = Room.databaseBuilder(requireContext(),appDatabase::class.java,"dbTest").build()


        binding.buttonLogin.setOnClickListener(
            View.OnClickListener {
                attempLogin(db)
            }
        )

        binding.buttonRegister.setOnClickListener(
            View.OnClickListener {
                attempRegister(db)
            }
        )

    }

    private fun attempLogin(db:appDatabase){
        val attempedUser = binding.inputLogin.text.toString()
        val attempedPassword = binding.inputPassword.text.toString()
        var userExists = true
        val toastNotExists = Toast.makeText(context,"Wrong username or password", Toast.LENGTH_SHORT)
        val toastExists = Toast.makeText(context,"Welcome!", Toast.LENGTH_SHORT)




        lifecycleScope.launch{
            withContext(Dispatchers.IO){

                var user = db.DaoUser().getUser(attempedUser)
                if( user == null){

                    userExists = false

                }else if( user.username != attempedUser || user.password != attempedPassword){

                    userExists = false

                }

                withContext(Dispatchers.Main){

                    if(!userExists){

                        toastNotExists.show()

                    }else{

                        toastExists.show()
                        val action = loginFragmentDirections.actionLoginFragmentToGameContainerFragment2(user.username)
                        findNavController().navigate(action)
                    }

                }

            }
        }
    }

    private fun attempRegister(db:appDatabase){

        val attempedUser = binding.inputLogin.text.toString()
        val attempedPassword = binding.inputPassword.text.toString()

        val toastNotExists = Toast.makeText(context,"User created. Welcome!", Toast.LENGTH_SHORT)
        val toastExists = Toast.makeText(context,"The user already exists", Toast.LENGTH_SHORT)
        val toastInvalid = Toast.makeText(context, "Cannot have empty fields", Toast.LENGTH_SHORT)

        if(attempedUser == "" || attempedPassword == "") {
            toastInvalid.show()
        }else {
            lifecycleScope.launch {
                withContext(Dispatchers.IO) {

                    val user = db.DaoUser().getUser(attempedUser)
                    if (user != null) {
                        toastExists.show()

                    } else {

                        var newUser = User(attempedUser, attempedPassword)
                        toastNotExists.show()
                        db.DaoUser().insertUser(newUser)
                        withContext(Dispatchers.Main) {
                            val action =
                                loginFragmentDirections.actionLoginFragmentToGameContainerFragment2(
                                    newUser.username
                                )
                            findNavController().navigate(action)
                        }

                    }
                }
            }
        }
    }

    //TODO: añadir lo que elimina _binding


}