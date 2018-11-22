package com.example.mohamedraslan.hossamexams.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Toast;

import com.example.mohamedraslan.hossamexams.Contracts.MyResultContract;
import com.example.mohamedraslan.hossamexams.Contracts.PermissionExamsContract;
import com.example.mohamedraslan.hossamexams.JsonModel.FullRegisterForm;
import com.example.mohamedraslan.hossamexams.JsonModel.PermissionUserEntering;
import com.example.mohamedraslan.hossamexams.JsonModel.Permission_Refrence;
import com.example.mohamedraslan.hossamexams.JsonModel.Result_Pojo;
import com.example.mohamedraslan.hossamexams.R;

import java.util.ArrayList;
import java.util.List;

public class PermissionExamsAdapter extends RecyclerView.Adapter<Permision_Holder> {


    List<Permission_Refrence> Result,listnew;
    Context context;
    PermissionExamsContract.viewMain viewMainmain;


    public PermissionExamsAdapter(List<Permission_Refrence> result, Context context, PermissionExamsContract.viewMain viewMainmain) {

        Result       = result;
        this.context = context;
        this.viewMainmain = viewMainmain;
    }


    @NonNull
    @Override
    public Permision_Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.permission_rec_shape,parent,false);
        return new Permision_Holder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final Permision_Holder holder, final int position) {

        holder.animates();
        holder.ExamNamerequest.setText(Result.get(position).getExamName());

        holder.buttongetallRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                viewMainmain.ApplicationForExams(Result.get(position).getExamID());

            }
        });

    }

    @Override
    public int getItemCount() {
        return Result.size();
    }




    public Filter getFilter() {




        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

                FilterResults results = new FilterResults();        // Holds the results of a filtering operation in values
                ArrayList<Permission_Refrence> FilteredArrList = new ArrayList<>();
                if (listnew == null) {

                    listnew = new ArrayList<>(Result); // saves the original data in mOriginalValues

                }

                if (constraint == null || constraint.length() == 0) {

                    // set the Original result to return
                    results.count = listnew.size();
                    results.values = listnew;
                } else {
                    constraint = constraint.toString().toLowerCase();
                    for (int i = 0; i < listnew.size(); i++) {
                        String data = listnew.get(i).getExamName();
                        if (data.toLowerCase().startsWith(constraint.toString())) {
                            FilteredArrList.add(listnew.get(i));
                        }
                    }
                    // set the Filtered result to return
                    results.count = FilteredArrList.size();
                    results.values = FilteredArrList;
                }
                return results;
            }


            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {

                Result = (ArrayList<Permission_Refrence>) results.values; // has the filtered values
                notifyDataSetChanged();

            }
        };
        return filter;
    }

}