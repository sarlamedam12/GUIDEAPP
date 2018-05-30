package com.example.user3.guideapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user3.guideapp.Adapters.CourseAdapter;
import com.example.user3.guideapp.Adapters.FaqAdapter;
import com.example.user3.guideapp.Adapters.WeekAdapter;
import com.example.user3.guideapp.Helper.SharedPrefManager;
import com.example.user3.guideapp.Model.CourseData;
import com.example.user3.guideapp.Model.HomeData;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;

public class CourseDetails extends AppCompatActivity  {
    ProgressDialog progressDialog;
    WeekAdapter weekAdapter;
    ExpandableListView expListView;
    List<String> listDataHeader;
    RecyclerView recyclerViewfaq;
    HashMap<String, List<String>> listDataChild;

    TextView textCourseId,textViewCoursedesc;
    ImageView bannerimage;
    List<CourseData.DataCourseFaq> faqList;
    FaqAdapter adapterfaq;
    String courseid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_details);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        textCourseId = findViewById(R.id.textViewTitle);
        bannerimage=findViewById(R.id.imageViewBanner);
        textViewCoursedesc=findViewById(R.id.textViewCoursedesc);
        expListView=findViewById(R.id.expandableListViewlecture);
        recyclerViewfaq=findViewById(R.id.recyclerViewfaq);
        //expListView.setGroupIndicator(null);
        progressDialog = new ProgressDialog(this);
        courseid=getIntent().getStringExtra("courseid");
        getMyCourseDesc();


    }




   /* @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if(resultCode==RESULT_OK)
            {
                courseid=data.getStringExtra("courseid");
            }
        }
    }*/

    private void setListViewHeight(ExpandableListView listView,
                                   int group) {
        ExpandableListAdapter listAdapter = (ExpandableListAdapter) listView.getExpandableListAdapter();
        int totalHeight = 0;
        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(),
                View.MeasureSpec.EXACTLY);
        for (int i = 0; i < listAdapter.getGroupCount(); i++) {
            View groupItem = listAdapter.getGroupView(i, false, null, listView);
            groupItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);

            totalHeight += groupItem.getMeasuredHeight();

            if (((listView.isGroupExpanded(i)) && (i != group))
                    || ((!listView.isGroupExpanded(i)) && (i == group))) {
                for (int j = 0; j < listAdapter.getChildrenCount(i); j++) {
                    View listItem = listAdapter.getChildView(i, j, false, null,
                            listView);
                    listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);

                    totalHeight += listItem.getMeasuredHeight();

                }
            }
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        int height = totalHeight
                + (listView.getDividerHeight() * (listAdapter.getGroupCount() - 1));
        if (height < 10)
            height = 200;
        params.height = height;
        listView.setLayoutParams(params);
        listView.requestLayout();

    }
    public void getMyCourseDesc() {
        try {
            //String res="";
            progressDialog.setMessage("loading...");
            progressDialog.show();
            new CourseDetails.GETCourseDesc().execute(SharedPrefManager.getInstance(this).getUser().access_token,courseid);

            //Toast.makeText(getApplicationContext(),res,Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            e.printStackTrace();
            progressDialog.dismiss();
            Toast.makeText(getApplicationContext(), "Error: " + e, Toast.LENGTH_SHORT).show();
            // System.out.println("Error: " + e);
        }
    }

    private class GETCourseDesc extends AsyncTask<String, Void, String>  {
        @Override
        protected String doInBackground(String... params) {

            //     InputStream inputStream
            String accesstoken = params[0];
            String courseid = params[1];
            //String res = params[2];
            String json = "";
            try {

                OkHttpClient client = new OkHttpClient();
                Request.Builder builder = new Request.Builder();
                builder.url("http://guidedev.azurewebsites.net/api/InstructorApi/GetCourseDetails/"+courseid);
                builder.addHeader("Content-Type", "application/x-www-form-urlencoded");
                builder.addHeader("Accept", "application/json");
                builder.addHeader("Authorization", "Bearer " + accesstoken);

               /* FormBody.Builder parameters = new FormBody.Builder();
                parameters.add("grant_type", "password");
                parameters.add("username", cliente);
                parameters.add("password", clave);
                builder.post(parameters.build());
                */
                okhttp3.Response response = client.newCall(builder.build()).execute();

                if (response.isSuccessful()) {
                    json = response.body().string();


                }
            } catch (Exception e) {
                e.printStackTrace();
                progressDialog.dismiss();
                // System.out.println("Error: " + e);
                Toast.makeText(getApplicationContext(), "Error: " + e, Toast.LENGTH_SHORT).show();
            }


            return json;
        }

        protected void onPostExecute(String result) {

            if (result.isEmpty()) {
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), "Invalid request", Toast.LENGTH_SHORT).show();
            } else {
               //System.out.println("CONTENIDO:  " + result);
             Gson gson = new Gson();
             CourseData.RootObject jsonbodys = gson.fromJson(result, CourseData.RootObject.class);
             CourseData.Datacoursedetails cd=jsonbodys.datacoursedetails;
             CourseData.Datacoursebanner cb=jsonbodys.datacoursebanner;
             List<CourseData.Datacoursecontent> cc=jsonbodys.datacoursecontent;
             List<CourseData.Dataweek > dw=jsonbodys.dataweek;
                List<CourseData.Datatopic > dt=jsonbodys.datatopic;
                textCourseId.setText( cd.getCourseName());
                textViewCoursedesc.setText( cd.getCourseDescription());
                String imgurl="https://guidedevblob.blob.core.windows.net/"+cd.getCourseID().toLowerCase()+"/"+cb.getFileId()+"/"+cb.getFileName().replace(' ','_').toLowerCase();
                Picasso.with(CourseDetails.this).load(imgurl).into(bannerimage);
                listDataHeader = new ArrayList<String>();
                listDataChild = new HashMap<String, List<String>>();

                //looping through all the heroes and inserting the names inside the string array
                for (int i = 0; i < dw.size(); i++) {

                    listDataHeader.add(dw.get(i).getWeekName());

                    List<String>  wn  = new ArrayList<String>();
                    for(int j = 0; j < dt.size(); j++)
                    {
                        if(dt.get(j).getWeekID()==dw.get(i).getWeekID()) {
                            for(int k=0;k<cc.size();k++){
                                if(dt.get(j).getTopicID()==cc.get(k).getTopicID()){
                                    String contentid=Integer.toString( cc.get(k).getContentID());
                                    String lecture=dt.get(j).getTopicName()+":"+cc.get(k).getContentTitle()+","+contentid;

                                    wn.add(lecture);

                                }
                            }
                        }
                    }
                    listDataChild.put( dw.get(i).getWeekName(), wn);
                   //System.out.println("Weekname:"+ weeks[i]);
                }
                weekAdapter = new WeekAdapter(CourseDetails.this, listDataHeader, listDataChild);

                expListView.setAdapter(weekAdapter);
                expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
                    @Override
                    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {


                       // Intent contentdetails = new Intent(CourseDetails.this,ContentDetails.class);
                        //startActivity(contentdetails);
                      String contentid=listDataChild.get(listDataHeader.get(groupPosition)).get(childPosition);
                      contentid=contentid.substring(contentid.lastIndexOf(",") + 1);

                        Intent contentdetails = new Intent(CourseDetails.this,ContentDetails.class);
                        contentdetails.putExtra("contentid",contentid);
                        contentdetails.putExtra("courseid",courseid);

                              startActivity(contentdetails);
                        //startActivityForResult(contentdetails, 1);
                        return false;
                    }
                });
                expListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
                    @Override
                    public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                        setListViewHeight(parent, groupPosition);
                        return false;
                    }
                });
               // List<CourseData.DataCourseFaq > df=jsonbodys.dataCourseFaq;

                faqList=new ArrayList<>();
                faqList=jsonbodys.dataCourseFaq;
                adapterfaq = new FaqAdapter(CourseDetails.this, faqList);
                recyclerViewfaq.setLayoutManager(new LinearLayoutManager(CourseDetails.this));
                recyclerViewfaq.setAdapter(adapterfaq);
                //Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
                //jsonbody.datacoursebanner.getFileName();
                //courseapiList=new ArrayList<>();
                //  courseapiList=jsonbody.courseapilist;
                //  adapter = new CourseAdapter(MyCourse.this, courseapiList);
                //  recyclerView.setAdapter(adapter);

                progressDialog.dismiss();

            }


        }
    }
}