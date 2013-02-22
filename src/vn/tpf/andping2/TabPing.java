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
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;

/**
 * Created with IntelliJ IDEA.
 * User: hung.vo
 * Date: 2/1/13
 * Time: 11:32 AM
 * To change this template use File | Settings | File Templates.
 */
public class TabPing extends Fragment {

    private static final int TASK_DONE = 1;
    private static final int TASK_CANCELLED = 0;
    private static final int ERROR = -1;

    private Button btnPing, btnStop;
    private EditText edtIp;
    private TextView txRs, txNPackets;
    private ProgressBar prStart;
    private SeekBar skTimes;
    private PingTask pt;
    private ScrollView svScroll;

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

        RelativeLayout thelayout  = (RelativeLayout)inflater.inflate(R.layout.ping_layout,container,false);

        skTimes =(SeekBar)thelayout.findViewById(R.id.seekTimes);
        txNPackets = (TextView)thelayout.findViewById(R.id.txPackets);
        btnPing = (Button)thelayout.findViewById(R.id.btPing);
        prStart = (ProgressBar)thelayout.findViewById(R.id.progressBar);
        txRs = (TextView)thelayout.findViewById(R.id.txResult);
        edtIp = (EditText)thelayout.findViewById(R.id.edAddress);
        btnStop = (Button)thelayout.findViewById(R.id.btStop);

        //Implement On Seek Bar Change
        skTimes.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {


            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                // TODO Auto-generated method stub
                if (progress == 0){
                    txNPackets.setText("No limit");
                }
                else{
                    txNPackets.setText(progress + " Packets");
                }

            }
        });

        //Restore state
        if(savedInstanceState !=null){
            txRs.setText(savedInstanceState.getString("result"));
        }

        //Cancel Ping
        btnStop.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick (View v){
                pt.cancel(true);
            }

        });


        //Handle event button Ping click
        btnPing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String address = edtIp.getText().toString();
                int times = skTimes.getProgress();
                pt = new PingTask(times);
                pt.execute(address);
            }
        });
        return thelayout;
    }


    @Override
    public void onActivityCreated (Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null){
            skTimes.setProgress(savedInstanceState.getInt("seekbar"));
            edtIp.setText(savedInstanceState.getString("adress"));
            txRs.setText(savedInstanceState.getString("result"));
        }
    }
    //Save state
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("seekbar", skTimes.getProgress());
        outState.putString("address",edtIp.getText().toString());
        outState.putString("result",txRs.getText().toString());
        Log.v("rs",txRs.getText().toString());
    }


    private class PingTask extends AsyncTask<String, String, Integer> {

        private int times;

        public PingTask(int times){
            this.times = times;
        }

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
            if (times == 0){
                cmd  = "ping " + address;
            }
            else {
                cmd  = "ping -c " + times + " " + address;
            }
            try {

                Process p;

                p= Runtime.getRuntime().exec(cmd);

                Log.v("cmd",cmd);
                BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
                String s;
                    while ((s  = stdInput.readLine())!= null) {
                        publishProgress(s);
                    Log.v("rs", s);
                    if (isCancelled()){
                        stdInput.close();
                        return TASK_CANCELLED;
                    }
                }
                p.waitFor();
                stdInput.close();
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



        public int getTimes() {
            return times;
        }

        public void setTimes(int times) {
            this.times = times;
        }

    }
}
