package com.am.mohamedraslan.hossamexams.MainModle;

import android.support.annotation.NonNull;

import com.am.mohamedraslan.hossamexams.Contracts.RequestFromStudentToExamWhatContract;
import com.am.mohamedraslan.hossamexams.Enums.DataBase_Refrences;
import com.am.mohamedraslan.hossamexams.JsonModel.PermissionUserEntering;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class RequestFromStudentToExamWhatModel implements RequestFromStudentToExamWhatContract.MainModel {

    RequestFromStudentToExamWhatContract.MainPresnter mainPresnter;

    public RequestFromStudentToExamWhatModel(RequestFromStudentToExamWhatContract.MainPresnter mainPresnter) {


        this.mainPresnter = mainPresnter;

    }

    @Override
    public void getStudents(String examID) {


        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference reference = firebaseDatabase.getReference(DataBase_Refrences.Permissions.getRef());
        reference.child(examID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                if (dataSnapshot.exists()){

                    HashMap<String, PermissionUserEntering> results = dataSnapshot.getValue(new GenericTypeIndicator<HashMap<String, PermissionUserEntering>>() {});
                    List<PermissionUserEntering> toutourial         = new ArrayList<>(Objects.requireNonNull(results).values());
                    mainPresnter.studentExisits(toutourial);


                }else {

                    mainPresnter.studentNotExisits();

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
