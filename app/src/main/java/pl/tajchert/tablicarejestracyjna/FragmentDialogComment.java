package pl.tajchert.tablicarejestracyjna;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;

import pl.tajchert.tablicarejestracyjna.api.APIConnect;


/**
 * A simple {@link android.app.DialogFragment} subclass.
 */
public class FragmentDialogComment extends DialogFragment {
    private String plateId;

    final int REQUEST_IMAGE_CAPTURE = 1;
    final int REQUEST_IMAGE_GALLERY= 2;

    private EditText editTextPlateId;
    private EditText editTextNick;
    private EditText editTextComment;
    private ImageView sendButton;

    static FragmentDialogComment newInstance(String plateId) {
        FragmentDialogComment f = new FragmentDialogComment();
        if(plateId == null) {
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




        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //SEND

                String plate;
                if(plateId == null || plateId.length() == 0) {
                    plate = plateId;
                } else {
                    plate = editTextPlateId.getText().toString();
                }
                if(plate == null || plate.length()==0){
                    //cancell if plate is null
                    return;
                }
                if(editTextNick.getText().toString().length() == 0 || editTextComment.getText().toString().length() == 0){
                    //Do not add empty comments
                    return;
                }
                UploadComment uploader = new UploadComment(editTextNick.getText().toString(),editTextComment.getText().toString(), plate,null);
                uploader.execute();
            }
        });

        if(plateId == null || plateId.length() == 0){
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
            APIConnect.addComment(author,plateId,content,image);
            return "Executed";
        }

        @Override
        protected void onPostExecute(String result) {
            getDialog().dismiss();
        }

        @Override
        protected void onPreExecute() {}
    }
}
