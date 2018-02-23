package app.sinanyilmaz.firebasearccomp;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;



public class DataRepository {
    private static  DataRepository sInstance;

    private String mUsername;
    private FirebaseDatabase mFirebaseDb;
    private DatabaseReference mMessageDbReference;


    private DataRepository(){
        mFirebaseDb = FirebaseDatabase.getInstance();
        mMessageDbReference = mFirebaseDb.getReference();

    }

    public static DataRepository getInstance() {
        if (sInstance == null) {
            synchronized (DataRepository.class) {
                if (sInstance == null) {
                    sInstance = new DataRepository();
                }
            }
        }
        return sInstance;
    }


    public FirebaseDatabase getFirebaseDbReference(){
        return mFirebaseDb;
    }

    public void setUserName(String userName){
        mUsername = userName;
    }

    public String getUserName(){
        return mUsername;
    }

}
