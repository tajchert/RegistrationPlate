package pl.tajchert.tablicarejestracyjna;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

import pl.tajchert.tablicarejestracyjna.api.APIConnect;


/**
 * A simple {@link android.app.DialogFragment} subclass.
 */
public class FragmentDialogComment extends DialogFragment {
    private String plateId;

    public String lastPicLocation;

    private EditText editTextPlateId;
    private EditText editTextNick;
    private EditText editTextComment;
    private ImageView sendButton;
    private Button imageButton;
    private ImageView imageSelected;


    static FragmentDialogComment newInstance(String plateId) {
        FragmentDialogComment f = new FragmentDialogComment();
        if (plateId == null) {
            return f;
        }
        Bundle args = new Bundle();
        args.putString("plateIdCommentFrag", plateId);
        f.setArguments(args);

        return f;
    }

    public FragmentDialogComment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            plateId = getArguments().getString("plateIdCommentFrag");
        } catch (Exception e) {
            //no such string - show plate editText
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_dialog_comment, container, false);

        editTextPlateId = (EditText) v.findViewById(R.id.EditTextPlateId);
        editTextNick = (EditText) v.findViewById(R.id.EditTextNick);
        editTextComment = (EditText) v.findViewById(R.id.EditTextComment);
        sendButton = (ImageView) v.findViewById(R.id.buttonSend);
        imageButton = (Button) v.findViewById(R.id.buttonImage);
        imageSelected = (ImageView) v.findViewById(R.id.imageSelected);

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogChooser();
            }
        });

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //SEND

                String plate;
                if (plateId != null && plateId.length() > 0) {
                    plate = plateId;
                } else {
                    plate = editTextPlateId.getText().toString();
                }
                if (plate == null || plate.length() == 0) {
                    //cancell if plate is null
                    Toast.makeText(getActivity().getApplicationContext(), "Dodaj tablice rejestracyjną.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (editTextNick.getText().toString().length() == 0 || editTextComment.getText().toString().length() == 0) {
                    //Do not add empty comments
                    Toast.makeText(getActivity().getApplicationContext(), "Dodaj komentarz.", Toast.LENGTH_SHORT).show();
                    return;
                }
                UploadComment uploader = new UploadComment(editTextNick.getText().toString(), editTextComment.getText().toString(), plate, lastPicLocation);
                uploader.execute();
            }
        });

        if (plateId == null || plateId.length() == 0) {
            editTextPlateId.setText("");
            editTextPlateId.requestFocus();
        } else {
            editTextPlateId.setText(plateId);
            editTextNick.requestFocus();
        }
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        getDialog().setTitle(getResources().getString(R.string.title_activity_new_comment));
        getDialog().setCanceledOnTouchOutside(false);
        return v;
    }


    public void setImage(Bitmap selectedBitmap) {
        if (selectedBitmap != null) {
            imageSelected.setVisibility(View.VISIBLE);
            imageSelected.setImageBitmap(selectedBitmap);
        } else {
            imageSelected.setVisibility(View.GONE);
            imageSelected.setImageBitmap(null);
        }
    }

    private class UploadComment extends AsyncTask<String, Void, String> {
        private String author;
        private String content;
        private String plateId;
        private String image;

        private UploadComment(String author, String content, String plateId, String image) {
            this.author = author;
            this.content = content;
            this.plateId = plateId;
            this.image = image;
        }

        @Override
        protected String doInBackground(String... params) {
            plateId = plateId.replace(" ", "");
            if(image != null && image.contains("file:")){
                image=image.replace("file:", "");
            }
            APIConnect.addComment(author, plateId, content, image, getActivity());
            return "Executed";
        }

        @Override
        protected void onPostExecute(String result) {
            getDialog().dismiss();
        }

        @Override
        protected void onPreExecute() {
        }
    }

    private void showDialogChooser() {
        String items[] = {"Z biblioteki", "Wykonaj nowe"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Zdjęcie");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                // Do something with the selection
                if (item == 0) {
                    choosePictureIntentDispach();
                } else if (item == 1) {
                    //take picture
                    takePictureIntentDispach();
                }
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void choosePictureIntentDispach() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        getActivity().startActivityForResult(photoPickerIntent, ToolConstants.ACTION_GALLERY_PICTURE);
    }

    private void takePictureIntentDispach() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = ToolConstants.createImageFile();
                lastPicLocation = photoFile.getAbsolutePath();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                getActivity().startActivityForResult(takePictureIntent, ToolConstants.ACTION_TAKE_PICTURE);
            }
        }
    }




}
