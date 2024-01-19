package com.example.jetimdemo;

import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;

import androidx.core.view.WindowCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.jetimdemo.databinding.ActivityMainBinding;
import com.jet.im.JetIM;
import com.jet.im.JetIMConst;
import com.jet.im.interfaces.IConnectionManager;
import com.jet.im.interfaces.IMessageManager;
import com.jet.im.model.Conversation;
import com.jet.im.model.Message;
import com.jet.im.model.messages.TextMessage;
import com.jet.im.utils.LoggerUtils;

import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {

    private final String TOKEN1 = "CgZhcHBrZXkaIDAr072n8uOcw5YBeKCcQ+QCw4m6YWhgt99U787/dEJS";
    private final String TOKEN2 = "CgZhcHBrZXkaINodQgLnbhTbt0SzC8b/JFwjgUAdIfUZTEFK8DvDLgM1";
    private final String TOKEN3 = "CgZhcHBrZXkaINMDzs7BBTTZTwjKtM10zyxL4DBWFuZL6Z/OAU0Iajpv";
    private final String TOKEN4 = "CgZhcHBrZXkaIDHZwzfny4j4GiJye8y8ehU5fpJ+wVOGI3dCsBMfyLQv";
    private final String TOKEN5 = "CgZhcHBrZXkaIOx2upLCsmsefp8U/KNb52UGnAEu/xf+im3QaUd0HTC2";

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        JetIM.getInstance().getConnectionManager().addConnectionStatusListener("mainActivity", new IConnectionManager.IConnectionStatusListener() {
            @Override
            public void onStatusChange(JetIMConst.ConnectionStatus status, int code) {
                Log.i("lifei", "main activity onStatusChange status is " + status + " code is " + code);
                if (status == JetIMConst.ConnectionStatus.CONNECTED) {

                    //disconnect
//                    JetIM.getInstance().getConnectionManager().disconnect(false);

                    //send message
                    Conversation conversation = new Conversation(Conversation.ConversationType.PRIVATE, "userid1");
                    TextMessage text = new TextMessage("tttt");
                    TextMessage text2 = new TextMessage("iiiii");
                    JetIM.getInstance().getMessageManager().sendMessage(text, conversation, new IMessageManager.ISendMessageCallback() {
                        @Override
                        public void onSave(Message message) {
                            Log.i("lifei", "sendMessage onSave");
                        }

                        @Override
                        public void onSuccess(Message message) {
                            Log.i("lifei", "sendMessage onSuccess");
                        }

                        @Override
                        public void onError(Message message, int errorCode) {
                            Log.i("lifei", "sendMessage onError");
                        }
                    });
                    JetIM.getInstance().getMessageManager().sendMessage(text2, conversation, new IMessageManager.ISendMessageCallback() {
                        @Override
                        public void onSave(Message message) {
                            Log.i("lifei", "sendMessage onSave 2");
                        }

                        @Override
                        public void onSuccess(Message message) {
                            Log.i("lifei", "sendMessage onSuccess 2");
                        }

                        @Override
                        public void onError(Message message, int errorCode) {
                            Log.i("lifei", "sendMessage onError 2");
                        }
                    });



                }
            }
        });
        JetIM.getInstance().getConnectionManager().connect(TOKEN3);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAnchorView(R.id.fab)
                        .setAction("Action", null).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }
}