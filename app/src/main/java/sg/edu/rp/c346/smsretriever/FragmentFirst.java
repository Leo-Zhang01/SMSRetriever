package sg.edu.rp.c346.smsretriever;


import android.Manifest;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.PermissionChecker;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.zip.Inflater;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentFirst extends Fragment {


    Button btnAddText;
    TextView tvFrag1;
    EditText editText, button;
    String smsBody;
    public FragmentFirst() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_fragment_first, container, false);
        tvFrag1 = (TextView) view.findViewById(R.id.tvFrag1);
        btnAddText = (Button) view.findViewById(R.id.btnAddFrag1);
        editText = view.findViewById(R.id.editText);
        button = view.findViewById(R.id.button);
        btnAddText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int permissionCheck = PermissionChecker.checkSelfPermission
                        (getContext(), Manifest.permission.READ_SMS);

                if (permissionCheck != PermissionChecker.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{Manifest.permission.READ_SMS}, 0);
                    // stops the action from proceeding further as permission not
                    //  granted yet
                    return;
                }
                String like = editText.getText().toString();
                Uri uri = Uri.parse("content://sms");
                String[] reqCols = new String[]{"date", "address", "body", "type"};
                // Get Content Resolver object from which to
                //  query the content provider
                ContentResolver cr = getActivity().getContentResolver();

                // The filter String
                String filter="body LIKE ?";
                // The matches for the ?
                String[] filterArgs = {"%"+like+"%"};
                // Fetch SMS Message from Built-in Content Provider

                Cursor c = cr.query(uri, reqCols, filter, filterArgs, null);



                smsBody = "";
                if (c.moveToFirst()) {
                    do {
                        long dateInMillis = c.getLong(0);
                        String date = (String) DateFormat
                                .format("dd MMM yyyy h:mm:ss aa", dateInMillis);
                        String address = c.getString(1);
                        String body = c.getString(2);
                        String type = c.getString(3);
                        if (type.equalsIgnoreCase("1")) {
                            type = "Inbox:";
                        } else {
                            type = "Sent:";
                        }
                        smsBody += type + " " + address + "\n at " + date
                                + "\n\"" + body + "\"\n\n";
                    } while (c.moveToNext());
                }


            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvFrag1.setText(smsBody);
                Intent email = new Intent(Intent.ACTION_SEND);
// Put essentials like email address, subject & body text
                email.putExtra(Intent.EXTRA_EMAIL,
                        new String[]{"jason_lim@rp.edu.sg"});
                email.putExtra(Intent.EXTRA_SUBJECT,
                        "PS C347");
                email.putExtra(Intent.EXTRA_TEXT,
                        smsBody);
// This MIME type indicates email
                email.setType("message/rfc822");
// createChooser shows user a list of app that can handle
// this MIME type, which is, email
                startActivity(Intent.createChooser(email,
                        "Choose an Email client :"));

            }
        });
        return view;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {

        switch (requestCode) {
            case 0: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the read SMS
                    //  as if the btnRetrieve is clicked
                    btnAddText.performClick();

                } else {
                    // permission denied... notify user
                    Toast.makeText(getContext(), "Permission not granted",
                            Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
