package net.magnetspace.frequency.GUI;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PorterDuff;
import android.media.AudioManager;
import android.media.audiofx.Visualizer;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import net.magnetspace.frequency.Core.FrequencyGenerator;
import net.magnetspace.frequency.Core.PlayList;
import net.magnetspace.frequency.GUI.CustomView.VisualizerView;
import net.magnetspace.frequency.R;
import net.magnetspace.frequency.SQL.DatabaseHandler;

public class FrequencyPlayerActivity extends Activity {
    private Button startPlayingBtn;
    private SeekBar seekBar;
    private TextView textView;
    private TextView textView2;
    private VisualizerView visualizerView;
    private Visualizer visualizer;
    private Button pwrBtn1;
    private Button pwrBtn2;
    private Button pwrBtn3;
    private Button pwrBtn4;
    private Button pwrBtn5;
    private TextView batLevel;

    private AudioManager audioManager;
    private FrequencyGenerator frequencyGenerator;
    private PlayList playList;
    private DatabaseHandler dbHandler;
    private CountDownTimer countDownTimer;

    private int allTime;
    private int sec = 0;
    private int min = 0;
    private int cursor = 0;
    private int progress = 0;
    private boolean isPlaying = false;

    private BroadcastReceiver mBatInfoReceiver = new BroadcastReceiver(){
        @Override
        public void onReceive(Context ctxt, Intent intent) {
            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
            batLevel.setText(getResources().getString(R.string.battery_level)+" "+String.valueOf(level) + "%");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_frequency_player);
        getActionBar().hide();
        dbHandler = new DatabaseHandler(this);
        startPlayingBtn = (Button) findViewById(R.id.startPlayingBtn);
        seekBar = (SeekBar) findViewById(R.id.seekBar);
        textView = (TextView) findViewById(R.id.textView);
        textView2 = (TextView) findViewById(R.id.textView2);
        visualizerView = (VisualizerView) findViewById(R.id.myvisualizerview);

        pwrBtn1 = (Button) findViewById(R.id.power1);
        pwrBtn2 = (Button) findViewById(R.id.power2);
        pwrBtn3 = (Button) findViewById(R.id.power3);
        pwrBtn4 = (Button) findViewById(R.id.power4);
        pwrBtn5 = (Button) findViewById(R.id.power5);
        batLevel = (TextView) findViewById(R.id.textView4);
        this.registerReceiver(this.mBatInfoReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));


        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        double x = (double) audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC) * 1.0;
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, (int) x, 0);

        pwrBtn5.getBackground().setColorFilter(0xFFFF0000, PorterDuff.Mode.MULTIPLY);

        frequencyGenerator = new FrequencyGenerator();

        final ListView listView = (ListView)findViewById(R.id.listView3);
        listView.setAdapter(new ArrayAdapter(this, android.R.layout.simple_list_item_1, dbHandler.getSelectedFreqNames(MainActivity.getSelectedListItemID())));
        playList = dbHandler.getFrequencies();
        seekBar.setMax(playList.size()*60);
        seekBar.setProgress(0);
        allTime = playList.size()*60*1000;
        textView2.setText(String.format("%d/%d: %s", playList.size(), cursor+1, playList.get(cursor).getName()));
        frequencyGenerator.setFrequency(playList.get(cursor).getFreq());

        setupVisualizerFxAndUI();
        visualizer.setEnabled(true);

        countDownTimer = new CountDownTimer(playList.size()*60*1000, 1000) {
            @Override
            public void onTick(long l) {
                progress++;
                sec++;
                if(sec == 60)
                {
                    min++;
                    sec = 0;
                    cursor++;

                    if(cursor != playList.size()) {
                        frequencyGenerator.setFrequency(playList.get(cursor).getFreq());
                        textView2.setText(String.format("%d/%d: %s", playList.size(),
                                cursor+1, playList.get(cursor).getName()));
                    }
                    else
                        countDownTimer.cancel();
                }
                seekBar.setProgress(progress);
                allTime -= 1000;
                if(allTime==0) {
                    sec=0; min++;
                    countDownTimer.cancel();
                }
                textView.setText(String.format("%02d:%02d/%02d:00", min, sec, playList.size()));
            }

            @Override
            public void onFinish() {
                sec=0; min++;
                seekBar.setProgress(progress+1);
                stopPlaying();
            }
        };
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_frequency_player, menu);
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

    private void stopPlaying()
    {
        isPlaying = !isPlaying;
        startPlayingBtn.setText(getResources().getString(R.string.start));
        frequencyGenerator.stop();
        frequencyGenerator.finish();
        countDownTimer.cancel();
        startPlayingBtn.setEnabled(false);
        textView.setText(String.format("%02d:%02d/%02d:00", min, sec, playList.size()));
    }

    public void OnStartPlayingBtn(View view)
    {
        if(isPlaying)
        {
            isPlaying = !isPlaying;
            startPlayingBtn.setText(getResources().getString(R.string.start));
            frequencyGenerator.stop();
            //frequencyGenerator.finish();
            countDownTimer.cancel();
            visualizerView.animate().translationY(-1000);
        }
        else {
            isPlaying = !isPlaying;
            startPlayingBtn.setText(getResources().getString(R.string.stop));
            countDownTimer.start();
            frequencyGenerator.start();
            visualizerView.animate().translationY(0);
        }
        audioManager.setStreamMute(AudioManager.STREAM_MUSIC, false);
    }

    public void onBackBtn2(View view) {
        countDownTimer.cancel();
        frequencyGenerator.finish();
        unregisterReceiver(mBatInfoReceiver);
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void setupVisualizerFxAndUI() {

        visualizer = new Visualizer(frequencyGenerator.getAudiSessionID());
        visualizer.setCaptureSize(Visualizer.getCaptureSizeRange()[1]);
        visualizer.setDataCaptureListener(
                new Visualizer.OnDataCaptureListener() {
                    public void onWaveFormDataCapture(Visualizer visualizer,
                                                      byte[] bytes, int samplingRate) {
                        visualizerView.updateVisualizer(bytes);
                    }

                    public void onFftDataCapture(Visualizer visualizer,
                                                 byte[] bytes, int samplingRate) {
                    }
                }, Visualizer.getMaxCaptureRate() / 2, true, false);
    }

    public void setPower1(View view)
    {
        double x = (double) audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC) * 0.2;
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, (int) x, 0);
        pwrBtn1.getBackground().setColorFilter(0xFFFF0000, PorterDuff.Mode.MULTIPLY);
        pwrBtn2.getBackground().setColorFilter(0xFFDDDDDD, PorterDuff.Mode.MULTIPLY);
        pwrBtn3.getBackground().setColorFilter(0xFFDDDDDD, PorterDuff.Mode.MULTIPLY);
        pwrBtn4.getBackground().setColorFilter(0xFFDDDDDD, PorterDuff.Mode.MULTIPLY);
        pwrBtn5.getBackground().setColorFilter(0xFFDDDDDD, PorterDuff.Mode.MULTIPLY);
    }
    public void setPower2(View view)
    {
        double x = (double) audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC) * 0.4;
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, (int) x, 0);
        pwrBtn1.getBackground().setColorFilter(0xFFDDDDDD, PorterDuff.Mode.MULTIPLY);
        pwrBtn2.getBackground().setColorFilter(0xFFFF0000, PorterDuff.Mode.MULTIPLY);
        pwrBtn3.getBackground().setColorFilter(0xFFDDDDDD, PorterDuff.Mode.MULTIPLY);
        pwrBtn4.getBackground().setColorFilter(0xFFDDDDDD, PorterDuff.Mode.MULTIPLY);
        pwrBtn5.getBackground().setColorFilter(0xFFDDDDDD, PorterDuff.Mode.MULTIPLY);
    }
    public void setPower3(View view)
    {
        double x = (double) audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC) * 0.6;
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, (int) x, 0);
        pwrBtn1.getBackground().setColorFilter(0xFFDDDDDD, PorterDuff.Mode.MULTIPLY);
        pwrBtn2.getBackground().setColorFilter(0xFFDDDDDD, PorterDuff.Mode.MULTIPLY);
        pwrBtn3.getBackground().setColorFilter(0xFFFF0000, PorterDuff.Mode.MULTIPLY);
        pwrBtn4.getBackground().setColorFilter(0xFFDDDDDD, PorterDuff.Mode.MULTIPLY);
        pwrBtn5.getBackground().setColorFilter(0xFFDDDDDD, PorterDuff.Mode.MULTIPLY);
    }
    public void setPower4(View view)
    {
        double x = (double) audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC) * 0.8;
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, (int) x, 0);
        pwrBtn1.getBackground().setColorFilter(0xFFDDDDDD, PorterDuff.Mode.MULTIPLY);
        pwrBtn2.getBackground().setColorFilter(0xFFDDDDDD, PorterDuff.Mode.MULTIPLY);
        pwrBtn3.getBackground().setColorFilter(0xFFDDDDDD, PorterDuff.Mode.MULTIPLY);
        pwrBtn4.getBackground().setColorFilter(0xFFFF0000, PorterDuff.Mode.MULTIPLY);
        pwrBtn5.getBackground().setColorFilter(0xFFDDDDDD, PorterDuff.Mode.MULTIPLY);
    }
    public void setPower5(View view)
    {
        int x = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, x, 0);
        pwrBtn1.getBackground().setColorFilter(0xFFDDDDDD, PorterDuff.Mode.MULTIPLY);
        pwrBtn2.getBackground().setColorFilter(0xFFDDDDDD, PorterDuff.Mode.MULTIPLY);
        pwrBtn3.getBackground().setColorFilter(0xFFDDDDDD, PorterDuff.Mode.MULTIPLY);
        pwrBtn4.getBackground().setColorFilter(0xFFDDDDDD, PorterDuff.Mode.MULTIPLY);
        pwrBtn5.getBackground().setColorFilter(0xFFFF0000, PorterDuff.Mode.MULTIPLY);
    }

}
