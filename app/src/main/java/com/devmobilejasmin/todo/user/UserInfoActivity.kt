package com.devmobilejasmin.todo.user

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.activity.viewModels
import androidx.core.net.toUri
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import coil.load
import coil.transform.CircleCropTransformation
import com.devmobilejasmin.todo.R
import com.devmobilejasmin.todo.databinding.ActivityUserInfoBinding
import com.devmobilejasmin.todo.form.FormActivity
import com.devmobilejasmin.todo.network.Api
import com.devmobilejasmin.todo.network.TaskListViewModel
import com.devmobilejasmin.todo.taskList.Task
import com.google.android.material.snackbar.Snackbar
import com.google.modernstorage.mediastore.FileType
import com.google.modernstorage.mediastore.MediaStoreRepository
import com.google.modernstorage.mediastore.SharedPrimary
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.util.*
import android.app.Activity
import com.devmobilejasmin.todo.taskList.TaskListFragment


class UserInfoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityUserInfoBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)



        binding.takePictureButton.setOnClickListener {
            launchCameraWithPermission()
        }

        binding.uploadImageButton.setOnClickListener{
            galleryLauncher.launch("image/*")
        }

        binding.modifyUserInformation.setOnClickListener {
            val intent = Intent(this, UserFormActivity::class.java)
            intent.putExtra("userinfo", viewModel.userInfo.value)
            formLauncher.launch(intent)
        }

        binding.UserModificationValidationButton.setOnClickListener {
            intent.putExtra("userInfo", viewModel.userInfo.value)
            setResult(RESULT_OK, intent)
            finish()
        }


        lifecycleScope.launchWhenStarted {
            photoUri = mediaStore.createMediaUri(
                filename = "picture-${UUID.randomUUID()}.jpg",
                type = FileType.IMAGE,
                location = SharedPrimary
            ).getOrThrow()
        }


        lifecycleScope.launch { // on lance une coroutine car `collect` est `suspend`
            viewModel.userInfo.collect { newUserInfo ->

                // cette lambda est executée à chaque fois que la liste est mise à jour dans le repository
                // on met à jour la liste dans l'adapteur

                binding.currentImage.load(newUserInfo?.avatar) {
                    // affiche une image par défaut en cas d'erreur:
                    error(R.drawable.ic_launcher_background)
                    transformations(CircleCropTransformation())
                }

                binding.userFirstName.text = viewModel.userInfo.value?.firstName
                binding.userLastName.text = viewModel.userInfo.value?.lastName
                binding.userEmailAdress.text = viewModel.userInfo.value?.email
            }
        }


        lifecycleScope.launch {
            viewModel.actualise()
        }
    }



    private val viewModel: UserInfoViewModel by viewModels()

    private lateinit var binding: ActivityUserInfoBinding
    private lateinit var parentActivity: TaskListFragment

    val mediaStore by lazy { MediaStoreRepository(this) }

    private lateinit var photoUri: Uri

    val formLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val newUserInfo = result.data?.getSerializableExtra("userinfo") as UserInfo

        viewModel.updateTextInfo(newUserInfo)
    }

    private val cameraPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { accepted ->
            if (accepted) launchCamera()
                // lancer l'action souhaitée
            else showExplanation()
                // afficher une explication
        }

    private val cameraLauncher =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { accepted ->
            val view = binding.root
                // n'importe quelle vue (ex: un bouton, binding.root, window.decorView, ...)
                if (accepted) handleImage(photoUri)
                else Snackbar.make(view, "Échec!", Snackbar.LENGTH_LONG).show()
        }

    private fun launchCameraWithPermission() {
        val camPermission = Manifest.permission.CAMERA
        val permissionStatus = checkSelfPermission(camPermission)
        val isAlreadyAccepted = permissionStatus == PackageManager.PERMISSION_GRANTED
        val isExplanationNeeded = shouldShowRequestPermissionRationale(camPermission)
        when {
            isAlreadyAccepted -> launchCamera()
                // lancer l'action souhaitée
            isExplanationNeeded -> showExplanation()
                // afficher une explication
            else -> cameraPermissionLauncher.launch(camPermission)
                // lancer la demande de permission
        }
    }

    private fun showExplanation() {
        // ici on construit une pop-up système (Dialog) pour expliquer la nécessité de la demande de permission
        AlertDialog.Builder(this)
            .setMessage("🥺 On a besoin de la caméra, vraiment! 👉👈")
            .setPositiveButton("Bon, ok") { _, _ ->  }
            .setNegativeButton("Nope") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    private fun launchAppSettings() {
        // Cet intent permet d'ouvrir les paramètres de l'app (pour modifier les permissions déjà refusées par ex)
        val intent = Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.fromParts("package", this.packageName, null)
        )
        // ici pas besoin de vérifier avant car on vise un écran système:
        startActivity(intent)
    }

    private fun handleImage(imageUri: Uri) {
        // afficher l'image dans l'ImageView
        viewModel.changeImage(convert(imageUri))
    }

    private fun launchCamera() {
        cameraLauncher.launch(photoUri)
    }

    private fun convert(uri: Uri): MultipartBody.Part {
        return MultipartBody.Part.createFormData(
            name = "avatar",
            filename = "temp.jpeg",
            body = contentResolver.openInputStream(uri)!!.readBytes().toRequestBody()
        )
    }

    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { imageUri ->
        if (imageUri != null){
            viewModel.changeImage(convert(imageUri))
        }
    }

}