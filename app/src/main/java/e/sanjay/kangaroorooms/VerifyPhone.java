package e.sanjay.kangaroorooms;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.TimeUnit;

public class VerifyPhone extends AppCompatActivity {

    String countryCodeMobileNumber;
    String phoneVerifyId;
    boolean verificationByCode,instantVerification;
    String resendToken;
    EditText mEditTextSixDigitCode;
    LinearLayout mLayout;
    PhoneAuthCredential userCredential;
    PhoneAuthProvider.ForceResendingToken resendingToken;
    String otp;
    FirebaseAuth fbAuth;
    private TextView mRegisteredPhoneNumber;
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_phone);

        fbAuth = FirebaseAuth.getInstance();

        Intent intent = getIntent();

        //Intent intent1 = getIntent();
        countryCodeMobileNumber = intent.getStringExtra("countryCodeMobNumber");
        phoneVerifyId = intent.getStringExtra("verificationIdSent");
        resendToken =  intent.getStringExtra("reSendToken");
        verificationByCode = true;
        instantVerification = false;

        mRegisteredPhoneNumber = (TextView) findViewById(R.id.registered_phone_number);

        mRegisteredPhoneNumber.setText("on " + intent.getStringExtra(LoginActivity.EXTRA_MESSAGE));

        mEditTextSixDigitCode = (EditText) findViewById(R.id.edit_text_6_digit_code);

        mEditTextSixDigitCode.setFilters(new InputFilter[]{new InputFilter.LengthFilter(6)});
        mEditTextSixDigitCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                mEditTextSixDigitCode.setPaddingRelative(0, 0, 0, 0);
                mEditTextSixDigitCode.setLetterSpacing(1);
                mEditTextSixDigitCode.setTextSize(20);
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (mEditTextSixDigitCode.length() == 0) {
                    mEditTextSixDigitCode.setPadding(10, 0, 0, 0);
                    mEditTextSixDigitCode.setLetterSpacing(0);
                    mEditTextSixDigitCode.setTextSize(12);
                }
            }
        });

        mEditTextSixDigitCode.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    mEditTextSixDigitCode.setBackground(getResources().getDrawable(R.drawable.linear_activity_in_activity_first_run_second_background_on_focus));
                } else {
                    mEditTextSixDigitCode.setBackground(getResources().getDrawable(R.drawable.enter_code_background));
                }
            }
        });

        mLayout = (LinearLayout) findViewById(R.id.mLayout);

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                countryCodeMobileNumber,
                60,
                TimeUnit.SECONDS,
                VerifyPhone.this,
                CallBacks);

    }

    PhoneAuthProvider.OnVerificationStateChangedCallbacks CallBacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {

            Toast.makeText(VerifyPhone.this, "Google play verified your phone number automatically", Toast.LENGTH_SHORT).show();
            mEditTextSixDigitCode.setText(phoneAuthCredential.getSmsCode());
            userCredential = phoneAuthCredential;
            verificationByCode = false;
            instantVerification = true;

        }

        @Override
        public void onVerificationFailed(FirebaseException e) {

            Toast.makeText(getApplicationContext(), "Request Failed!.Please Try after some time.", Toast.LENGTH_SHORT).show();

        }

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {

            Toast.makeText(getApplicationContext(), "Code has been sent to your mobile number", Toast.LENGTH_SHORT).show();

            phoneVerifyId = s;

            resendingToken = forceResendingToken;

            super.onCodeSent(s, forceResendingToken);
        }

    };


    public void loginButton(View view){

        otp = mEditTextSixDigitCode.getText().toString();

        if (otp.length() != 6) {

            Snackbar snackbar = Snackbar.make(mLayout, "Please enter valid OTP", Snackbar.LENGTH_SHORT);
            View snackBarView = snackbar.getView();
            snackBarView.setBackgroundColor(getResources().getColor(R.color.errorSnackBarColor));
            TextView mSnackBarTextView = (TextView) snackBarView.findViewById(android.support.design.R.id.snackbar_text);
            mSnackBarTextView.setTextAlignment(snackBarView.TEXT_ALIGNMENT_CENTER);
            snackbar.show();
        }
        else {

            pd = new ProgressDialog(VerifyPhone.this);
            pd.setMessage("Logging in...");
            pd.show();

            if (verificationByCode) {

                verifyCode(otp);
            }
            else if (instantVerification){

                verifyCode(otp);
            }

        }

    }

    private void verifyCode(String code){

        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(phoneVerifyId, code);

        signInWithPhoneAuthCredential(credential);

    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {

        fbAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()) {

                    getUserUid();

                }
                else {

                    pd.dismiss();
                    Toast.makeText(VerifyPhone.this, "Invalid verification code.Please try again", Toast.LENGTH_SHORT).show();

                }

            }
        });
    }


    public void getUserUid(){

        DatabaseReference users = FirebaseDatabase.getInstance().getReference("Users");

        users.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot : dataSnapshot.getChildren()){

                    DataSnapshot phoneSnap = snapshot.child("PhoneNumber");

                    for (DataSnapshot snapshot1 : phoneSnap.getChildren()){

                        if (countryCodeMobileNumber.equals(snapshot1.getValue(String.class))){

                            LoginActivity.userUid = snapshot.getKey();

                            DataSnapshot nameSnap = snapshot.child("Name");

                            for (DataSnapshot dataSnapshot1 : nameSnap.getChildren()){

                                LoginActivity.userName = dataSnapshot1.getValue(String.class);

                            }

                            break;

                        }

                    }

                }

                pd.dismiss();

                if (LoginActivity.userUid != null){

                    getSharedPreferences("User",MODE_PRIVATE)
                            .edit()
                            .putString("UserUid",LoginActivity.userUid)
                            .apply();

                    getSharedPreferences("UserName",MODE_PRIVATE)
                            .edit()
                            .putString("Name",LoginActivity.userName)
                            .apply();

                    getSharedPreferences("PhoneNumber",MODE_PRIVATE)
                            .edit()
                            .putString("Phone",countryCodeMobileNumber)
                            .apply();

                    goToFirstActivity();

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void goToFirstActivity(){

        Intent intent = new Intent(VerifyPhone.this, FirstActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();

    }


    public void resendCode(View view){

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                countryCodeMobileNumber,
                60,
                TimeUnit.SECONDS,
                this,
                CallBacks,
                resendingToken
        );

    }

}
