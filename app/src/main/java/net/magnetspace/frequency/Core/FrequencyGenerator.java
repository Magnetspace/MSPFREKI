package net.magnetspace.frequency.Core;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

/**
 * Created by Botond on 2015.06.22..
 */
public class FrequencyGenerator {

    private AudioTrack audioTrack;
    private int sampleCount;

    private final int sampleRate = 22050;
    private final int minFrequency = 1;
    private final int bufferSize = sampleRate / minFrequency;
    private byte[] samples;

    public FrequencyGenerator()
    {
        audioTrack = new AudioTrack(
                AudioManager.STREAM_MUSIC,
                sampleRate,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_8BIT,
                bufferSize,
                AudioTrack.MODE_STATIC );
    }

    public void setFrequency( double frequency )
    {
        if(frequency < 1)
            frequency = 1;
        int x = (int)((double)bufferSize * frequency / sampleRate);
        sampleCount = (int)( (double)x * sampleRate / frequency);

        samples = new byte[sampleCount];
        for( int i = 0; i != sampleCount; ++i ) {
            double t = (double)i * (1.0/sampleRate);
            double f = Math.sin( t * 2*Math.PI * frequency);
            samples[i] = (byte)(f * 127);
        }

        audioTrack.write(samples, 0, sampleCount);
    }


    public void start()
    {
        audioTrack.reloadStaticData();
        audioTrack.setLoopPoints(0, sampleCount, -1);
        audioTrack.play();
    }


    public void stop()
    {
        audioTrack.stop();
    }

    public  void finish() {
        audioTrack.release();
    }

    public byte[] getSamples() {
        return samples;
    }

    public int getAudiSessionID() {
        return audioTrack.getAudioSessionId();
    }
}
