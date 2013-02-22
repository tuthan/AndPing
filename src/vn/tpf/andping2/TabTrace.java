package vn.tpf.andping2;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * Created with IntelliJ IDEA.
 * User: hung.vo
 * Date: 2/1/13
 * Time: 11:32 AM
 * To change this template use File | Settings | File Templates.
 */
public class TabTrace extends Fragment {
    private static final int TASK_DONE = 1;
    private static final int TASK_CANCELLED = 0;
    private static final int ERROR = -1;

    private Button btnTrace, btnStop;
    private EditText edtAdd;
    private TextView txRs, txNPackets;
    private ProgressBar prStart;
    private LookupTask lt;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (container == null) {
            // We have different layouts, and in one of them this
            // fragment's containing frame doesn't exist.  The fragment
            // may still be created from its saved state, but there is
            // no reason to try to create its view hierarchy because it
            // won't be displayed.  Note this is not needed -- we could
            // just run the code below, where we would create and return
            // the view hierarchy; it would just never be used.
            return null;
        }
        RelativeLayout thelayout = (RelativeLayout)inflater.inflate(R.layout.trace_layout,container,false);
        btnTrace = (Button)thelayout.findViewById(R.id.btTrace);
        prStart = (ProgressBar)thelayout.findViewById(R.id.progressBar);
        txRs = (TextView)thelayout.findViewById(R.id.txResult);
        edtAdd = (EditText)thelayout.findViewById(R.id.edAddress);
        btnStop = (Button)thelayout.findViewById(R.id.btStop);

        btnTrace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 lt = new LookupTask();
                 lt.execute(edtAdd.getText().toString());
            }
        });

        return thelayout;
    }



     //AsyncTask for lookup
    private class LookupTask extends AsyncTask<String, String, Integer> {



        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            prStart.setVisibility(View.VISIBLE);
            txRs.setText("");
        }

        @Override
        protected void onProgressUpdate(String... values) {
            // TODO Auto-generated method stub
            txRs.append("\n" + values[0]);

        }
        @Override
        protected Integer doInBackground(String... params) {
            // TODO Auto-generated method stub
            String address = params[0];
            String cmd = "";

            cmd  = "traceroute " + address;

            try {

                Process p;

                p= Runtime.getRuntime().exec(new String[]{"su","-c",cmd});
                BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
                String s ;

                while ((s  = stdInput.readLine())!= null) {
                    publishProgress(s);
                    Log.v("rs", s);
                    if (isCancelled()){
                        return TASK_CANCELLED;
                    }
                }
                p.destroy();
                return TASK_DONE;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return ERROR;

        }

        @Override
        protected void onPostExecute(Integer result) {
            // TODO Auto-generated method stub
            prStart.setVisibility(View.GONE);
            switch (result) {
                case TASK_DONE:
                    txRs.append("\n Task Done");
                    break;
                case ERROR:
                    txRs.append("Sorry got some error");
                    break;
                case TASK_CANCELLED:
                    txRs.append("\n Task canceled by user");
                    break;
                default:
                    break;
            }
        }

        @Override
        protected void onCancelled() {
            // TODO Auto-generated method stub
            txRs.append("\n Task canceled by user");
            prStart.setVisibility(View.GONE);
        }

    }
}
