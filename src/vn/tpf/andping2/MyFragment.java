package vn.tpf.andping2;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost;
import android.widget.TextView;

/**
 * Created with IntelliJ IDEA.
 * User: hung.vo
 * Date: 2/20/13
 * Time: 8:23 AM
 * To change this template use File | Settings | File Templates.
 */
public class MyFragment extends Fragment implements TabHost.OnTabChangeListener{
    private static final String TAG = "Android Ping";
    private static final String TAB_1 = "Ping";
    private static final String TAB_2 = "DNS Lookup";
    private static final String TAB_3 = "Trace Route";

    private View mRoot;
    private TabHost mTabHost;
    private int currentTab;

    @Override
    public void onAttach (Activity activity){
      super.onAttach(activity);
    }

    public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        mRoot = inflater.inflate(R.layout.tab_fragment,null);
        mTabHost = (TabHost)mRoot.findViewById(android.R.id.tabhost);

         //setup tab
        setupTabs();
        return  mRoot;
    }

    public void onActivityCreated (Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        setRetainInstance(true);
        mTabHost.setOnTabChangedListener(this);
        mTabHost.setCurrentTab(currentTab);
        //Update tab

        updateTab(TAB_1,R.id.tab_1);
    }

    private void setupTabs(){
        mTabHost.setup();
        mTabHost.addTab(newTab(TAB_1,R.string.tab_ping,R.id.tab_1));
        mTabHost.addTab(newTab(TAB_2,R.string.tab_lookup,R.id.tab_2));
        mTabHost.addTab(newTab(TAB_3,R.string.tab_trace,R.id.tab_3));

    }

    private TabHost.TabSpec newTab (String tag, int labelID, int layoutID){
        View indicator = LayoutInflater.from(getActivity()).inflate(R.layout.tab,(ViewGroup)mRoot.findViewById(android.R.id.tabs),false);
        ((TextView)indicator.findViewById(R.id.text)).setText(labelID);
        TabHost.TabSpec tabSpec = mTabHost.newTabSpec(tag);
        tabSpec.setIndicator(indicator);
        tabSpec.setContent(layoutID);
        return tabSpec;
    }

    public void onTabChanged(String tabID){
        if(TAB_1.equals(tabID)){
            updateTab(tabID,R.id.tab_1);
            currentTab = 0;
            return;
        }

        if(TAB_2.equals(tabID)){
            updateTab(tabID,R.id.tab_2);
            currentTab = 1;
            return;
        }

        if(TAB_3.equals(tabID)){
            updateTab(tabID,R.id.tab_3);
            currentTab = 2;
            return;
        }
    }

    private void updateTab(String tabID,int placeholder){
        FragmentManager fm = getFragmentManager();
        if (tabID.equals(TAB_1)){
        if(fm.findFragmentByTag(tabID)==null){
            fm.beginTransaction()
                    .replace(placeholder,new TabPing(),tabID)
                    .commit();
        }
        }
        if (tabID.equals(TAB_2)){
            if(fm.findFragmentByTag(tabID)==null){
                fm.beginTransaction()
                        .replace(placeholder,new TabLookup(),tabID)
                        .commit();
            }
        }


        if (tabID.equals(TAB_3)){
            if(fm.findFragmentByTag(tabID)==null){
                fm.beginTransaction()
                        .replace(placeholder,new TabTrace(),tabID)
                        .commit();
            }
        }
    }
}
