package com.digizone.chrisarbe.musicproject;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import at.huber.youtubeExtractor.VideoMeta;
import at.huber.youtubeExtractor.YouTubeExtractor;
import at.huber.youtubeExtractor.YtFile;

import android.widget.ArrayAdapter;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MusicaYoutube.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MusicaYoutube#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MusicaYoutube extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    String PLACES_URL;
    String LOG_TAG;

    String[] values = new String[]{};
    String[] arr = {};

    static final String YOUTUBE_DATA_API_KEY = "AIzaSyCq8ylFId73K13bHZD6HxvWjEJOlsYQULI";
    String youtubeLink;
    public static EditText campoSearch;

    String terminoBusqueda;
    private LinearLayout mainLayout;
    private ProgressBar mainProgressBar;

    private InterstitialAd mInterstitialAd;

    public MainActivity variable = new MainActivity();

    public static FloatingActionButton fab;

    public static FloatingActionButton home;

    public AlertDialog dialogSearch;

    private ProgressDialog pDialog;

    private android.app.AlertDialog.Builder builder;

    Dialog customDialog = null;

    private List<YtFragmentedVideo> formatsToShowList;

    private static final int ITAG_FOR_AUDIO = 140;


    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1 ;

    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1 ;



    public MusicaYoutube() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MusicaYoutube.
     */
    // TODO: Rename and change types and number of parameters
    public static MusicaYoutube newInstance(String param1, String param2) {
        MusicaYoutube fragment = new MusicaYoutube();
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

        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            } else {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
            }
        }

        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
            } else {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
            }
        }

        pDialog = new ProgressDialog(getContext());
        pDialog.setCancelable(false);
        builder = new android.app.AlertDialog.Builder(getContext());

        mInterstitialAd = new InterstitialAd(getContext());
        mInterstitialAd.setAdUnitId("ca-app-pub-8744365861161319/1978590729");
        //mInterstitialAd.loadAd(new AdRequest.Builder().build());
        mInterstitialAd.loadAd(new AdRequest.Builder().addTestDevice("DC4FDD8F9668C1895E13BF225BFC8268").build());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View rootView = inflater.inflate(R.layout.fragment_musica_youtube, container, false);
        //busquedaRate();

        fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogSearch = createSearhDialog();
                dialogSearch.show();
                if (mInterstitialAd.isLoaded()) {
                    mInterstitialAd.show();
                } else {
                    Log.d("TAG", "The interstitial wasn't loaded yet.");
                }
            }
        });

        home = (FloatingActionButton) rootView.findViewById(R.id.home);
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = null;
                fragment = new Home();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.Contenedor, fragment).commit();
            }
        });

        mainLayout = (LinearLayout) rootView.findViewById(R.id.main_layout);
        mainLayout.setVisibility(View.GONE);

        dialogSearch = createSearhDialog();
        dialogSearch.show();

        return rootView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
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
        void onFragmentInteraction(Uri uri);
    }

    public void busquedaRate () {
        PLACES_URL = "https://www.googleapis.com/youtube/v3/search?part=snippet,id&maxResults=20&key=AIzaSyA5SjEcYREnw0bFHeZPa21wKAr6sox5j3s";
        LOG_TAG = "VolleyPlacesRemoteDS";

        // Instantiate the RequestQueue
        RequestQueue requestQueue = Volley.newRequestQueue(getContext().getApplicationContext());

        //Prepare the Request
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET, //GET or POST
                PLACES_URL, //URL
                null, //Parameters
                new Response.Listener<JSONObject>() { //Listener OK

                    @Override
                    public void onResponse(JSONObject responsePlaces) {
                        //Response OK!! :)
                        try {
                            //JSONObject resultado = responsePlaces.getJSONObject("items");
                            //String cadena = responsePlaces.getString("items").substring(1, responsePlaces.getString("items").length() - 1);
                            //JSONObject result2 = new JSONObject(cadena);
                            JSONArray the_json_array = responsePlaces.getJSONArray("items");
                            Log.i("Este es el tamano ",the_json_array.toString());
                            int size = the_json_array.length();
                            Log.i("Este es el tamano",Integer.toString(size));
                            values = new String[]{};
                            List<String> listFromArray = Arrays.asList(values);
                            List<String> tempList = new ArrayList<String>(listFromArray);
                            arr = Arrays.copyOf(arr, 20); // new size will be 10 elements
                            for (int i = 0; i < size; i++) {
                                JSONObject another_json_object = the_json_array.getJSONObject(i);
                                JSONObject result3 = new JSONObject(another_json_object.getString("snippet"));
                                tempList.add(result3.getString("title"));
                                JSONObject result4 = new JSONObject(another_json_object.getString("id"));
                                arr[i] = result4.getString("videoId");
                            }
                            Log.d(LOG_TAG,responsePlaces.toString());
                            String[] tempArray = new String[tempList.size()];
                            values = tempList.toArray(tempArray);
                            listaVideos();
                        } catch (JSONException e) {
                            // JSON error
                            e.printStackTrace();
                            Toast.makeText(getContext().getApplicationContext(), "JSON Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                }, new Response.ErrorListener() { //Listener ERROR

            @Override
            public void onErrorResponse(VolleyError error) {
                //There was an error :(
                Log.d(LOG_TAG,error.toString());
            }
        });

        //Send the request to the requestQueue
        requestQueue.add(request);
    }

    public void listaVideos () {

        pDialog.setMessage("Cargando Videos ...");
        showDialog();

        final ListView milista = (ListView)getView().findViewById(R.id.listaInicial);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_expandable_list_item_1, values){
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view =super.getView(position, convertView, parent);

                TextView textView=(TextView) view.findViewById(android.R.id.text1);


                textView.setTextColor(Color.WHITE);

                return view;
            }
        };

        milista.setAdapter(adapter);

        mainLayout = (LinearLayout) getView().findViewById(R.id.main_layout);
        mainLayout.setVisibility(View.GONE);

        milista.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            public void onItemClick(AdapterView<?> parent, View view, int position, long id){
                int item = position;
                String itemval = (String)milista.getItemAtPosition(position);
                YoutubeActivity.YOUTUBE_VIDEO_ID = arr[item];

                youtubeLink = "https://www.youtube.com/watch?v=";

                youtubeLink = youtubeLink + arr[item];

                customDialog = new Dialog(getContext(),R.style.Theme_AppCompat_DayNight_Dialog);
                customDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                //customDialog.setCancelable(false);
                customDialog.setContentView(R.layout.dialog_option);

                TextView titulo = (TextView) customDialog.findViewById(R.id.titulo);
                titulo.setText("Opciones de Video");

                TextView contenido = (TextView) customDialog.findViewById(R.id.contenido);
                contenido.setText("¿Qué deseas hacer?");

                ((Button) customDialog.findViewById(R.id.aceptar)).setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view)
                    {
                        customDialog.dismiss();
                        if (mInterstitialAd.isLoaded()) {
                            mInterstitialAd.show();
                        } else {
                            Log.d("TAG", "The interstitial wasn't loaded yet.");
                        }
                        startActivity(new Intent(getActivity(), YoutubeActivity.class));
                    }
                });

                ((Button) customDialog.findViewById(R.id.cancelar)).setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view)
                    {
                        customDialog.dismiss();
                        if (youtubeLink != null && (youtubeLink.contains("://youtu.be/") || youtubeLink.contains("youtube.com/watch?v="))) {
                            getYoutubeDownloadUrl(youtubeLink);
                        } else {

                        }
                    }
                });

                customDialog.show();


























                /*
                int item = position;
                String itemval = (String)milista.getItemAtPosition(position);
                YoutubeActivity.YOUTUBE_VIDEO_ID = arr[item];

                youtubeLink = youtubeLink + arr[item];

                if (youtubeLink != null && (youtubeLink.contains("://youtu.be/") || youtubeLink.contains("youtube.com/watch?v="))) {
                    // We have a valid link
                    getYoutubeDownloadUrl(youtubeLink);
                    //Toast.makeText(getContext(), "Entre al if", Toast.LENGTH_LONG).show();
                } else {
                    //Toast.makeText(getContext(), "Entre al else", Toast.LENGTH_LONG).show();
                    //finish();
                }*/
                //startActivity(new Intent(MainActivity.this, YoutubeActivity.class));
                //Toast.makeText(getApplicationContext(), "Position: "+ item+" - Valor: "+itemval, Toast.LENGTH_LONG).show();
            }
        });
        hideDialog();
    }

    public void busquedaVideos (String busqueda) {
        PLACES_URL = "https://www.googleapis.com/youtube/v3/search?part=snippet,id&maxResults=50&q="+busqueda+"&type=video&key=AIzaSyA5SjEcYREnw0bFHeZPa21wKAr6sox5j3s";
        LOG_TAG = "VolleyPlacesRemoteDS";

        // Instantiate the RequestQueue
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());

        //Prepare the Request
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET, //GET or POST
                PLACES_URL, //URL
                null, //Parameters
                new Response.Listener<JSONObject>() { //Listener OK

                    @Override
                    public void onResponse(JSONObject responsePlaces) {
                        //Response OK!! :)
                        try {
                            //JSONObject resultado = responsePlaces.getJSONObject("items");
                            //String cadena = responsePlaces.getString("items").substring(1, responsePlaces.getString("items").length() - 1);
                            //JSONObject result2 = new JSONObject(cadena);
                            JSONArray the_json_array = responsePlaces.getJSONArray("items");
                            Log.i("Este es el tamano ",the_json_array.toString());
                            int size = the_json_array.length();
                            Log.i("Este es el tamano",Integer.toString(size));
                            values = new String[]{};
                            List<String> listFromArray = Arrays.asList(values);
                            List<String> tempList = new ArrayList<String>(listFromArray);
                            arr = Arrays.copyOf(arr, 50); // new size will be 10 elements
                            for (int i = 0; i < size; i++) {
                                JSONObject another_json_object = the_json_array.getJSONObject(i);
                                JSONObject result3 = new JSONObject(another_json_object.getString("snippet"));
                                tempList.add(result3.getString("title"));
                                JSONObject result4 = new JSONObject(another_json_object.getString("id"));
                                arr[i] = result4.getString("videoId");
                                JSONObject result5 = new JSONObject(result3.getString("thumbnails"));
                                JSONObject result6 = new JSONObject(result5.getString("default"));
                                //Toast.makeText(getContext(), "Hola: "+ result6.getString("url"), Toast.LENGTH_LONG).show();
                            }
                            Log.d(LOG_TAG,responsePlaces.toString());
                            String[] tempArray = new String[tempList.size()];
                            values = tempList.toArray(tempArray);
                            listaVideos();
                        } catch (JSONException e) {
                            // JSON error
                            e.printStackTrace();
                            Toast.makeText(getContext().getApplicationContext(), "JSON Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                }, new Response.ErrorListener() { //Listener ERROR

            @Override
            public void onErrorResponse(VolleyError error) {
                //There was an error :(
                Log.d(LOG_TAG,error.toString());
            }
        });

        //Send the request to the requestQueue
        requestQueue.add(request);
    }

    public AlertDialog createSearhDialog() {

        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        LayoutInflater inflater = this.getLayoutInflater();

        View v = inflater.inflate(R.layout.fragment_search_music, null);

        campoSearch = (EditText) v.findViewById(R.id.campoBusqueda);

        builder.setView(v).setNegativeButton("Cancelar",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

        builder.setView(v).setPositiveButton("Buscar",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String termino = campoSearch.getText().toString();
                        termino = termino.replace(" ", "+");
                        terminoBusqueda = termino;
                        //Toast.makeText(getApplicationContext(), "Termino : "+ termino, Toast.LENGTH_LONG).show();
                        busquedaVideos(termino);
                    }
                });
        return builder.create();
    }

    private void getYoutubeDownloadUrl(String youtubeLink) {
        mainLayout = (LinearLayout) getView().findViewById(R.id.main_layout);
        mainLayout.removeAllViews();
        addButtonClose();
        new YouTubeExtractor(getActivity()) {
            /*
            @Override
            public void onExtractionComplete(SparseArray<YtFile> ytFiles, VideoMeta vMeta) {
                //mainProgressBar.setVisibility(View.GONE);
                if (ytFiles == null) {
                    // Something went wrong we got no urls. Always check this.
                    getActivity().finish();
                    return;
                }
                // Iterate over itags
                for (int i = 0, itag; i < ytFiles.size(); i++) {
                    itag = ytFiles.keyAt(i);
                    // ytFile represents one file with its url and meta data
                    YtFile ytFile = ytFiles.get(itag);

                    // Just add videos in a decent format => height -1 = audio
                    if (ytFile.getFormat().getHeight() == -1 || ytFile.getFormat().getHeight() >= 360) {
                        addButtonToMainLayout(vMeta.getTitle(), ytFile);
                    }
                }
            }*/
            @Override
            public void onExtractionComplete(SparseArray<YtFile> ytFiles, VideoMeta vMeta) {
                //mainProgressBar.setVisibility(View.GONE);
                if (ytFiles == null) {
                    // Something went wrong we got no urls. Always check this.
                    getActivity().finish();
                    return;
                }
                formatsToShowList = new ArrayList<>();
                for (int i = 0, itag; i < ytFiles.size(); i++) {
                    itag = ytFiles.keyAt(i);
                    YtFile ytFile = ytFiles.get(itag);

                    if (ytFile.getFormat().getHeight() == -1 || ytFile.getFormat().getHeight() >= 360) {
                        addFormatToList(ytFile, ytFiles);
                    }
                }
                Collections.sort(formatsToShowList, new Comparator<YtFragmentedVideo>() {
                    @Override
                    public int compare(YtFragmentedVideo lhs, YtFragmentedVideo rhs) {
                        return lhs.height - rhs.height;
                    }
                });
                for (YtFragmentedVideo files : formatsToShowList) {
                    addButtonToMainLayout(vMeta.getTitle(), files);
                }
            }
        }.extract(youtubeLink, true, false);
        //dialogSearch = createDownloadYoutube();
        //dialogSearch.show();
    }

    private void addButtonToMainLayout(final String videoTitle, final YtFragmentedVideo ytFrVideo) {
        // Display some buttons and let the user choose the format
        /*String btnText = (ytfile.getFormat().getHeight() == -1) ? "Audio " + ytfile.getFormat().getAudioBitrate() + " kbit/s" : ytfile.getFormat().getHeight() + "p";
        btnText += (ytfile.getFormat().isDashContainer()) ? " dash" : "";
        Button btn = new Button(getContext());
        btn.setText(btnText);

        //getActivity().setContentView(R.layout.download_youtube);
        mainLayout = (LinearLayout) getView().findViewById(R.id.main_layout);
        mainLayout.setVisibility(View.VISIBLE);

        btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String filename;
                if (videoTitle.length() > 55) {
                    filename = videoTitle.substring(0, 55) + "." + ytfile.getFormat().getExt();
                } else {
                    filename = videoTitle + "." + ytfile.getFormat().getExt();
                }
                filename = filename.replaceAll("[\\\\><\"|*?%:#/]", "");
                downloadFromUrl(ytfile.getUrl(), videoTitle, filename);
                //finish();*/
                /*
                Intent intent = getActivity().getIntent();
                getActivity().finish();
                startActivity(intent);
                */
                //variable.cargarFragment();
                //busquedaVideos(terminoBusqueda);

                //mainLayout.setVisibility(View.GONE);

            //}
        //});
        /*try {
            mainLayout.addView(btn);
            //Toast.makeText(getContext(), "Agregue boton", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext().getApplicationContext(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }*/

        // Display some buttons and let the user choose the format
        String btnText;
        if (ytFrVideo.height == -1)
            btnText = "Audio " + ytFrVideo.audioFile.getFormat().getAudioBitrate() + " kbit/s";
        else
            btnText = (ytFrVideo.videoFile.getFormat().getFps() == 60) ? ytFrVideo.height + "p60" :
                    ytFrVideo.height + "p";
        Button btn = new Button(getContext());
        btn.setText(btnText);

        mainLayout = (LinearLayout) getView().findViewById(R.id.main_layout);
        mainLayout.setVisibility(View.VISIBLE);
        btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String filename;
                if (videoTitle.length() > 55) {
                    filename = videoTitle.substring(0, 55);
                } else {
                    filename = videoTitle;
                }
                filename = filename.replaceAll("[\\\\><\"|*?%:#/]", "");
                filename += (ytFrVideo.height == -1) ? "" : "-" + ytFrVideo.height + "p";
                String downloadIds = "";
                boolean hideAudioDownloadNotification = false;
                if (ytFrVideo.videoFile != null) {
                    downloadIds += downloadFromUrl(ytFrVideo.videoFile.getUrl(), videoTitle,
                            filename + "." + ytFrVideo.videoFile.getFormat().getExt(), false);
                    downloadIds += "-";
                    hideAudioDownloadNotification = true;
                }
                if (ytFrVideo.audioFile != null) {
                    downloadIds += downloadFromUrl(ytFrVideo.audioFile.getUrl(), videoTitle,
                            filename + "." + ytFrVideo.audioFile.getFormat().getExt(), hideAudioDownloadNotification);
                }
                if (ytFrVideo.audioFile != null)
                    cacheDownloadIds(downloadIds);

                //variable.cargarFragment();
                //busquedaVideos(terminoBusqueda);

                mainLayout.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Descargando...", Toast.LENGTH_LONG).show();
            }
        });
        mainLayout.addView(btn);
    }

    private void addButtonClose () {
        Button btn = new Button(getContext());
        btn.setText("Cerrar");
        btn.setBackgroundColor(Color.parseColor("#E74C3C"));
        mainLayout = (LinearLayout) getView().findViewById(R.id.main_layout);
        btn.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               mainLayout.setVisibility(View.GONE);
           }
        });
        mainLayout.addView(btn);
    }
    /*
    private void downloadFromUrl(String youtubeDlUrl, String downloadTitle, String fileName) {
        Uri uri = Uri.parse(youtubeDlUrl);
        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setTitle(downloadTitle);

        request.allowScanningByMediaScanner();
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_MUSIC, fileName);

        DownloadManager manager = (DownloadManager) getActivity().getSystemService(Context.DOWNLOAD_SERVICE);
        manager.enqueue(request);
    }*/

    private long downloadFromUrl(String youtubeDlUrl, String downloadTitle, String fileName, boolean hide) {
        Uri uri = Uri.parse(youtubeDlUrl);
        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setTitle(downloadTitle);
        if (hide) {
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN);
            request.setVisibleInDownloadsUi(false);
        } else
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_MUSIC, fileName);

        DownloadManager manager = (DownloadManager) getActivity().getSystemService(Context.DOWNLOAD_SERVICE);
        return manager.enqueue(request);
    }

    public AlertDialog createDownloadYoutube() {

        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        LayoutInflater inflater = this.getLayoutInflater();

        View v = inflater.inflate(R.layout.content_main, null);

        // Check how it was started and if we can get the youtube link
        /*
        if (youtubeLink != null && (youtubeLink.contains("://youtu.be/") || youtubeLink.contains("youtube.com/watch?v="))) {
            // We have a valid link
            getYoutubeDownloadUrl(youtubeLink);
            Toast.makeText(this, "Hola Mundo 1", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Hola Mundo 2", Toast.LENGTH_LONG).show();
            //finish();
        }
        */
        builder.setView(v).setNegativeButton("Cancelar",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

        return builder.create();
    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    private class YtFragmentedVideo {
        int height;
        YtFile audioFile;
        YtFile videoFile;
    }

    private void cacheDownloadIds(String downloadIds) {
        File dlCacheFile = new File(getActivity().getCacheDir().getAbsolutePath() + "/" + downloadIds);
        try {
            dlCacheFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void addFormatToList(YtFile ytFile, SparseArray<YtFile> ytFiles) {
        int height = ytFile.getFormat().getHeight();
        if (height != -1) {
            for (YtFragmentedVideo frVideo : formatsToShowList) {
                if (frVideo.height == height && (frVideo.videoFile == null ||
                        frVideo.videoFile.getFormat().getFps() == ytFile.getFormat().getFps())) {
                    return;
                }
            }
        }
        YtFragmentedVideo frVideo = new YtFragmentedVideo();
        frVideo.height = height;
        if (ytFile.getFormat().isDashContainer()) {
            if (height > 0) {
                frVideo.videoFile = ytFile;
                frVideo.audioFile = ytFiles.get(ITAG_FOR_AUDIO);
            } else {
                frVideo.audioFile = ytFile;
            }
        } else {
            frVideo.videoFile = ytFile;
        }
        formatsToShowList.add(frVideo);
    }
}