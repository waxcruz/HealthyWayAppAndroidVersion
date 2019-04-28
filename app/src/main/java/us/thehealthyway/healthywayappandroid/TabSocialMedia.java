package us.thehealthyway.healthywayappandroid;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;


import static us.thehealthyway.healthywayappandroid.AppData.DEBUG;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link TabSocialMedia.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link TabSocialMedia#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TabSocialMedia extends Fragment {
    private static final String TAG = "HW.TabSocialMedia";
    
    // UI references
    private ImageButton link_to_healthy_way;
    private ImageButton link_facebook;
    private ImageButton link_instagram;
    private ImageButton link_you_tube;
    private ImageButton link_pinterest;

    // link
    private String show_link;
    
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public TabSocialMedia() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TabSocialMedia.
     */
    // TODO: Rename and change types and number of parameters
    public static TabSocialMedia newInstance(String param1, String param2) {
        TabSocialMedia fragment = new TabSocialMedia();
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
        View view;
        view = inflater.inflate(R.layout.fragment_social_media_tab, container, false);
         link_to_healthy_way = (ImageButton) view.findViewById(R.id.link_to_healthy_way);
         link_to_healthy_way.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 show_link = HW_Links.healthyWay;
                 display_social_media(show_link);
             }
         });
         link_facebook = (ImageButton) view.findViewById(R.id.link_facebook);
         link_facebook.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 show_link = HW_Links.facebook;
                 display_social_media(show_link);
             }
         });
         link_instagram = (ImageButton) view.findViewById(R.id.link_instagram);
         link_instagram.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 show_link = HW_Links.instagram;
                 display_social_media(show_link);
             }
         });
         link_you_tube = (ImageButton) view.findViewById(R.id.link_you_tube);
         link_you_tube.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 show_link = HW_Links.youTube;
                 display_social_media(show_link);
             }
         });
         link_pinterest = (ImageButton) view.findViewById(R.id.link_pinterest);
         link_pinterest.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 show_link = HW_Links.pinterest;
                 display_social_media(show_link);
             }
         });
        
        
        
        
        
        
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(HW_Enumerations.TabNames.TAB_SOCIAL_MEDIA, HW_Enumerations.SubTabNames.SUB_TAB_LEVEL_0);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(HW_Enumerations.TabNames tabIndex, HW_Enumerations.SubTabNames subTabIndex);
    }

    void display_social_media(String url) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(browserIntent);
    }

}
