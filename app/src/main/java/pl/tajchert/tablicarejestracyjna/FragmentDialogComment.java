package pl.tajchert.tablicarejestracyjna;


import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;


/**
 * A simple {@link android.app.DialogFragment} subclass.
 */
public class FragmentDialogComment extends DialogFragment {
    private String plateId;

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
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        View v = inflater.inflate(R.layout.fragment_dialog_comment, container, false);

        editTextPlateId = (EditText) v.findViewById(R.id.EditTextPlateId);
        editTextNick = (EditText) v.findViewById(R.id.EditTextNick);
        editTextComment = (EditText) v.findViewById(R.id.EditTextComment);
        sendButton = (ImageView) v.findViewById(R.id.buttonSend);


        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //SEND
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

        return v;
    }


}
