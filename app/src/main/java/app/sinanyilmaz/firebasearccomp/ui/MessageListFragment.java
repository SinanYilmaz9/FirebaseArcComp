package app.sinanyilmaz.firebasearccomp.ui;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;

import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
import java.util.List;

import app.sinanyilmaz.firebasearccomp.DataRepository;
import app.sinanyilmaz.firebasearccomp.R;
import app.sinanyilmaz.firebasearccomp.data.Entity;
import app.sinanyilmaz.firebasearccomp.databinding.MessageListFragmentBinding;
import app.sinanyilmaz.firebasearccomp.viewmodel.MessagesListViewModel;

import static android.app.Activity.RESULT_CANCELED;



public class MessageListFragment extends Fragment {

    private static final String TAG = "MessageListFragment";
    private static final String ANONYMOUS = "anonymous";
    private static final int RC_SIGN_IN = 1;

    private MessagesListViewModel mModel ;
    private MessageListFragmentBinding mBinding;
    private MyFragmentListenerImpl mFragmentCallback;
    private MessageAdapter mMessageAdapter = new MessageAdapter();
    private Toast toast;

    private FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    interface MyFragmentListenerImpl {
        void onFabButtonClicked();
    }


    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        mModel = ViewModelProviders.of(getActivity()).get(MessagesListViewModel.class);
        FirebaseApp.initializeApp(getActivity());
        mAuthStateListener = firebaseAuth -> {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            if(user != null){
                DataRepository.getInstance().setUserName(user.getDisplayName());
            } else {
                Log.d(TAG, "user is null");
                DataRepository.getInstance().setUserName(ANONYMOUS);
                // Choose authentication providers
                List<AuthUI.IdpConfig> providers = Arrays.asList(
                        new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build(),
                        new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build() );
                // Create and launch sign-in intent
                startActivityForResult(
                        AuthUI.getInstance()
                                .createSignInIntentBuilder()
                                .setIsSmartLockEnabled(false)
                                .setAvailableProviders(providers)
                                .setLogo(R.mipmap.ic_launcher)
                                .build(),
                        RC_SIGN_IN);
            }
        };
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState){

        mBinding = DataBindingUtil.inflate(inflater, R.layout.message_list_fragment,
               container, false );


        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mBinding.recyclerview.setLayoutManager(layoutManager);


        mBinding.fab.setOnClickListener(v -> {
            mFragmentCallback.onFabButtonClicked();
        });

        return mBinding.getRoot();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.d(TAG, "on attach");

        try {
            mFragmentCallback = (MyFragmentListenerImpl) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState ){
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "onActivityCreated");

        mBinding.recyclerview.setAdapter(mMessageAdapter);

        // Update the list when the data changes
        if(mModel != null){
            LiveData<List<Entity>> liveData = mModel.getMessageListLiveData();

            liveData.observe(getActivity(), (List<Entity> mEntities) -> {
                mMessageAdapter.setMessageList(mEntities);
            });
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "result code = " + String.valueOf(resultCode));

        if(resultCode == RESULT_CANCELED){
            return;
        }
        switch(requestCode){
            case RC_SIGN_IN:
                toast.makeText(getActivity(),"Signed in",Toast.LENGTH_SHORT).show();

                break;

            default: Log.w(TAG, "switch(requestCode), case not implemented.");
        }

    }

    @Override
    public void onResume(){
        super.onResume();
        Log.d(TAG, "onResume");
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    public void onPause(){
        Log.d(TAG, "onPause");
        super.onPause();
        if(mAuthStateListener != null){
            mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
        }

    }



}
