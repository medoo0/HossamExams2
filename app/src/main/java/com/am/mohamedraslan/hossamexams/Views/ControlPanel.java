package com.am.mohamedraslan.hossamexams.Views;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.am.mohamedraslan.hossamexams.Contracts.ControlPanelContract;
import com.am.mohamedraslan.hossamexams.Dialog.AlertDialog;
import com.am.mohamedraslan.hossamexams.Dialog.AnimatedDialog;
import com.am.mohamedraslan.hossamexams.Dialog.NotificationDialog;
import com.am.mohamedraslan.hossamexams.Dialog.StudentDialog;
import com.am.mohamedraslan.hossamexams.Fragment.AboutDoctor;
import com.am.mohamedraslan.hossamexams.Fragment.AboutProgrammer;
import com.am.mohamedraslan.hossamexams.Fragment.AddQ_frag;
import com.am.mohamedraslan.hossamexams.Fragment.ExamList;
import com.am.mohamedraslan.hossamexams.Fragment.ExamsResults;
import com.am.mohamedraslan.hossamexams.Fragment.MyResults;
import com.am.mohamedraslan.hossamexams.Fragment.PermissionsFromStudent;
import com.am.mohamedraslan.hossamexams.Fragment.Question_Bank_Frag;
import com.am.mohamedraslan.hossamexams.Fragment.RequestFromStudentToExamWhat;
import com.am.mohamedraslan.hossamexams.Fragment.StudentManagement;
import com.am.mohamedraslan.hossamexams.Fragment.StudentsWrongs;
import com.am.mohamedraslan.hossamexams.Fragment.addExam;
import com.am.mohamedraslan.hossamexams.JsonModel.Result_Pojo;
import com.am.mohamedraslan.hossamexams.JsonModel.WorngQestion;
import com.am.mohamedraslan.hossamexams.MainPresnter.ControlpanelPresnter;
import com.am.mohamedraslan.hossamexams.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;

public class ControlPanel extends AppCompatActivity
                          implements ControlPanelContract.ControlUI
                                   , View.OnClickListener
                                   , NavigationView.OnNavigationItemSelectedListener{

    private Toolbar toolbar;
    private ImageView open_nav;
    private DrawerLayout drawer;
    private static NavigationView navigation;
    public static TextView Title ;
    private FirebaseAuth auth;
    StudentDialog studentDialog;
    ControlpanelPresnter controlpanelPresnter;
    boolean AreAdmin = false;
    AnimatedDialog animatedDialog;
    BroadcastReceiver broadcastReceiver;
    NotificationDialog notificationDialog;
    CircleImageView circleImageView;
    TextView UserName;
    public static ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawaer);




        // حنسجل الtokendevice في الداتا بيز الاول

        //notification
        FirebaseMessaging.getInstance().subscribeToTopic("all");


        controlpanelPresnter = new ControlpanelPresnter(this);
        controlpanelPresnter.updateUitoViews();

//        controlpanelPresnter.tellmodelAretokenExisitorNot();
        //  حنشةف التوكن متخزن اصلا ولا لا في الداتا بيز

        String refreshedToken = FirebaseInstanceId.getInstance().getToken();

        controlpanelPresnter.tellModeltostoreToken(refreshedToken);


        //اخفاء الادوات من المستخدم العادي
        hideAdminToolsFromUsers();

        //Check if User Banned افحص المستخدم اذا كان محظور
        controlpanelPresnter.CheckifUserBanned(auth.getCurrentUser().getUid());

        //default fragment .
        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right, R.anim.slide_in_right, R.anim.slide_out_left)
                .replace(R.id.Exam_Frame,new ExamList())
                .commit();

        //image profile back ground .
        Random r = new Random();
        int n = r.nextInt(3);
        if(n == 0 ){
         circleImageView.setBackgroundResource(R.drawable.ic_student_1);

        }
        else if(n == 1){
            circleImageView.setBackgroundResource(R.drawable.ic_student_2);
        }
        else if(n == 2){
            circleImageView.setBackgroundResource(R.drawable.ic_student_3);
        }
        else {
            circleImageView.setBackgroundResource(R.drawable.ic_student_4);
        }

        ///////////
        controlpanelPresnter.CheckifAdmin(auth.getCurrentUser().getUid());
        controlpanelPresnter.getuserName(auth.getCurrentUser().getUid());

    }



    @Override
    public void showWrongsforStudent(Result_Pojo result_pojo,String FinalDegree , String TotalD) {


        StudentsWrongs myResults = new StudentsWrongs();
        Bundle b   = new Bundle();
        b.putString("me" , "1");
        b.putString("FinalDegree",FinalDegree);
        b.putString("Total",TotalD);
        b.putParcelableArrayList("WrongQuestions",result_pojo.getWrongQuestions());
        myResults.setArguments(b);
        getSupportFragmentManager().popBackStack();
        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right, R.anim.slide_in_right, R.anim.slide_out_left)
                .replace(R.id.Exam_Frame,myResults).addToBackStack(null)
                .commit();

    }

    @Override
    public void showFragmentWrongs(String name, String finalDegree, String total, String examID, ArrayList<WorngQestion> arrayList, Integer imageTag, String uID, CircleImageView imageView) {



        Bundle bundle = new Bundle();
        bundle.putString("Name",name );
        bundle.putString("FinalDegree",finalDegree);
        bundle.putString("Total",total);
        bundle.putString("examID",examID);
        bundle.putParcelableArrayList("WrongQuestions",arrayList);
        //to pass image to next fragment.
        bundle.putInt("Image", imageTag);
        bundle.putString("UserUid",uID);
        // set MyFragment Arguments
        StudentsWrongs StudentsWrongs = new StudentsWrongs();
        StudentsWrongs.setArguments(bundle);




        ViewCompat.setTransitionName(imageView, "Image");
        getSupportFragmentManager()
                .beginTransaction()
                .addSharedElement(imageView, imageView.getTransitionName())
                .replace(R.id.Exam_Frame, StudentsWrongs)
                .addToBackStack(null)
                .commit();



    }

    @Override
    public void showDialogStudent(String what) {

        studentDialog.dismiss();

        switch (what){

            case "allStudents":


                StudentManagement all = new StudentManagement();
                Bundle b = new Bundle();
                b.putString("what","allStudents");
                all.setArguments(b);
                getSupportFragmentManager().popBackStack();
                getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right, R.anim.slide_in_right, R.anim.slide_out_left)
                        .replace(R.id.Exam_Frame,all)
                        .addToBackStack(null)
                        .commit();

                break;

           case "myStudents":

               StudentManagement myStudnet = new StudentManagement();
               Bundle b1 = new Bundle();
               b1.putString("what","myStudents");
               myStudnet.setArguments(b1);
               getSupportFragmentManager().popBackStack();
               getSupportFragmentManager()
                       .beginTransaction()
                       .setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right, R.anim.slide_in_right, R.anim.slide_out_left)
                       .replace(R.id.Exam_Frame,myStudnet)
                       .addToBackStack(null)
                       .commit();

               break;


        }






    }

    @Override
    public void initializeViews() {
        toolbar    = findViewById(R.id.toolbar);
        open_nav   = findViewById(R.id.open_nav);
        open_nav.setOnClickListener(this);
        drawer     = findViewById(R.id.drawer);
        navigation = findViewById(R.id.navigation);
        Menu nav_Menu = navigation.getMenu();
        nav_Menu.findItem(R.id.exit).setEnabled(false);
        Title      = toolbar.findViewById(R.id.toolbar_title);
        auth       = FirebaseAuth.getInstance();
        progressBar = findViewById(R.id.progressBar);
        animatedDialog = new AnimatedDialog(this);
        View headerLayout = navigation.getHeaderView(0);
        circleImageView = headerLayout.findViewById(R.id.myprofile);
        UserName        = headerLayout.findViewById(R.id.UserName);
        setSupportActionBar(toolbar);
//        open_nav.setOnClickListener(this);
        navigation.setNavigationItemSelectedListener(this);


    }



    @Override
    public void whenClickFAB_showFrag() {

        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right, R.anim.slide_in_right, R.anim.slide_out_left)
                .replace(R.id.Exam_Frame,new AddQ_frag())
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void CheckifUserBannedResult(String Result) {

        //اذا كان هذا الطالب محظور

        if(Result.equals("Successful")) {

            if(!ControlPanel.this.isFinishing())
            {

                AlertDialog alertDialog = new AlertDialog(ControlPanel.this
                        , getString(R.string.YouareBanned));
                alertDialog.setCancelable(false);
                alertDialog.show();
                alertDialog.btnNo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {


                        Intent intent = new Intent(ControlPanel.this , MainActivity.class);
                        startActivity(intent);

                        finish();



                        //لو عاوز المحظور ميعرش يعمل اي اكونت تاني علي البرنامج يبقه الغي السطر الجي
                        auth.signOut();
                    }
                });
            }


        }


    }

    @Override
    public void editQuestions(String questionID, String val) {

        AddQ_frag addQ_frag = new AddQ_frag();
        Bundle b = new Bundle();
        b.putString("ID",questionID);
        b.putString("val",val);
        addQ_frag.setArguments(b);


        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right, R.anim.slide_in_right, R.anim.slide_out_left)
                .replace(R.id.Exam_Frame,addQ_frag)
                .addToBackStack(null)
                .commit();


    }

    @Override
    public void editSuccessopenBank() {

        onBackPressed();

    }




    @Override
    public void onClick(View view) {

        if (view == open_nav){
            //  لو عزت تغير الاتجاه من هنا بتاع فتح ال nav
            if (drawer.isDrawerOpen(Gravity.START)){

                drawer.closeDrawer(Gravity.START);

            }else {
                drawer.openDrawer(Gravity.START);
            }

        }

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){


            case R.id.emams:

                getSupportFragmentManager().popBackStack();
                getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right, R.anim.slide_in_right, R.anim.slide_out_left)
                        .replace(R.id.Exam_Frame,new ExamList()).addToBackStack(null)
                        .commit();

                break;
            case R.id.MYResult:

                getSupportFragmentManager().popBackStack();
                getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right, R.anim.slide_in_right, R.anim.slide_out_left)
                        .replace(R.id.Exam_Frame,new MyResults()).addToBackStack(null)
                        .commit();

                break;

            case R.id.studentManger:

                //  اداره الطلاب


                studentDialog = new StudentDialog(ControlPanel.this,R.style.PauseDialog,this);
                studentDialog.show();


//
//
//                getSupportFragmentManager().popBackStack();
//                getSupportFragmentManager()
//                        .beginTransaction()
//                        .setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right, R.anim.slide_in_right, R.anim.slide_out_left)
//                        .replace(R.id.Exam_Frame,new StudentManagement())
//                        .addToBackStack(null)
//                        .commit();

                
                break;

            case R.id.per:

                getSupportFragmentManager().popBackStack();
                getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right, R.anim.slide_in_right, R.anim.slide_out_left)
                        .replace(R.id.Exam_Frame,new PermissionsFromStudent()).addToBackStack(null)
                        .commit();


                break;

            case R.id.exit:

                final AlertDialog aleartDialog = new AlertDialog(this,getString(R.string.title),getString(R.string.message));
                aleartDialog.show();
                aleartDialog.btnYes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        aleartDialog.dismiss();
//
//                        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
//                        String uID                = firebaseAuth.getCurrentUser().getUid();

//                         حنتشيك علي الي في الداتا ولو صح حنطلع بره
                        animatedDialog.ShowDialog();
                        FirebaseMessaging.getInstance().unsubscribeFromTopic("all");

                        if (AreAdmin){

                            // notificationstoAdmin Mr.AhmedSamyFrom Students disable when SignOut
                            FirebaseMessaging.getInstance().unsubscribeFromTopic("Admins");
                        }

                        auth.signOut();
                        startActivity(new Intent(ControlPanel.this,MainActivity.class));
                        animatedDialog.Close_Dialog();
                        finish();

                    }
                });



                break;


            case R.id.questions:


                getSupportFragmentManager().popBackStack();
                getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right, R.anim.slide_in_right, R.anim.slide_out_left)
                        .replace(R.id.Exam_Frame,new Question_Bank_Frag())
                        .addToBackStack(null)
                        .commit();


                break;

            case R.id.results :

                getSupportFragmentManager().popBackStack();
                getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right, R.anim.slide_in_right, R.anim.slide_out_left)
                        .replace(R.id.Exam_Frame,new ExamsResults())
                        .addToBackStack(null)
                        .commit();
                break;

            case R.id.addExam :


                getSupportFragmentManager().popBackStack();
                getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right, R.anim.slide_in_right, R.anim.slide_out_left)
                        .replace(R.id.Exam_Frame,new addExam())
                        .addToBackStack(null)
                        .commit();
                break;

            case R.id.aboutDoctor :
                SetNavUnChecked();
                //getSupportFragmentManager().popBackStack();
                getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right, R.anim.slide_in_right, R.anim.slide_out_left)
                        .replace(R.id.Exam_Frame,new AboutDoctor())
                        .addToBackStack(null)
                        .commit();


                break;
            case R.id.aboutProgrammer :
                SetNavUnChecked();
                //getSupportFragmentManager().popBackStack();
                getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right, R.anim.slide_in_right, R.anim.slide_out_left)
                        .replace(R.id.Exam_Frame,new AboutProgrammer())
                        .addToBackStack(null)
                        .commit();


                break;

            case R.id.addnotification:

                 notificationDialog = new NotificationDialog(this,R.style.PauseDialog,this);
                notificationDialog.show();

                break;
        }
//        getSupportFragmentManager().popBackStack();   //finish
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(Gravity.START)){
            drawer.closeDrawer(Gravity.START);
        }else {
           super.onBackPressed();
        }

    }


    private void hideAdminToolsFromUsers()
    {

        Menu nav_Menu = navigation.getMenu();
        nav_Menu.findItem(R.id.addExam).setVisible(false);
        nav_Menu.findItem(R.id.questions).setVisible(false);
        nav_Menu.findItem(R.id.results).setVisible(false);
        nav_Menu.findItem(R.id.studentManger).setVisible(false);
        nav_Menu.findItem(R.id.per).setVisible(false);
        nav_Menu.findItem(R.id.addnotification).setVisible(false);


    }
    @Override
    public void AdminTools() {

        Menu nav_Menu = navigation.getMenu();
        nav_Menu.findItem(R.id.addExam).setVisible(true);
        nav_Menu.findItem(R.id.questions).setVisible(true);
        nav_Menu.findItem(R.id.results).setVisible(true);
        nav_Menu.findItem(R.id.studentManger).setVisible(true);
        nav_Menu.findItem(R.id.per).setVisible(true);
        nav_Menu.findItem(R.id.addnotification).setVisible(true);
        circleImageView.setBackgroundResource(R.drawable.ahmedsamy);
        if (!AreAdmin){

            AreAdmin = true;
        }

        FirebaseMessaging.getInstance().subscribeToTopic("Admins");
        nav_Menu.findItem(R.id.exit).setEnabled(true);


    }

    @Override
    public void UserTools() {

        Menu nav_Menu = navigation.getMenu();
        nav_Menu.findItem(R.id.addExam).setVisible(false);
        nav_Menu.findItem(R.id.questions).setVisible(false);
        nav_Menu.findItem(R.id.results).setVisible(false);
        nav_Menu.findItem(R.id.studentManger).setVisible(false);
        if (AreAdmin){

            AreAdmin = false;
        }

        nav_Menu.findItem(R.id.per).setVisible(false);
        nav_Menu.findItem(R.id.addnotification).setVisible(false);
        nav_Menu.findItem(R.id.exit).setEnabled(true);

    }

    @Override
    public void showRequestsFromStudent(String examID,String what,String nameExam) {


        RequestFromStudentToExamWhat requestFromStudentToExamWhat = new RequestFromStudentToExamWhat();
        switch (what){


            case "0":

                Bundle b = new Bundle();
                b.putString("examid", examID);
                b.putString("name",nameExam);
                requestFromStudentToExamWhat.setArguments(b);
                getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right)
                        .replace(R.id.Exam_Frame,requestFromStudentToExamWhat,"K")
                        .addToBackStack(null)
                        .commit();
                break;

            case "1":

                progressBar.setVisibility(View.INVISIBLE);
                getSupportFragmentManager().popBackStack();
                RequestFromStudentToExamWhat requestFromStudentToExamWhat1 = new RequestFromStudentToExamWhat();
                Bundle b1 = new Bundle();
                b1.putString("examid", examID);
                b1.putString("name",nameExam);
                requestFromStudentToExamWhat1.setArguments(b1);
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.Exam_Frame,requestFromStudentToExamWhat1)
                        .addToBackStack(null)
                        .commit();


                com.am.mohamedraslan.hossamexams.Dialog.AlertDialog alertDialog
                        = new com.am.mohamedraslan.hossamexams.Dialog.AlertDialog(this, "لقد تم تنفيذ طلبك بنجاح.");
                alertDialog.show();

                break;

        }




        // حنظهر ام الطلبات بتاعه الامتحان بقااااااااااااااا


    }

    @Override
    public void notificationMessages(String message,ProgressBar p1 , ProgressBar p2) {

        // send notification to allstudent //
        sendnotificationtoallUsers(message,p1,p2);


    }

    @Override
    public void showingresults() {


        Fragment fragment = getSupportFragmentManager().findFragmentByTag("K");

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.Exam_Frame,fragment,"K")
                .addToBackStack(null)
                .commit();



    }

    @Override
    public void tokenSussessfullystored() {
        //Toast.makeText(this, "التوكن اتخزن تمااااااااااام", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void problemwithtoken() {

       // Toast.makeText(this, "التوكن فيه مشكله مش عارف", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void tokenisExisitinFirebase() {

    }

    @Override
    public void tokennotExisitinFirebase() {



    }

    @Override
    public void userAreDeletedSussess() {
        animatedDialog.Close_Dialog();
        startActivity(new Intent(ControlPanel.this,MainActivity.class));
        finish();
    }

    @Override
    public void problemwithDeleteUser() {
        animatedDialog.Close_Dialog();
        Toast.makeText(this, "لقد حدثت مشكله اثناء الحذف.", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void SetUsername(String nameStudent) {
        UserName.setText( "Welcome,  "  + nameStudent );
    }
    public static void SetNavChecked(int position){

        navigation.getMenu().getItem(position).setChecked(true);

    }
    public static void SetNavUnChecked(){

        for (int position = 0 ; position < navigation.getMenu().size(); position++  ) {
            navigation.getMenu().getItem(position).setChecked(false);
        }

    }




    public void sendnotificationtoallUsers(String s, final ProgressBar p1, final ProgressBar p2) {


        JSONObject obj = null;
        JSONObject dataobjData = null;

        try {

            obj = new JSONObject();

            dataobjData = new JSONObject();
            dataobjData.put("image", "0");
            dataobjData.put("message",s);

            obj.put("to", "/topics/all");
            obj.put("data", dataobjData);

            Log.d("MYOBJs", obj.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST, "https://fcm.googleapis.com/fcm/send", obj,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e("SUCCESS", response + "");
                        NotificationDialog.edTextNot.setText("");
                        p1.setVisibility(View.GONE);
                        p2.setVisibility(View.GONE);
                        NotificationDialog.sendfeedback.setEnabled(true);
                        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                        Toast.makeText(ControlPanel.this, "لقد تم إرسال الإشعار بنجاح.", Toast.LENGTH_SHORT).show();
                        if (notificationDialog!=null){

                            notificationDialog.dismiss();

                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {


                        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                            Toast.makeText(ControlPanel.this, "تاكد من الإتصال بالإنترنت", Toast.LENGTH_SHORT).show();
                            NotificationDialog.sendfeedback.setEnabled(true);
                            p1.setVisibility(View.GONE);
                            p2.setVisibility(View.GONE);


                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<String, String>();
                headers.put("Authorization", "key=" + "AAAA0NknKjs:APA91bH8x30IaI5ZAz49kUGAOXEwjiFxZnWTpELAu2DMOu_vgz5GhNDnERYkv7X5Z-NveF02btyVdkMyHWhYH0wYTU3nqtbW9vx67M4Xv1vn7-rOisNEYixwQpeImD-7yguPEhTM_Nkk");
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };



            RequestQueue requestQueue = Volley.newRequestQueue(this);
            int socketTimeout = 1000 * 60;// 60 seconds
            RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            jsObjRequest.setRetryPolicy(policy);
            requestQueue.add(jsObjRequest);


    }

}
