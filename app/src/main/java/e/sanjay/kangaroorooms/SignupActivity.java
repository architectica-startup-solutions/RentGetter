package e.sanjay.kangaroorooms;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.LightingColorFilter;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Html;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class SignupActivity extends AppCompatActivity {

    private static final int CUSTOM_COLOR = 0xFFFF0000;
    private Toolbar toolbar;
    ProgressDialog progressDialog;
    private TextView mRegisteredPhoneNumber;
    private TextView mTimer;
    private TextView mResendCode;
    private TextView mTextViewAgreePolicy;
    private TextView mTextViewReferralCode;
    private EditText mEditTextEmail;
    private EditText mEditTextName;
    private EditText mEditTextSixDigitCode;
    private EditText mEditTextReferralCode;
    LinearLayout mLayout;
    public String mobileNumber;
    public String countryCodeMobileNumber;
    protected String phoneVerifyId;
    private String resendToken;
    private FirebaseAuth fbAuth;
    private FirebaseDatabase mFireBaseDataBase;
    private DatabaseReference mDataBaseReference,newUser;
    TextView msg;
    String otp;
    PhoneAuthProvider.ForceResendingToken resendingToken;
    String email;
    String name;
    String phn;
    boolean isPhoneNumberVerified,verificationByCode,instantVerification;
    boolean correctEmail;
    PhoneAuthCredential userCredential;
    boolean isEmailUsed;
    boolean isPhoneUsed;
    DatabaseReference reference;


    Boolean isDeny;
    Boolean isFirstRunThird;
    Boolean isAllow;

    public static final String EXTRA_MESSAGE =
            "com.aryan.android.oyo.extra.MESSAGE";


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
        setContentView(R.layout.activity_signup);

        toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        Intent intent = getIntent();

        //Intent intent1 = getIntent();
        mobileNumber = intent.getStringExtra("number");
        countryCodeMobileNumber = intent.getStringExtra("countryCodeMobNumber");
        phoneVerifyId = intent.getStringExtra("verificationIdSent");
        resendToken =  intent.getStringExtra("reSendToken");
        isPhoneNumberVerified = false;
        verificationByCode = true;
        instantVerification = false;


        mRegisteredPhoneNumber = (TextView) findViewById(R.id.registered_phone_number);
        mRegisteredPhoneNumber.setText("on " + intent.getStringExtra(LoginActivity.EXTRA_MESSAGE));
        mTimer = (TextView) findViewById(R.id.timer);
        mResendCode = (TextView) findViewById(R.id.resend_code);
        mTextViewAgreePolicy = (TextView) findViewById(R.id.text_view_agree_policy);
        msg = (TextView) findViewById(R.id.msg1);
        fbAuth = FirebaseAuth.getInstance();
        mFireBaseDataBase = FirebaseDatabase.getInstance();
        mDataBaseReference = mFireBaseDataBase.getReference("Users");

        mEditTextEmail = (EditText) findViewById(R.id.edit_text_email);
        mEditTextName = (EditText) findViewById(R.id.edit_text_name);
        mEditTextSixDigitCode = (EditText) findViewById(R.id.edit_text_6_digit_code);
        //mEditTextReferralCode = (EditText) findViewById(R.id.edit_text_referral_code);

        mLayout = (LinearLayout) findViewById(R.id.first_run_third_activity);

        mTextViewAgreePolicy.setText(Html.fromHtml(getResources().getString(R.string.agree)));

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


        new CountDownTimer(120000, 1000) {

            public void onTick(long millisUntilFinished) {
                String text = String.format(Locale.getDefault(), "Waiting for OTP... %d:%02ds ",
                        TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) % 60,
                        TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) % 60);
                mTimer.setText(text);
                // mTimer.setText("Waiting for OTP... " + millisUntilFinished / 1000+"s");
                mResendCode.setClickable(false);
            }

            public void onFinish() {
                mResendCode.setTextColor(getResources().getColor(R.color.endColor));
                mResendCode.setClickable(true);
            }
        }.start();

        mEditTextEmail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    mEditTextEmail.setBackground(getResources().getDrawable(R.drawable.linear_activity_in_activity_first_run_second_background_on_focus));
                } else {
                    mEditTextEmail.setBackground(getResources().getDrawable(R.drawable.linear_layout_in_activity_first_run_second_background));
                }
            }
        });

        mEditTextName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    mEditTextName.setBackground(getResources().getDrawable(R.drawable.linear_activity_in_activity_first_run_second_background_on_focus));
                } else {
                    mEditTextName.setBackground(getResources().getDrawable(R.drawable.linear_layout_in_activity_first_run_second_background));
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

        mEditTextReferralCode.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    mEditTextReferralCode.setBackground(getResources().getDrawable(R.drawable.linear_activity_in_activity_first_run_second_background_on_focus));
                } else {
                    mEditTextReferralCode.setBackground(getResources().getDrawable(R.drawable.linear_layout_in_activity_first_run_second_background));
                }
            }
        });

        isFirstRunThird = getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                .getBoolean("isFirstRunThird", true);

        isDeny = getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                .getBoolean("isDeny", false);

        isAllow = getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                .getBoolean("isAllow", false);

        if (isFirstRunThird || (!isDeny && !isAllow)) {
            //show start activity
            AlertDialog.Builder myAlertBuilder = new AlertDialog.Builder(this);
            myAlertBuilder.setIcon(R.drawable.ic_textsms_black_24dp);
            myAlertBuilder.setTitle(getResources().getText(R.string.dent_allow_sms));
            myAlertBuilder.setPositiveButton("ALLOW", new
                    DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit()
                                    .putBoolean("isAllow", true).commit();

                            mTimer.setVisibility(mTimer.GONE);
                            mResendCode.setVisibility(mResendCode.GONE);

                        }
                    });
            myAlertBuilder.setNegativeButton("DENY", new
                    DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit()
                                    .putBoolean("isDeny", true).commit();
                        }
                    });
            AlertDialog alertDialog = myAlertBuilder.create();
            alertDialog.show();
            Button positiveButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
            positiveButton.setTextColor(getResources().getColor(R.color.allow_deny_alert));
            Button negativeButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
            negativeButton.setTextColor(getResources().getColor(R.color.allow_deny_alert));

        }



        if (isDeny) {

            LayoutInflater layoutInflater = LayoutInflater.from(this);
            View promptView = layoutInflater.inflate(R.layout.deny_alert_layout, null);

            final AlertDialog alertD = new AlertDialog.Builder(this).create();

            TextView userInput = (TextView) promptView.findViewById(R.id.dent_txt_sms);

            Button btnAdd1 = (Button) promptView.findViewById(R.id.deny_btn);

            btnAdd1.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {

                    // btnAdd1 has been clicked
                    getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit()
                            .putBoolean("isAllow", true).commit();

                    getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit()
                            .putBoolean("isDeny", false).commit();

                    alertD.cancel();

                    mTimer.setVisibility(mTimer.GONE);
                    mResendCode.setVisibility(mResendCode.GONE);
                }
            });

            alertD.setView(promptView);

            alertD.show();

            alertD.getWindow().getDecorView().getBackground().setColorFilter(new LightingColorFilter(0xFF0000FF, CUSTOM_COLOR));
        }


        if (isAllow) {
            mTimer.setVisibility(mTimer.GONE);
            mResendCode.setVisibility(mResendCode.GONE);
        }

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                countryCodeMobileNumber,
                60,
                TimeUnit.SECONDS,
                SignupActivity.this,
                CallBacks);

    }

    public void editPhoneNumber(View view) {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    public void newAccount (View view) {

        InputMethodManager mImMan = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        mImMan.hideSoftInputFromWindow(mEditTextEmail.getWindowToken(), 0);

        mImMan.hideSoftInputFromWindow(mEditTextName.getWindowToken(), 0);

        mImMan.hideSoftInputFromWindow(mEditTextReferralCode.getWindowToken(), 0);

        mImMan.hideSoftInputFromWindow(mEditTextSixDigitCode.getWindowToken(), 0);

        email = mEditTextEmail.getText().toString();
        name = mEditTextName.getText().toString();
        otp = mEditTextSixDigitCode.getText().toString();
        phn = mRegisteredPhoneNumber.getText().toString();

        correctEmail = !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches();

        if (!correctEmail) {
            mEditTextEmail.setBackground(getResources().getDrawable(R.drawable.error_phone_number));
            Snackbar snackbar = Snackbar.make(mLayout, "Please provide a valid email", Snackbar.LENGTH_SHORT);
            View snackBarView = snackbar.getView();
            snackBarView.setBackgroundColor(getResources().getColor(R.color.errorSnackBarColor));
            TextView mSnackBarTextView = (TextView) snackBarView.findViewById(android.support.design.R.id.snackbar_text);
            mSnackBarTextView.setTextAlignment(snackBarView.TEXT_ALIGNMENT_CENTER);
            snackbar.show();
        } else if (name.length() == 0) {
            mEditTextName.setBackground(getResources().getDrawable(R.drawable.error_phone_number));
            Snackbar snackbar = Snackbar.make(mLayout, "Please provide your name", Snackbar.LENGTH_SHORT);
            View snackBarView = snackbar.getView();
            snackBarView.setBackgroundColor(getResources().getColor(R.color.errorSnackBarColor));
            TextView mSnackBarTextView = (TextView) snackBarView.findViewById(android.support.design.R.id.snackbar_text);
            mSnackBarTextView.setTextAlignment(snackBarView.TEXT_ALIGNMENT_CENTER);
            snackbar.show();
        } else if (otp.length() != 6) {
            mEditTextName.setBackground(getResources().getDrawable(R.drawable.error_phone_number));
            Snackbar snackbar = Snackbar.make(mLayout, "Please enter valid OTP", Snackbar.LENGTH_SHORT);
            View snackBarView = snackbar.getView();
            snackBarView.setBackgroundColor(getResources().getColor(R.color.errorSnackBarColor));
            TextView mSnackBarTextView = (TextView) snackBarView.findViewById(android.support.design.R.id.snackbar_text);
            mSnackBarTextView.setTextAlignment(snackBarView.TEXT_ALIGNMENT_CENTER);
            snackbar.show();
        }
        else{

            getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit()
                    .putBoolean("isFirstRun", false).commit();

            getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit()
                    .putString("name", name).commit();

            getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit()
                    .putString("email", email).commit();

            getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit()
                    .putString("phn", phn).commit();

            progressDialog = new ProgressDialog(SignupActivity.this);
            progressDialog.setMessage("Creating account...");
            progressDialog.show();

            checkEmail();

        }

    }

    PhoneAuthProvider.OnVerificationStateChangedCallbacks CallBacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {

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


    private void verifyCode(String code){

        isPhoneNumberVerified = false;

        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(phoneVerifyId, code);
        if (credential != null){
            isPhoneNumberVerified = true;
        }
        signInWithPhoneAuthCredential(credential);

    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {

        fbAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()) {

                    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                    newUser = mDataBaseReference.child(currentUser.getUid());

                    progressDialog.dismiss();

                    newUser.child("Name").setValue(name);
                    newUser.child("Email").setValue(email);
                    newUser.child("PhoneNumber").setValue(countryCodeMobileNumber);

                    LoginActivity.userUid = FirebaseAuth.getInstance().getUid();
                    LoginActivity.userName = name;
                    LoginActivity.phone = countryCodeMobileNumber;

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

                    Toast.makeText(SignupActivity.this, "sign up successful", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(SignupActivity.this, FirstActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);

                }
                else {

                    progressDialog.dismiss();
                    Toast.makeText(SignupActivity.this, "Invalid verification code.Please try again", Toast.LENGTH_SHORT).show();

                }

            }
        });
    }

    public void checkEmail() {

        reference = FirebaseDatabase.getInstance().getReference("Users/");

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                isEmailUsed = false;

                for (DataSnapshot ds : dataSnapshot.getChildren()) {


                    String data = ds.getValue().toString();

                    Log.i("data", data);

                    DataSnapshot dataSnap = ds.child("Email");

                    for (DataSnapshot dsp : dataSnap.getChildren()) {

                        String Email = dsp.getValue().toString();

                        if (Email.equals(email)) {   //but returning false

                            isEmailUsed = true;
                            break;

                        }
                    }

                }

                if (isEmailUsed){

                    progressDialog.dismiss();
                    Toast.makeText(SignupActivity.this, "This email id is already linked with another account.Please use another Email Id.", Toast.LENGTH_SHORT).show();

                }
                else {

                    DatabaseReference customerCheck = FirebaseDatabase.getInstance().getReference("Vendors");

                    customerCheck.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            boolean isUserVendor = false;

                            for (DataSnapshot snapshot : dataSnapshot.getChildren()){

                                if (countryCodeMobileNumber.equals(snapshot.child("PhoneNumber").getValue(String.class))){

                                    isUserVendor = true;
                                    break;

                                }

                            }

                            if (isUserVendor){

                                progressDialog.dismiss();
                                Toast.makeText(SignupActivity.this, "This mobile number is already registered as a vendor.", Toast.LENGTH_SHORT).show();

                            }
                            else {

                                if (verificationByCode) {

                                    verifyCode(otp);
                                }
                                else if (instantVerification){

                                    verifyCode(otp);
                                }

                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                Toast.makeText(SignupActivity.this, "database error", Toast.LENGTH_SHORT).show();

            }
        });

    }

    public void resendCode(View view) {

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
