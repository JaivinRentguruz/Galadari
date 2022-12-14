package com.rentguruz.app.b2b.galadariauto.flexiicar.selfcheckout;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.rentguruz.app.b2b.galadariauto.R;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.androidnetworking.AndroidNetworking;
import com.rentguruz.app.b2b.galadariauto.adapters.CustomToast;
import com.rentguruz.app.b2b.galadariauto.adapters.LoginRes;
import com.rentguruz.app.b2b.galadariauto.apicall.ApiService;
import com.rentguruz.app.b2b.galadariauto.apicall.OnResponseListener;
import com.rentguruz.app.b2b.galadariauto.model.AttachmentsModel;
import com.rentguruz.app.b2b.galadariauto.model.parameter.CommonParams;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import static com.rentguruz.app.b2b.galadariauto.apicall.ApiEndPoint.UPLOADIMAGE;

public class Fragment_Vehicle_Image_2 extends Fragment {
    LinearLayout lblNext;
    ImageView imgback, UploadImg;
    private static final int RESULT_LOAD_IMAGE = 1;
    String imagestr;
    JSONArray ImageList = new JSONArray();
    TextView DateTime, txtDiscard;
   // Bundle AgreementsBundle;
   Bundle bundle = new Bundle();
    Handler handler = new Handler();
    LoginRes loginRes;
    List<AttachmentsModel> attachmentsModelList = new ArrayList<>();
    CommonParams params;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        loginRes = new LoginRes(getContext());
        params = new CommonParams();
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_vehicle_images_2, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        DateTime = view.findViewById(R.id.txt_DateTimeVehImg2);
        Calendar c = Calendar.getInstance();
        SimpleDateFormat dateformat = new SimpleDateFormat("dd-MM-yyyy hh:mm aa");
        String datetime = dateformat.format(c.getTime());
        DateTime.setText(datetime);
        try {
          /*  AgreementsBundle = getArguments().getBundle("AgreementsBundle");
            ImageList = new JSONArray(AgreementsBundle.getString("ImageList"));*/
            bundle.putInt( "Id", getArguments().getInt("Id"));
            attachmentsModelList = (List<AttachmentsModel>) getArguments().getSerializable("image");
            lblNext = view.findViewById(R.id.lblback_backside);
            imgback = view.findViewById(R.id.backarrow_backside);
            UploadImg = view.findViewById(R.id.BackSideImage);
            lblNext.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                 /*   AgreementsBundle.putString("ImageList", ImageList.toString());
                    Bundle Agreements = new Bundle();
                    Agreements.putBundle("AgreementsBundle", AgreementsBundle);
                    System.out.println(Agreements);*/
                    bundle.putSerializable("image", (Serializable) attachmentsModelList);
                    NavHostFragment.findNavController(Fragment_Vehicle_Image_2.this)
                            .navigate(R.id.action_Vehicle_img_2_to_Vehicle_Image_3, bundle);
                }
            });
            UploadImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, RESULT_LOAD_IMAGE);
                }
            });
            imgback.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                 /*   AgreementsBundle.putString("ImageList", ImageList.toString());
                    Bundle Agreements = new Bundle();
                    Agreements.putBundle("AgreementsBundle", AgreementsBundle);*/
                    NavHostFragment.findNavController(Fragment_Vehicle_Image_2.this)
                            .navigate(R.id.action_Vehicle_img_2_to_Vehicle_Image_1, bundle);
                }
            });

            txtDiscard = view.findViewById(R.id.DiscardVeh2);

            txtDiscard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    NavHostFragment.findNavController(Fragment_Vehicle_Image_2.this)
                            .navigate(R.id.action_Vehicle_img_2_to_Agreements);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == Activity.RESULT_OK && null != data)
        {
            Uri selectedImage = data.getData();
            Bitmap bitmap = null;
            String[] imageProjection = {MediaStore.Images.Media.DATA};
            Cursor cursor =getContext().getContentResolver().query(selectedImage, imageProjection, null, null, null);
            if(cursor != null) {
                cursor.moveToFirst();
                int indexImage = cursor.getColumnIndex(imageProjection[0]);
                String  part_image = cursor.getString(indexImage);
                File file = new File(part_image);

                AndroidNetworking.initialize(getActivity());
                ApiService apiService = new ApiService();

                Double file_size = Double.valueOf(String.valueOf(file.length()/1024));
                Double file_sizeMB = file_size/1024;

                Log.d("TAG", "onActivityResult: " + file_sizeMB);

                apiService.UPLOAD_REQUEST(uploadImage,UPLOADIMAGE, params.getCheckoutImage(getArguments().getInt("Id")), file);

            }

            try
            {
                bitmap = getScaledBitmap(selectedImage,400,400);
                JSONObject docObj = new JSONObject();
                docObj.put("Doc_For",9);
                docObj.put("VhlPictureSide", 2);
                docObj.put("fileBase64",getImagePathFromUri(selectedImage) );
                ImageList.put(docObj);
            }
            catch (Exception e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            UploadImg.setImageBitmap(bitmap);
        }
    }

    public String getImagePathFromUri(Uri contentUri)
    {
        String res = null;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getActivity().getContentResolver().query(contentUri, proj, null, null, null);
        if (cursor.moveToFirst()) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            res = cursor.getString(column_index);
        }
        cursor.close();
        return res;
    }

    private Bitmap getScaledBitmap(Uri selectedImage, int width, int height) throws FileNotFoundException
    {
        BitmapFactory.Options sizeOptions = new BitmapFactory.Options();
        sizeOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(getActivity().getContentResolver().openInputStream(selectedImage), null, sizeOptions);

        int inSampleSize = calculateInSampleSize(sizeOptions, width, height);

        sizeOptions.inJustDecodeBounds = false;
        sizeOptions.inSampleSize = inSampleSize;

        return BitmapFactory.decodeStream(getActivity().getContentResolver().openInputStream(selectedImage), null, sizeOptions);
    }

    private int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight)
    {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            // Calculate ratios of height and width to requested one
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);

            // Choose the smallest ratio as inSampleSize value
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        return inSampleSize;
    }

    OnResponseListener uploadImage = new OnResponseListener() {
        @Override
        public void onSuccess(String response, HashMap<String, String> headers) {
            Log.d("TAG", "onSuccess: " + response);
            handler.post(new Runnable() {
                @Override
                public void run() {
                    try {
                        JSONObject responseJSON = new JSONObject(response);
                        Boolean status = responseJSON.getBoolean("Status");

                        if (status)
                        {
                            JSONArray data = responseJSON.getJSONArray("Data");
                            for (int i = 0; i <data.length() ; i++) {
                                AttachmentsModel attachmentsModel = new AttachmentsModel();
                                attachmentsModel =    loginRes.getModel(data.get(i).toString(), AttachmentsModel.class);
                                attachmentsModelList.add(attachmentsModel);
                            }

                        }
                        else
                        {
                            String msg = responseJSON.getString("Message");
                            CustomToast.showToast(getActivity(),msg,1);
                        }
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            });
        }

        @Override
        public void onError(String error) {
            Log.d("TAG", "onError: " + error);
        }
    };
}
