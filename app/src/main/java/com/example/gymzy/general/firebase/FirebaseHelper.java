package com.example.gymzy.general.firebase;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

public class FirebaseHelper {

    public static DatabaseReference getDatabase() {
        // REEMPLAZA ESTA URL con la que copiaste de tu consola de Firebase
        String url = "https://bdpruebasprog-default-rtdb.firebaseio.com/";
        return FirebaseDatabase.getInstance(url).getReference();
    }

    public static FirebaseFirestore getFirestore() {
        return FirebaseFirestore.getInstance();
    }

    public static FirebaseAuth getAuth() {
        return FirebaseAuth.getInstance();
    }
}