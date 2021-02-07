package com.example.admin.printqr.ui.Qr_Generator;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.admin.printqr.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class QrGeneratorFragment extends Fragment implements View.OnClickListener {

    private Bitmap qRBit;
    Button button;
    ImageView image,ImageView;
    private EditText nameET, prodDateET, expDate;
    private DatabaseReference productsDBRef;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_qr_generator, container, false);


        //Initialize items
        image = root.findViewById(R.id.imageView);
        ImageView = root.findViewById(R.id.productImageView);
        nameET = root.findViewById(R.id.nameET);
        prodDateET = root.findViewById(R.id.manDatET);
        expDate = root.findViewById(R.id.expDateET);
        button =  root.findViewById(R.id.button3);


        productsDBRef = FirebaseDatabase.getInstance().getReference("Products");


        //click listeners
        button.setOnClickListener(this);
        ImageView.setOnClickListener(this);
        return root;
    }








    private void generateQR(final String prodID) {

        QRCodeWriter writer = new QRCodeWriter();
        try {
           // BitMatrix bitMatrix = writer.encode("bitcoin: "+ btc_acc_adress + "\n amount = "+ amountBTC, BarcodeFormat.QR_CODE, 512, 512);
            BitMatrix bitMatrix = writer.encode(prodID, BarcodeFormat.QR_CODE, 512, 512);

            int width = bitMatrix.getWidth();
            int height = bitMatrix.getHeight();

            Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {

                    bmp.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);

                }
            }

            image.setVisibility(View.VISIBLE);
            image.setImageBitmap(bmp);

            StorageReference reference = FirebaseStorage.getInstance().getReference().child("ProductsQR/"+prodID+".jpg");

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data = baos.toByteArray();

            reference.putBytes(data).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {



                    Snackbar.make(getView(),"Task Failed " +e.getMessage(), BaseTransientBottomBar.LENGTH_SHORT);




                }

            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {


                    Task<Uri> downloadUri=taskSnapshot.getStorage().getDownloadUrl()

                            .addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {

                                    String  imageLink=task.getResult().toString();

                                    Map newImage=new HashMap();

                                    newImage.put("productQRImageURL",imageLink);

                                    productsDBRef.child(prodID).updateChildren(newImage);

                                    Toast.makeText(getContext(),"Uploaded", Toast.LENGTH_LONG).show();
                                    getActivity().finish();

                                }
                            });


                    Snackbar.make(getView(),"Task Uploaded successfully.", BaseTransientBottomBar.LENGTH_SHORT);
                }
            });


        } catch (WriterException e) {

            e.printStackTrace();

        }
    }








    @Override
    public void onClick(View v) {

        if (v == ImageView){

            Intent getImageIntent = new Intent(Intent.ACTION_PICK);
            getImageIntent.setType("image/*");
            startActivityForResult(getImageIntent, 99);



        }else if (v == button){

            uploadProduct();
        }
    }









    String prodName,manDate,expiryDate;

    private void uploadProduct() {

        prodName = nameET.getText().toString().trim();
        manDate = prodDateET.getText().toString().trim();
        expiryDate = expDate.getText().toString().trim();


        if (!prodName.isEmpty() && !manDate.isEmpty() && !expiryDate.isEmpty()){

            Map productInfor = new HashMap();
            productInfor.put("Product Name", prodName);
            productInfor.put("Manufacture Date", manDate);
            productInfor.put("Expiry Date", expiryDate);



            String productID = productsDBRef.push().getKey();
            productsDBRef.child(productID).updateChildren(productInfor);
            uploadImage(productID);
            generateQR(productID);
        }
    }








    private Uri resultUri;
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 99 && resultCode == Activity.RESULT_OK && data !=null && data.getData() != null){

            resultUri = data.getData();

            try {

                Bitmap bitmap= MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(),resultUri);
                ImageView.setImageBitmap(bitmap);

            } catch (FileNotFoundException e) {

                e.printStackTrace();

            } catch (IOException e) {

                e.printStackTrace();

            }


        }
    }



    public void uploadImage(final String productID){
        if (resultUri != null)
        {

            StorageReference reference = FirebaseStorage.getInstance().getReference().child("Products/"+productID+".jpg");

            reference.putFile(resultUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            Task<Uri> downloadUri=taskSnapshot.getStorage().getDownloadUrl()

                                    .addOnCompleteListener(new OnCompleteListener<Uri>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Uri> task) {

                                            String  imageLink=task.getResult().toString();

                                            Map newImage=new HashMap();

                                            newImage.put("productImageURL",imageLink);

                                            productsDBRef.child(productID).updateChildren(newImage);

                                            Toast.makeText(getContext(),"Uploaded", Toast.LENGTH_LONG).show();

                                        }
                                    });


                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    Toast.makeText(getContext(),"error"+e.getMessage(),Toast.LENGTH_LONG).show();
                }
            });
        }
    }
}