package e.sanjay.kangaroorooms;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class LoginActivity extends AppCompatActivity {

    private Toolbar toolbar;
    List<String> code;
    ProgressDialog pd;
    public static TextView mCountryCode;
    public EditText mPhoneNumber;
    public static ImageView mFlagImage;
    LinearLayout mUserInput;
    RelativeLayout mLayout;
    public String PhoneNumber;
    String message;
    String fullNumber;
    String registeredNumber;
    boolean isUserRegistered;
    protected String phoneVerificationId;
    private PhoneAuthProvider.ForceResendingToken resendingToken;
    DatabaseReference mDataBaseReference;
    public static final String EXTRA_MESSAGE =
            "com.aryan.android.oyo.extra.MESSAGE";
    int ref;
    DatabaseReference reference;
    boolean isPhoneNumberVerified,verificationByCode,instantVerification;
    static String userName;
    static String userUid;
    static String phone;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        mCountryCode = (TextView) findViewById(R.id.country_code);
        mFlagImage = (ImageView) findViewById(R.id.flag_img);
        mUserInput = (LinearLayout)findViewById(R.id.user_input_in_frsa) ;
        mPhoneNumber = (EditText) findViewById(R.id.editText_phone) ;
        registeredNumber = null;
        mDataBaseReference = FirebaseDatabase.getInstance().getReference("Users");
        reference = FirebaseDatabase.getInstance().getReference("Users/");

        isPhoneNumberVerified = false;
        verificationByCode = true;
        instantVerification = false;

    }

    public void changeBackground(View view) {
        mUserInput.setBackgroundResource(R.drawable.linear_activity_in_activity_first_run_second_background_on_focus);
    }


    public void verify(View view) {

        InputMethodManager mImMan = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        mImMan.hideSoftInputFromWindow(mPhoneNumber.getWindowToken(), 0);

        PhoneNumber = mPhoneNumber.getText().toString();

        message = mCountryCode.getText() + " " + mPhoneNumber.getText();
        fullNumber = mCountryCode.getText().toString() + mPhoneNumber.getText().toString();

        int i=0;


        if(PhoneNumber.length() == 0){
            mUserInput.setBackground(getResources().getDrawable(R.drawable.error_phone_number));
            Snackbar snackbar= Snackbar.make(mLayout,"Enter phone number",Snackbar.LENGTH_SHORT);
            View snackBarView = snackbar.getView();
            snackBarView.setBackgroundColor(getResources().getColor(R.color.errorSnackBarColor));
            TextView mSnackBarTextView = (TextView) snackBarView.findViewById(android.support.design.R.id.snackbar_text);
            mSnackBarTextView.setTextAlignment(snackBarView.TEXT_ALIGNMENT_CENTER);
            snackbar.show();
        }
        else if(PhoneNumber.length() != 10) {
            mUserInput.setBackground(getResources().getDrawable(R.drawable.error_phone_number));
            Snackbar snackbar= Snackbar.make(mLayout,"Enter valid Phone Number",Snackbar.LENGTH_SHORT);
            View snackBarView = snackbar.getView();
            snackBarView.setBackgroundColor(getResources().getColor(R.color.errorSnackBarColor));
            TextView mSnackBarTextView = (TextView) snackBarView.findViewById(android.support.design.R.id.snackbar_text);
            mSnackBarTextView.setTextAlignment(snackBarView.TEXT_ALIGNMENT_CENTER);
            snackbar.show();
        }
        else {

            SignIn();
        }

    }
    public void SignIn() {

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                ref = 0;
                isUserRegistered = false;

                for (DataSnapshot ds : dataSnapshot.getChildren()) {

                    String phoneNum = ds.child("PhoneNumber").getValue(String.class);

                    if (fullNumber.equals(phoneNum)) {

                        isUserRegistered = true;
                        break;

                    }

                }
                if(isUserRegistered){

                    Toast.makeText(getApplicationContext(), "user is already registered", Toast.LENGTH_SHORT).show();

                } else {

                    Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.putExtra(EXTRA_MESSAGE, message);
                    intent.putExtra("number",PhoneNumber);
                    intent.putExtra("countryCodeMobNumber",fullNumber);
                    intent.putExtra("verificationIdSent",phoneVerificationId);
                    intent.putExtra("reSendToken",resendingToken);
                    startActivity(intent);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                Toast.makeText(LoginActivity.this, "database error", Toast.LENGTH_SHORT).show();

            }
        });

    }

    public void loginButton(View v){

        InputMethodManager mImMan = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        mImMan.hideSoftInputFromWindow(mPhoneNumber.getWindowToken(), 0);

        PhoneNumber = mPhoneNumber.getText().toString();

        message = mCountryCode.getText() + " " + mPhoneNumber.getText();
        fullNumber = mCountryCode.getText().toString() + mPhoneNumber.getText().toString();
        int i=0;

        if(PhoneNumber.length() == 0){
            mUserInput.setBackground(getResources().getDrawable(R.drawable.error_phone_number));
            Snackbar snackbar= Snackbar.make(mLayout,"Enter phone number",Snackbar.LENGTH_SHORT);
            View snackBarView = snackbar.getView();
            snackBarView.setBackgroundColor(getResources().getColor(R.color.errorSnackBarColor));
            TextView mSnackBarTextView = (TextView) snackBarView.findViewById(android.support.design.R.id.snackbar_text);
            mSnackBarTextView.setTextAlignment(snackBarView.TEXT_ALIGNMENT_CENTER);
            snackbar.show();
        }
        else if(PhoneNumber.length() != 10) {
            mUserInput.setBackground(getResources().getDrawable(R.drawable.error_phone_number));
            Snackbar snackbar= Snackbar.make(mLayout,"Enter valid Phone Number",Snackbar.LENGTH_SHORT);
            View snackBarView = snackbar.getView();
            snackBarView.setBackgroundColor(getResources().getColor(R.color.errorSnackBarColor));
            TextView mSnackBarTextView = (TextView) snackBarView.findViewById(android.support.design.R.id.snackbar_text);
            mSnackBarTextView.setTextAlignment(snackBarView.TEXT_ALIGNMENT_CENTER);
            snackbar.show();
        }
        else{

            pd = new ProgressDialog(LoginActivity.this);
            pd.setMessage("Logging in...");
            pd.show();
            LogIn();
        }

    }

    public void LogIn() {

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                ref = 0;
                isUserRegistered = false;

                for (DataSnapshot ds : dataSnapshot.getChildren()) {

                    String data = ds.getValue().toString();

                    Log.i("data", data);

                    String phoneNum = ds.child("PhoneNumber").getValue(String.class);

                    if (fullNumber.equals(phoneNum)) {   //but returning false

                        isUserRegistered = true;
                        userName = ds.child("Name").getValue(String.class);
                        break;

                    }

                }

                if (isUserRegistered){

                    Intent intent = new Intent(getApplicationContext(), VerifyPhone.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.putExtra(EXTRA_MESSAGE, message);
                    intent.putExtra("number",PhoneNumber);
                    intent.putExtra("countryCodeMobNumber",fullNumber);
                    intent.putExtra("verificationIdSent",phoneVerificationId);
                    intent.putExtra("reSendToken",resendingToken);
                    startActivity(intent);

                }
                else {

                    pd.dismiss();

                    Toast.makeText(getApplicationContext(), "User not registered", Toast.LENGTH_SHORT).show();
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                pd.dismiss();

                Toast.makeText(LoginActivity.this, "database error", Toast.LENGTH_SHORT).show();

            }
        });

    }

}
