package com.jerryjin.fastpermission;

import android.Manifest;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jerryjin.fastpermissionlib.permission.PermissionBean;
import com.jerryjin.fastpermissionlib.permission.PermissionHelper;

import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link BlankFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link BlankFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BlankFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    public BlankFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BlankFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BlankFragment newInstance(String param1, String param2) {
        BlankFragment fragment = new BlankFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View root = inflater.inflate(R.layout.frag_permission, container, false);

        root.findViewById(R.id.frag_permission)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        PermissionHelper
                                .getInstance(BlankFragment.this)
                                .setTitle("请求授权")
                                .setPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                        Manifest.permission.READ_EXTERNAL_STORAGE,
                                        Manifest.permission.RECORD_AUDIO,
                                        Manifest.permission.ACCESS_NETWORK_STATE})
                                .withCustomDescriptions(
                                        new String[]{
                                                PermissionHelper
                                                        .getInstance()
                                                        .searchForPermissionDescription(Manifest.permission.ACCESS_NETWORK_STATE),
                                                "Huge Pe"},
                                        new int[]{0, 2})
                                //.cancelCustomDescriptions()
                                .setOnRequestPermissionCallback(new PermissionHelper.PermissionCallbackImpl() {
                                    @Override
                                    public void onRequestPermissionsResult(List<PermissionBean> permissionBeans) {
                                        super.onRequestPermissionsResult(permissionBeans);
                                        Log.e("CALLBACK111", permissionBeans.toString());
                                    }
                                })
                                .show();
                    }
                });
        return root;
    }


}
