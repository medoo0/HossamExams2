package com.example.mohamedraslan.hossamexams.Views;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mohamedraslan.hossamexams.Contracts.ControlPanelContract;
import com.example.mohamedraslan.hossamexams.Dialog.AlertDialog;
import com.example.mohamedraslan.hossamexams.Dialog.AnimatedDialog;
import com.example.mohamedraslan.hossamexams.Dialog.StudentDialog;
import com.example.mohamedraslan.hossamexams.Fragment.AboutDoctor;
import com.example.mohamedraslan.hossamexams.Fragment.AboutProgrammer;
import com.example.mohamedraslan.hossamexams.Fragment.AddQ_frag;
import com.example.mohamedraslan.hossamexams.Fragment.ExamList;
import com.example.mohamedraslan.hossamexams.Fragment.ExamsResults;
import com.example.mohamedraslan.hossamexams.Fragment.MyResults;
import com.example.mohamedraslan.hossamexams.Fragment.PermissionsFromStudent;
import com.example.mohamedraslan.hossamexams.Fragment.Question_Bank_Frag;
import com.example.mohamedraslan.hossamexams.Fragment.RequestFromStudentToExamWhat;
import com.example.mohamedraslan.hossamexams.Fragment.StudentManagement;
import com.example.mohamedraslan.hossamexams.Fragment.StudentsWrongs;
import com.example.mohamedraslan.hossamexams.Fragment.addExam;
import com.example.mohamedraslan.hossamexams.JsonModel.Result_Pojo;
import com.example.mohamedraslan.hossamexams.JsonModel.WorngQestion;
import com.example.mohamedraslan.hossamexams.MainPresnter.ControlpanelPresnter;
import com.example.mohamedraslan.hossamexams.Notifications.MyFirebaseMessagingService;
import com.example.mohamedraslan.hossamexams.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.List;
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
    AnimatedDialog animatedDialog;
    BroadcastReceiver broadcastReceiver;
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

    }
    @Override
    public void AdminTools() {

        Menu nav_Menu = navigation.getMenu();
        nav_Menu.findItem(R.id.addExam).setVisible(true);
        nav_Menu.findItem(R.id.questions).setVisible(true);
        nav_Menu.findItem(R.id.results).setVisible(true);
        nav_Menu.findItem(R.id.studentManger).setVisible(true);
        nav_Menu.findItem(R.id.per).setVisible(true);
        circleImageView.setBackgroundResource(R.drawable.ahmedsamy);

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
                break;

        }




        // حنظهر ام الطلبات بتاعه الامتحان بقااااااااااااااا


    }

    @Override
    public void showingresults() {


        Fragment fragment = getSupportFragmentManager().findFragmentByTag("K");

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.Exam_Frame,fragment,"K")
                .addToBackStack(null)
                .commit();
        android.app.AlertDialog.Builder builder1b = new android.app.AlertDialog.Builder(this);
        builder1b.setMessage("يستطيع الطالب من الان دخول الإختبار.");
        builder1b.setCancelable(true);
        builder1b.show();


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
}
