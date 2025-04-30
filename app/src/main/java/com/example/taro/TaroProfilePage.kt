package com.example.taro

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class TaroProfilePage : ComponentActivity() {

    private lateinit var pointsText: TextView
    private lateinit var completedTasksText: TextView
    private lateinit var profileImageView: ImageView
    private lateinit var changePhotoButton: Button
    private lateinit var groupsRecyclerView: RecyclerView
    private lateinit var groupAdapter: GroupAdapter
    private val groupList = mutableListOf<UserGroup>()


    private val PICK_IMAGE_REQUEST = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.taro_profile)

        groupsRecyclerView = findViewById(R.id.groupsRecyclerView)
        groupsRecyclerView.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this)
        groupAdapter = GroupAdapter(groupList)
        groupsRecyclerView.adapter = groupAdapter
        val bottomNavView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        BottomNav.setupBottomNav(bottomNavView, this)
        bottomNavView.selectedItemId = R.id.nav_profile

        pointsText = findViewById(R.id.pointsText)
        completedTasksText = findViewById(R.id.completedTasksText)
        profileImageView = findViewById(R.id.profileImageView)
        changePhotoButton = findViewById(R.id.changePhotoButton)

        loadProfileData()
        loadUserGroups()

        changePhotoButton.setOnClickListener {
            openImagePicker()
        }
        val createGroupButton = findViewById<Button>(R.id.createGroupButton)
        val joinGroupButton = findViewById<Button>(R.id.joinGroupButton)

        createGroupButton.setOnClickListener {
            val intent = Intent(this, TaroCreateGroupPage::class.java)
            startActivity(intent)
        }

        joinGroupButton.setOnClickListener {
            val intent = Intent(this, TaroJoinGroupPage::class.java)
            startActivity(intent)
        }

        val logoutButton = findViewById<Button>(R.id.logoutButton)
        logoutButton.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this, TaroLogin::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
        /*val generateDataButton = findViewById<Button>(R.id.generateDataButton)
        generateDataButton.setOnClickListener {
            val userId = FirebaseAuth.getInstance().currentUser?.uid
            if (userId != null) {
                DevDataGenerator.generateDummyTasks(this, userId, weeks = 4, tasksPerWeek = 5)
            }
        }*/


    }
    override fun onResume() {
        super.onResume()
        loadUserGroups()
    }


    private fun loadProfileData() {
        val db = FirebaseFirestore.getInstance()
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        val user = FirebaseAuth.getInstance().currentUser

        if (user != null) {
            user.photoUrl?.let { uri ->
                Glide.with(this)
                    .load(uri)
                    .into(profileImageView)
            }
        }

        if (userId != null) {
            db.collection("users")
                .document(userId)
                .get()
                .addOnSuccessListener { document ->
                    val totalPoints = document.getLong("totalPoints") ?: 0
                    pointsText.text = "Points: $totalPoints"
                }

            // Fetch tasks to count completed vs total
            db.collection("users")
                .document(userId)
                .collection("tasks")
                .get()
                .addOnSuccessListener { documents ->
                    var completedCount = 0
                    val totalCount = documents.size()

                    for (doc in documents) {
                        if (doc.getBoolean("completed") == true) {
                            completedCount++
                        }
                    }

                    completedTasksText.text = "Completed: $completedCount / $totalCount"
                }
        }
    }

    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            val imageUri = data.data
            uploadImageToFirebase(imageUri!!)
        }
    }

    private fun uploadImageToFirebase(imageUri: Uri) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        val storageRef = FirebaseStorage.getInstance().reference.child("profile_pictures/$userId.jpg")

        storageRef.putFile(imageUri)
            .addOnSuccessListener {
                storageRef.downloadUrl.addOnSuccessListener { uri ->
                    updateUserProfilePicture(uri.toString())
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to upload image", Toast.LENGTH_SHORT).show()
            }
    }

    private fun updateUserProfilePicture(photoUrl: String) {
        val user = FirebaseAuth.getInstance().currentUser
        val profileUpdates = UserProfileChangeRequest.Builder()
            .setPhotoUri(Uri.parse(photoUrl))
            .build()

        user?.updateProfile(profileUpdates)
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Profile picture updated!", Toast.LENGTH_SHORT).show()
                    Glide.with(this)
                        .load(user.photoUrl)
                        .into(profileImageView)
                }
            }
    }
    private fun loadUserGroups() {
        val db = FirebaseFirestore.getInstance()
        val userId = FirebaseAuth.getInstance().currentUser?.uid

        if (userId != null) {
            db.collection("groups")
                .whereArrayContains("members", userId)
                .get()
                .addOnSuccessListener { documents ->
                    groupList.clear()
                    for (document in documents) {
                        val group = UserGroup(
                            id = document.id,
                            name = document.getString("name") ?: "Unnamed Group",
                            code = document.getString("code") ?: "Unknown"
                        )
                        groupList.add(group)
                    }
                    groupAdapter.notifyDataSetChanged()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Failed to load groups: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

}
