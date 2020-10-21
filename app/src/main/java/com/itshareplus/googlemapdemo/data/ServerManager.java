package com.itshareplus.googlemapdemo.data;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import Modules.Route;

import static android.content.ContentValues.TAG;

public class ServerManager {
    private static ServerManager instance;
//    private FirebaseFirestore db;
    private FirebaseDatabase database;

    public static ServerManager getInstance() {
        if(instance==null)
            instance = new ServerManager();
        return instance;
    }
    public void putData(List<Route> routes, final IServerManagerPutData iServerManagerPutData){
        if(database==null) {
//            db = FirebaseFirestore.getInstance();
            database = FirebaseDatabase.getInstance();
        }

        /**
         * Từ điểm A-B sẽ có nhiều steps
         * mỗi step sẽ có nhiều points
         * Dưới đây mỗi km sẽ gọi là 1 step và trong đoạn đường đó sẽ có các point là Lat/Lng
         *  0.2 km
         * 2020-06-26 19:54:39.773 6130-6130/com.itshareplus.googlemapdemo D/nhatnhat: putData: lat/lng: (10.80129,106.71447)
         * 2020-06-26 19:54:39.773 6130-6130/com.itshareplus.googlemapdemo D/nhatnhat: putData: lat/lng: (10.80139,106.71408)
         * 2020-06-26 19:54:39.774 6130-6130/com.itshareplus.googlemapdemo D/nhatnhat: putData: lat/lng: (10.80155,106.71338)
         * 2020-06-26 19:54:39.774 6130-6130/com.itshareplus.googlemapdemo D/nhatnhat: putData: lat/lng: (10.80156,106.71336)
         * 2020-06-26 19:54:39.774 6130-6130/com.itshareplus.googlemapdemo D/nhatnhat: putData: lat/lng: (10.80157,106.71329)
         * 2020-06-26 19:54:39.775 6130-6130/com.itshareplus.googlemapdemo D/nhatnhat: putData: lat/lng: (10.80157,106.71326)
         * 2020-06-26 19:54:39.775 6130-6130/com.itshareplus.googlemapdemo D/nhatnhat: putData: lat/lng: (10.80156,106.71318)
         * 2020-06-26 19:54:39.776 6130-6130/com.itshareplus.googlemapdemo D/nhatnhat: putData: lat/lng: (10.80153,106.71306)
         * 0.2 km
         * 2020-06-26 19:54:39.777 6130-6130/com.itshareplus.googlemapdemo D/nhatnhat: putData: lat/lng: (10.80153,106.71306)
         * 2020-06-26 19:54:39.777 6130-6130/com.itshareplus.googlemapdemo D/nhatnhat: putData: lat/lng: (10.80156,106.71254)
         * 2020-06-26 19:54:39.782 6130-6130/com.itshareplus.googlemapdemo D/nhatnhat: putData: lat/lng: (10.80158,106.71222)
         * 2020-06-26 19:54:39.787 6130-6130/com.itshareplus.googlemapdemo D/nhatnhat: putData: lat/lng: (10.8016,106.71193)
         * 2020-06-26 19:54:39.788 6130-6130/com.itshareplus.googlemapdemo D/nhatnhat: putData: lat/lng: (10.80156,106.71148)
         * 1.7 km
         * 2020-06-26 19:54:39.789 6130-6130/com.itshareplus.googlemapdemo D/nhatnhat: putData: lat/lng: (10.80156,106.71148)
         * 2020-06-26 19:54:39.789 6130-6130/com.itshareplus.googlemapdemo D/nhatnhat: putData: lat/lng: (10.80157,106.71146)
         * 2020-06-26 19:54:39.790 6130-6130/com.itshareplus.googlemapdemo D/nhatnhat: putData: lat/lng: (10.80159,106.71144)
         * 2020-06-26 19:54:39.790 6130-6130/com.itshareplus.googlemapdemo D/nhatnhat: putData: lat/lng: (10.8016,106.71142)
         * 2020-06-26 19:54:39.791 6130-6130/com.itshareplus.googlemapdemo D/nhatnhat: putData: lat/lng: (10.80161,106.71141)
         * 2020-06-26 19:54:39.791 6130-6130/com.itshareplus.googlemapdemo D/nhatnhat: putData: lat/lng: (10.80162,106.71138)
         * 2020-06-26 19:54:39.791 6130-6130/com.itshareplus.googlemapdemo D/nhatnhat: putData: lat/lng: (10.80162,106.71136)
         * 2020-06-26 19:54:39.792 6130-6130/com.itshareplus.googlemapdemo D/nhatnhat: putData: lat/lng: (10.80163,106.71135)
         * 2020-06-26 19:54:39.797 6130-6130/com.itshareplus.googlemapdemo D/nhatnhat: putData: lat/lng: (10.80163,106.71132)
         * 2020-06-26 19:54:39.798 6130-6130/com.itshareplus.googlemapdemo D/nhatnhat: putData: lat/lng: (10.80162,106.71129)
         * 2020-06-26 19:54:39.798 6130-6130/com.itshareplus.googlemapdemo D/nhatnhat: putData: lat/lng: (10.80161,106.71128)
         * 2020-06-26 19:54:39.803 6130-6130/com.itshareplus.googlemapdemo D/nhatnhat: putData: lat/lng: (10.80161,106.71127)
         * 2020-06-26 19:54:39.803 6130-6130/com.itshareplus.googlemapdemo D/nhatnhat: putData: lat/lng: (10.8016,106.71126)
         * 2020-06-26 19:54:39.804 6130-6130/com.itshareplus.googlemapdemo D/nhatnhat: putData: lat/lng: (10.80159,106.71125)
         * 2020-06-26 19:54:39.805 6130-6130/com.itshareplus.googlemapdemo D/nhatnhat: putData: lat/lng: (10.80158,106.71124)
         * 2020-06-26 19:54:39.806 6130-6130/com.itshareplus.googlemapdemo D/nhatnhat: putData: lat/lng: (10.80158,106.71123)
         * 2020-06-26 19:54:39.806 6130-6130/com.itshareplus.googlemapdemo D/nhatnhat: putData: lat/lng: (10.80157,106.71123)
         * 2020-06-26 19:54:39.807 6130-6130/com.itshareplus.googlemapdemo D/nhatnhat: putData: lat/lng: (10.80156,106.71122)
         * 2020-06-26 19:54:39.808 6130-6130/com.itshareplus.googlemapdemo D/nhatnhat: putData: lat/lng: (10.80148,106.71061)
         * 2020-06-26 19:54:39.808 6130-6130/com.itshareplus.googlemapdemo D/nhatnhat: putData: lat/lng: (10.80139,106.71015)
         * 2020-06-26 19:54:39.809 6130-6130/com.itshareplus.googlemapdemo D/nhatnhat: putData: lat/lng: (10.80132,106.70979)
         * 2020-06-26 19:54:39.810 6130-6130/com.itshareplus.googlemapdemo D/nhatnhat: putData: lat/lng: (10.80131,106.70973)
         * 2020-06-26 19:54:39.816 6130-6130/com.itshareplus.googlemapdemo D/nhatnhat: putData: lat/lng: (10.80124,106.70952)
         * 2020-06-26 19:54:39.817 6130-6130/com.itshareplus.googlemapdemo D/nhatnhat: putData: lat/lng: (10.8011,106.7092)
         * 2020-06-26 19:54:39.818 6130-6130/com.itshareplus.googlemapdemo D/nhatnhat: putData: lat/lng: (10.80102,106.70889)
         * 2020-06-26 19:54:39.819 6130-6130/com.itshareplus.googlemapdemo D/nhatnhat: putData: lat/lng: (10.80095,106.70871)
         * 2020-06-26 19:54:39.820 6130-6130/com.itshareplus.googlemapdemo D/nhatnhat: putData: lat/lng: (10.80091,106.70859)
         * 2020-06-26 19:54:39.823 6130-6130/com.itshareplus.googlemapdemo D/nhatnhat: putData: lat/lng: (10.80069,106.70813)
         * 2020-06-26 19:54:39.823 6130-6130/com.itshareplus.googlemapdemo D/nhatnhat: putData: lat/lng: (10.80056,106.70789)
         * 2020-06-26 19:54:39.825 6130-6130/com.itshareplus.googlemapdemo D/nhatnhat: putData: lat/lng: (10.80028,106.7074)
         * 2020-06-26 19:54:39.826 6130-6130/com.itshareplus.googlemapdemo D/nhatnhat: putData: lat/lng: (10.79992,106.70678)
         * 2020-06-26 19:54:39.829 6130-6130/com.itshareplus.googlemapdemo D/nhatnhat: putData: lat/lng: (10.79972,106.70651)
         * 2020-06-26 19:54:39.830 6130-6130/com.itshareplus.googlemapdemo D/nhatnhat: putData: lat/lng: (10.79932,106.706)
         * 2020-06-26 19:54:39.833 6130-6130/com.itshareplus.googlemapdemo D/nhatnhat: putData: lat/lng: (10.79908,106.70571)
         * 2020-06-26 19:54:39.834 6130-6130/com.itshareplus.googlemapdemo D/nhatnhat: putData: lat/lng: (10.79903,106.70566)
         * 2020-06-26 19:54:39.834 6130-6130/com.itshareplus.googlemapdemo D/nhatnhat: putData: lat/lng: (10.7989,106.70553)
         * 2020-06-26 19:54:39.835 6130-6130/com.itshareplus.googlemapdemo D/nhatnhat: putData: lat/lng: (10.79857,106.70521)
         * 2020-06-26 19:54:39.835 6130-6130/com.itshareplus.googlemapdemo D/nhatnhat: putData: lat/lng: (10.79837,106.70503)
         * 2020-06-26 19:54:39.836 6130-6130/com.itshareplus.googlemapdemo D/nhatnhat: putData: lat/lng: (10.79742,106.70415)
         * 2020-06-26 19:54:39.836 6130-6130/com.itshareplus.googlemapdemo D/nhatnhat: putData: lat/lng: (10.7965,106.70328)
         * 2020-06-26 19:54:39.838 6130-6130/com.itshareplus.googlemapdemo D/nhatnhat: putData: lat/lng: (10.79637,106.70316)
         * 2020-06-26 19:54:39.839 6130-6130/com.itshareplus.googlemapdemo D/nhatnhat: putData: lat/lng: (10.79584,106.70266)
         * 2020-06-26 19:54:39.840 6130-6130/com.itshareplus.googlemapdemo D/nhatnhat: putData: lat/lng: (10.79571,106.70254)
         * 2020-06-26 19:54:39.841 6130-6130/com.itshareplus.googlemapdemo D/nhatnhat: putData: lat/lng: (10.79553,106.70237)
         * 2020-06-26 19:54:39.842 6130-6130/com.itshareplus.googlemapdemo D/nhatnhat: putData: lat/lng: (10.79526,106.70211)
         * 2020-06-26 19:54:39.843 6130-6130/com.itshareplus.googlemapdemo D/nhatnhat: putData: lat/lng: (10.79512,106.70197)
         * 2020-06-26 19:54:39.844 6130-6130/com.itshareplus.googlemapdemo D/nhatnhat: putData: lat/lng: (10.79475,106.70162)
         * 2020-06-26 19:54:39.844 6130-6130/com.itshareplus.googlemapdemo D/nhatnhat: putData: lat/lng: (10.79468,106.70156)
         * 2020-06-26 19:54:39.845 6130-6130/com.itshareplus.googlemapdemo D/nhatnhat: putData: lat/lng: (10.79455,106.70144)
         * 2020-06-26 19:54:39.845 6130-6130/com.itshareplus.googlemapdemo D/nhatnhat: putData: lat/lng: (10.79446,106.70136)
         * 2020-06-26 19:54:39.846 6130-6130/com.itshareplus.googlemapdemo D/nhatnhat: putData: lat/lng: (10.79393,106.70094)
         * 2020-06-26 19:54:39.846 6130-6130/com.itshareplus.googlemapdemo D/nhatnhat: putData: lat/lng: (10.79376,106.70073)
         * 2020-06-26 19:54:39.846 6130-6130/com.itshareplus.googlemapdemo D/nhatnhat: putData: lat/lng: (10.7937,106.70065)
         * 2020-06-26 19:54:39.847 6130-6130/com.itshareplus.googlemapdemo D/nhatnhat: putData: lat/lng: (10.79363,106.70058)
         * 2020-06-26 19:54:39.848 6130-6130/com.itshareplus.googlemapdemo D/nhatnhat: putData: lat/lng: (10.79312,106.70013)
         * 2020-06-26 19:54:39.848 6130-6130/com.itshareplus.googlemapdemo D/nhatnhat: putData: lat/lng: (10.79306,106.70008)
         * 2020-06-26 19:54:39.849 6130-6130/com.itshareplus.googlemapdemo D/nhatnhat: putData: lat/lng: (10.793,106.70003)
         * 2020-06-26 19:54:39.850 6130-6130/com.itshareplus.googlemapdemo D/nhatnhat: putData: lat/lng: (10.79286,106.69988)
         * 2020-06-26 19:54:39.851 6130-6130/com.itshareplus.googlemapdemo D/nhatnhat: putData: lat/lng: (10.79275,106.69976)
         * 2020-06-26 19:54:39.853 6130-6130/com.itshareplus.googlemapdemo D/nhatnhat: putData: lat/lng: (10.79261,106.6996)
         */
        for (int i = 0; i <routes.size() ; i++) {
            // Từ A-B sẽ có nhiều steps và các steps sẽ có nhiều points
            String data = "";
            for (Route routeStep : routes.get(i).steps) {
                data += "{\"lat\":" + routeStep.startLocation.latitude + "," + "\"lon\":" + routeStep.startLocation.longitude + "," + "\"angle\":" +
                        routeStep.angle + "," + "\"dis\":" + routeStep.distance.value + "}#";
//                for (LatLng pointStep:routeStep.points) {
//                    data+="_"+pointStep.latitude+"@"+pointStep.longitude;
//                }
            }
            DatabaseReference myRef = database.getReference("path").child("route" + i);
            myRef.setValue(data).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    iServerManagerPutData.OnPutDataSuccess();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    iServerManagerPutData.OnPutDataFail(e.toString());
                }
            });
        }
//            data+="_"+routes.get(i).endLocation.latitude+"@"+routes.get(i).endLocation.longitude;
//            Map<String,String> map = new HashMap<>();
//            map.put("Data",data);
//            db.collection("Path").document("route"+i).set(map).addOnSuccessListener(new OnSuccessListener<Void>() {
//                @Override
//                public void onSuccess(Void aVoid) {
//                    iServerManagerPutData.OnPutDataSuccess();
//                }
//            }).addOnFailureListener(new OnFailureListener() {
//                @Override
//                public void onFailure(@NonNull Exception e) {
//                    iServerManagerPutData.OnPutDataFail(e.toString());
//                }
//            });
//            }


    }

    public void getData(final IServerManagerGetData iServerManagerGetData){
//        final List<String> listData = new ArrayList<>();
//        if(db==null)
//            db = FirebaseFirestore.getInstance();
//        /**
//         * Read data bình thường
//         */
////        db.collection("Path").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
////            @Override
////            public void onComplete(@NonNull Task<QuerySnapshot> task) {
////                if (task.isSuccessful()) {
////                    for (QueryDocumentSnapshot document : task.getResult()) {
////                        listData.add(document.get("Data").toString());
////                        Log.d(TAG, "onComplete: "+document.get("Data").toString());
////                    }
////                    iServerManagerGetData.OnGetDataSuccess(listData);
////                } else {
////                    iServerManagerGetData.OnGetDataFail( task.getException().toString());
////                }
////            }
////        });
//        /**
//         * Read data realtime
//         */
//        db.collection("Path").addSnapshotListener(new EventListener<QuerySnapshot>() {
//            @Override
//            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
//                for (DocumentSnapshot document:queryDocumentSnapshots.getDocuments()) {
//                    listData.add(document.get("Data").toString());
//                    Log.d(TAG, "onEvent: "+document.get("Data").toString());
//                }
//                if(listData.size()>0){
//                    iServerManagerGetData.OnGetDataSuccess(listData);
//                }else {
//                    iServerManagerGetData.OnGetDataFail(e.toString());
//                }
//            }
//        });
    }


    public interface IServerManagerPutData{
        void OnPutDataSuccess();
        void OnPutDataFail(String error);
    }
    public interface IServerManagerGetData{
        void OnGetDataSuccess(List<String> data);
        void OnGetDataFail(String error);
    }

}
