package br.com.hugo.victor.fingerprintexample;

import android.Manifest;
import android.app.KeyguardManager;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import static android.content.Context.FINGERPRINT_SERVICE;
import static android.content.Context.KEYGUARD_SERVICE;

public class MyCustomDialog extends DialogFragment {

    private static final String TAG = "MyCustomDialog";

    private TextView mActionCancel;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_fingerprint, container, false);

        mActionCancel = view.findViewById(R.id.cancelAction);
        mActionCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: closing dialog");
                getDialog().dismiss();
            }
        });

        VerifyFingerprint();

        return view;
    }

    public void VerifyFingerprint() {
        // Initializing both Android Keyguard Manager and Fingerprint Manager
        KeyguardManager keyguardManager = (KeyguardManager) getContext().getSystemService(KEYGUARD_SERVICE);
        FingerprintManager fingerprintManager = (FingerprintManager) getContext().getSystemService(FINGERPRINT_SERVICE);

        // Verifica se o device tem o sensor de digital
        if (!fingerprintManager.isHardwareDetected()) {
            // Aqui o device nao tem o sensor de digital
        } else {
            // Aqui o device tem o sensor de digital
            // Verifica se tem permissao para a digital
            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.USE_FINGERPRINT) !=
                    PackageManager.PERMISSION_GRANTED) {
                // Precisa registrar pelo menos uma digital
            } else {
                // Aqui o device já tem ao menos uma digital cadastrada
                // Verifica se a segurança da tela de bloqueio está ativada ou não
                if (!keyguardManager.isKeyguardSecure()) {
                    Toast.makeText(getContext(), "" + "Lock screen security not enabled in Settings", Toast.LENGTH_SHORT).show();
                } else {
                    GenerateKeyCipher generateKeyCipher = new GenerateKeyCipher();
                    generateKeyCipher.generateKey();
                    if (generateKeyCipher.cipherInit()) {
                        FingerprintManager.CryptoObject cryptoObject = new FingerprintManager.CryptoObject(generateKeyCipher.getcipher());
                        FingerprintHandler helper = new FingerprintHandler(getContext());
                        helper.startAuth(fingerprintManager, cryptoObject);

                    }
                }
            }
        }
    }

}
