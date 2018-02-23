package app.sinanyilmaz.firebasearccomp.viewmodel;

import android.arch.core.util.Function;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;


import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;

import app.sinanyilmaz.firebasearccomp.data.FirebaseQueryLiveData;
import app.sinanyilmaz.firebasearccomp.data.Entity;



public class MessagesListViewModel extends ViewModel {
    private static String TAG = "ListViewModel";

    private static final DatabaseReference dataRef =
            FirebaseDatabase.getInstance().getReference().child("messages");

    private List<Entity> mList = new ArrayList<>();

    @NonNull
    public LiveData<List<Entity>> getMessageListLiveData(){
        FirebaseQueryLiveData mLiveData = new FirebaseQueryLiveData(dataRef);

        LiveData<List<Entity>> mMessageLiveData =
                Transformations.map(mLiveData, new Deserializer());

        return mMessageLiveData;
    }

    private class Deserializer implements Function<DataSnapshot, List<Entity>>{

        @Override
        public List<Entity> apply(DataSnapshot dataSnapshot) {
            mList.clear();
            for(DataSnapshot snap : dataSnapshot.getChildren()){
                Entity msg = snap.getValue(Entity.class);
                mList.add(msg);
            }
            return mList;
        }
    }


    private String mPhoto = "";

    private final MutableLiveData<Boolean> pictureUploadIsSuccessful = new MutableLiveData<>();
    private final MutableLiveData<Boolean> messageUploadIsSuccessful = new MutableLiveData<>();




    public void setPhotoUrl(String photoUrl){
        mPhoto = photoUrl;
    }

    public String getPhotoUrl(){
        return mPhoto;
    }

    //NewPostFragment
    public MutableLiveData<Boolean> getPictureUploadIsSuccessful(){
        return pictureUploadIsSuccessful;
    }

    public MutableLiveData<Boolean> getMessageUploadIsSuccessful(){
        return messageUploadIsSuccessful;
    }



    public void createAndSendToDataBase(String userName, String descriptionText,String mPhoto){
        Entity entity = new Entity(descriptionText,userName,mPhoto);

        // push the new message to Firebase
        Task uploadTask = FirebaseDatabase.getInstance()
                .getReference()
                .child("messages")
                .push()
                .setValue(entity);
        uploadTask.addOnSuccessListener(o -> messageUploadIsSuccessful.setValue(true));
    }

    public void uploadPicture(Intent intentData){
        Uri selectedUri = intentData.getData();
        StorageReference photoRef = FirebaseStorage.getInstance()
                .getReference().child("photos")
                .child(selectedUri.getLastPathSegment());

        UploadTask uploadTask = photoRef.putFile(selectedUri);
        uploadTask.addOnSuccessListener(taskSnapshot -> {
            mPhoto = String.valueOf(taskSnapshot.getDownloadUrl());
            pictureUploadIsSuccessful.setValue(true);
        });
        uploadTask.addOnFailureListener(e -> pictureUploadIsSuccessful.setValue(false));
    }


}


