package com.bamboo.bullyalert.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import org.jsoup.Jsoup;
import com.bamboo.bullyalert.NavigationActivity;
import com.bamboo.bullyalert.R;
import com.bamboo.bullyalert.UtilityPackage.UtilityFunctions;
import com.bamboo.bullyalert.UtilityPackage.UtilityVariables;
import com.bamboo.bullyalert.adapter.MyAddUserRecyclerViewAdapter;
import com.bamboo.bullyalert.model.AddUser;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.nodes.DataNode;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.util.ArrayList;
import java.util.List;

public class AddUserFragment extends Fragment implements AdapterView.OnItemSelectedListener{

    private static final String ARG_COLUMN_COUNT = "column-count";
    private OnListFragmentInteractionListener mListListener;
    private String mSocialNetworkName;
    private EditText mEditTextUserToBeSearched;
    private Button mMonitorButton;
    private RecyclerView mRecyclerView;
    private List<AddUser>mListUserSearch;
    private RecyclerView.Adapter mUserSearchResultAdapter;
    private ProgressDialog mProgressDialog ;
    private Context mContext;

    public AddUserFragment()
    {
    }

    //@SuppressWarnings("unused")
    public static AddUserFragment newInstance() {
        AddUserFragment fragment = new AddUserFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, 1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_add_user_page, container, false);
        this.mContext = view.getContext();
        mProgressDialog = new ProgressDialog(mContext);
        Button mUserSearchButton = (Button) view.findViewById(R.id.searchButton);
        mMonitorButton = (Button)view.findViewById(R.id.monitorButton);
        mEditTextUserToBeSearched = (EditText)view.findViewById(R.id.edittext_username);
        mUserSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserSearch();
            }
        });
        mMonitorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performMonitoringRequestFunction();
            }
        });
        Spinner spinner = (Spinner) view.findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(mContext, R.array.social_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.list);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mRecyclerView.getContext(), LinearLayoutManager.VERTICAL);
        mRecyclerView.addItemDecoration(dividerItemDecoration);
        mMonitorButton = (Button)view.findViewById(R.id.monitorButton);
        mRecyclerView.setVisibility(View.GONE);
        mMonitorButton.setVisibility(View.GONE);
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListListener = null;
    }

    private void performMonitoringRequestFunction()
    {
        PerformMonitoringRequest performMonitoringRequest = new PerformMonitoringRequest();
        performMonitoringRequest.execute((Void) null);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        this.mSocialNetworkName = parent.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public interface OnListFragmentInteractionListener
    {
    }

    private void UserSearch()
    {
        String username = mEditTextUserToBeSearched.getText().toString().trim();
        final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
        getUserSearchResultByUserName(username);

    }

    private class JsoupAsyncTask extends AsyncTask<Void, Void, Boolean> {
        String usernameToSearch;
        ProgressDialog dialog;
        Context context;
        AddUser user;
        public JsoupAsyncTask(String usernameToSearch,ProgressDialog dialog, Context context) {
            super();
            this.usernameToSearch = usernameToSearch;
            this.dialog = dialog;
            this.context = context;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                Document doc;
                String htmlPageUrl = "https://www.instagram.com/"+this.usernameToSearch+"/";
                doc = Jsoup.connect(htmlPageUrl).get();
                Elements scriptElements = doc.getElementsByTag("script");
                for (Element element :scriptElements ){
                    for (DataNode node : element.dataNodes())
                    {
                        if(node.getWholeData().toString().contains("window._sharedData"))
                        {
                            String data = node.getWholeData().toString();
                            data = data.substring(data.indexOf("{"));
                            JSONObject obj = new JSONObject(data);
                            JSONArray objArray = obj.getJSONObject("entry_data").getJSONArray("ProfilePage");
                            JSONObject jsonObject = objArray.getJSONObject(0).getJSONObject("graphql").getJSONObject("user");
                            String username = jsonObject.optString("username");
                            String user_id = jsonObject.optString("id");
                            String urlProfilePicture = jsonObject.optString("profile_pic_url");
                            String website = "";
                            String full_name = jsonObject.optString("full_name");
                            String bio = jsonObject .optString("biography");
                            this.user = new AddUser(user_id,username,urlProfilePicture,false,full_name,bio,website);
                            return true;

                        }
                    }
                }
            } catch (Exception e) {
                Log.e(UtilityVariables.tag, "Exception while parsing: "+e.toString()+"class: "+this.getClass().getName());
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean result)
        {
            this.dialog.dismiss();
            if(result.booleanValue() == false)
            {
                Toast.makeText(this.context,"No user found. Try with a different username please.",Toast.LENGTH_SHORT).show();
                if(mListUserSearch != null)
                    mListUserSearch.clear();
                if (mUserSearchResultAdapter != null)
                    mUserSearchResultAdapter.notifyDataSetChanged();
            }
            else
            {
                mListUserSearch = new ArrayList<>();
                mListUserSearch.add(this.user);
                mUserSearchResultAdapter = new MyAddUserRecyclerViewAdapter(mListUserSearch,mListListener,this.context);
                mRecyclerView.setAdapter(mUserSearchResultAdapter);
                mRecyclerView.setVisibility(View.VISIBLE);
                mMonitorButton.setVisibility(View.VISIBLE);
                Log.i(UtilityVariables.tag, "user added to the user-search list UI: "+this.user.getmUserId());
            }

        }
    }


    private void getUserSearchResultByUserName(String username)
    {
        final ProgressDialog progressDialog = new ProgressDialog(this.mContext);
        progressDialog.setMessage("Getting the users...");
        progressDialog.show();
        if(this.mSocialNetworkName.equals("Instagram"))
        {
            JsoupAsyncTask jsoupAsyncTask = new JsoupAsyncTask(username,progressDialog,this.mContext);
            jsoupAsyncTask.execute();
        }
    }

    private class PerformMonitoringRequest extends AsyncTask<Void, Void, Boolean>
    {
        String mMessage;
        PerformMonitoringRequest() {
        }
        protected Boolean doInBackground(Void... params)
        {
            try {
                mProgressDialog.setMessage("Processing your monitoring request...");
                JSONObject data = new JSONObject();
                data.put("email",UtilityVariables.USER_EMAIL);
                JSONArray array = new JSONArray();
                int j = 0;
                for(int i =0;i<mListUserSearch.size();i++)
                {
                    if(mListUserSearch.get(i).ismChecked())
                    {
                        JSONObject temp = new JSONObject();
                        temp.put("email",UtilityVariables.USER_EMAIL);
                        temp.put("userid",mListUserSearch.get(i).getmUserId());
                        temp.put("username",mListUserSearch.get(i).getmUserName());
                        array.put(j,temp);
                        j+=1;
                    }
                }
                data.put("data",array);
                String urlString = UtilityVariables.INSTAGRAM_MONITOR_USER_SERVER;
                JSONObject resultjson = UtilityFunctions.getJsonStringFromPostRequestUrlString(urlString,data);
                if(resultjson != null && resultjson.optString("message") != null && resultjson.optString("success") != null)
                {
                    mMessage = resultjson.optString("message");
                    String success = resultjson.optString("success");
                    return success.equals("success");
                }
                else
                {
                    return false;
                }
            }catch (Exception e)
            {
                Log.e(UtilityVariables.tag,e.toString()+" in performMonitoringRequest in class "+this.getClass().getName());
                return false;
            }
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mProgressDialog.dismiss();
            if(mMessage != null)
            {
                Toast.makeText(mContext, mMessage, Toast.LENGTH_SHORT).show();
            }
            else
            {
                if(UtilityFunctions.isNetworkAvailable(mContext))
                {
                    Toast.makeText(mContext, "Something went wrong. Please try again.", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(mContext, "Please make sure the internet is on", Toast.LENGTH_SHORT).show();
                }
            }
            if (success)
            {
                Log.i(UtilityVariables.tag,"User has been inserted successfully in the server database");
                Intent intent = new Intent(mContext, NavigationActivity.class);
                startActivity(intent);
            }
        }

        @Override
        protected void onCancelled()
        {
            Log.i(UtilityVariables.tag,"Process was cancelled");
        }
    }



}
