package com.example.admin.printqr.ui.Qr_Generator;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.print.PrintHelper;

import com.example.admin.printqr.R;
import com.example.admin.printqr.ShowPrintQR;

public class QrGeneratorFragment extends Fragment {
    private Bitmap qRBit;
    Button button;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_qr_generator, container, false);


        qRBit = getActivity().getIntent().getParcelableExtra("bitmap");
        ImageView image = (ImageView) root.findViewById(R.id.imageView);
        image.setImageBitmap(qRBit);


        button =  root.findViewById(R.id.button3);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doPhotoPrint();
            }
        });
        return root;
    }

    public void print(View view) {
        doPhotoPrint();
    }


    private void doPhotoPrint() {
        PrintHelper photoPrinter = new PrintHelper(getContext());
        photoPrinter.setScaleMode(PrintHelper.SCALE_MODE_FIT);

        //print
        photoPrinter.printBitmap("image.png_test_print", qRBit, new PrintHelper.OnPrintFinishCallback() {
            @Override
            public void onFinish() {
                Toast.makeText(getContext(), "Thank you!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}