package com.rising.drawing;

import java.util.Observable;

import be.hogent.tarsos.dsp.SilenceDetector;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

public class SoundReader extends Observable implements AudioRecord.OnRecordPositionUpdateListener {

	private static final int AUDIO_SAMPLING_RATE = 8000;
	private static final int AUDIO_DATA_SIZE = 2048;
	private int bufferSize;
	private static int notifyRate = 3200;
	private static float threshold = (float) 15.0;
	
	private transient AudioRecord audioRecord;
	private transient Thread audioReaderThread;
	private transient SilenceDetector silenceDetector;
	
	private transient int pitch = -1;
	private transient int historyPitch;
	
	@Override
	public void onMarkerReached(final AudioRecord arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onPeriodicNotification(final AudioRecord arg0) {
		notifyObservers(pitch);
	}
	
	public void onDestroy() {
		if (audioReaderThread != null) {
			audioReaderThread.interrupt();
			audioReaderThread = null;
		}
		
		audioRecord.release();
		audioRecord = null;
		
		//silenceDetector = null;
	}

	public SoundReader(final int velocidad) throws Exception {
		
		setSpeed(velocidad);
		
		bufferSize = AudioRecord.getMinBufferSize(44100, AudioFormat.CHANNEL_IN_MONO, 
					  AudioFormat.ENCODING_PCM_16BIT) * 2;
			
		audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, 
									  AUDIO_SAMPLING_RATE, AudioFormat.CHANNEL_IN_MONO, 
									  AudioFormat.ENCODING_PCM_16BIT, bufferSize);
		audioRecord.setRecordPositionUpdateListener(this);
		
		if(audioRecord.getState() != AudioRecord.STATE_INITIALIZED) {
			throw new Exception("Could not initialize microphone.");
		}
		
		startRecorder();
		
		if(audioRecord.setPositionNotificationPeriod(notifyRate) != AudioRecord.SUCCESS) {
			throw new Exception("Wrong notify rate.");
		}
		
		silenceDetector = new SilenceDetector();
	}
	
	private void startRecorder() {
		audioRecord.startRecording();
		startAudioReaderThread();
	}
	
	private void startAudioReaderThread() {
		audioReaderThread = new Thread(new Runnable() {
			
			@Override
			public void run() {
				while (true) {
					if (audioRecord == null) {
						break;
					}
					
					final short[] audioData = new short[AUDIO_DATA_SIZE];
					final int shortsRead = audioRecord.read(audioData, 0, AUDIO_DATA_SIZE);
					
					if (shortsRead < 0) {
						Log.e("NO READ", "Could not read audio data.");
					} else {
						if (silenceDetector.isSilence(turnToFloatArray(audioData), threshold)) {
							pitch = 0;
						} else {
							pitch = ++historyPitch;
						}
						
						setChanged();
					}
				}
			}
		});
		
		audioReaderThread.setDaemon(false);
		audioReaderThread.start();
	}
	
	private float[] turnToFloatArray(final short[] shortArray) {
		float[] floatArray = new float[shortArray.length];
		
		for (int i=0; i<shortArray.length; i++) {
			floatArray[i] = shortArray[i];
		}
		
		return floatArray;
	}

	public void setSensitivity(final int sensitivity) {
		switch (sensitivity) {
			case 0:
				threshold = 60;
				break;
			case 1:
				threshold = 54;
				break;
			case 2:
				threshold = 48;
				break;
			case 3:
				threshold = 42;
				break;
			case 4:
				threshold = 36;
				break;
			case 5:
				threshold = 30;
				break;
			case 6:
				threshold = 24;
				break;
			case 7:
				threshold = 18;
				break;
			case 8:
				threshold = 12;
				break;
			case 9:
				threshold = 6;
				break;
			case 10:
				threshold = 0;
				break;
				/*
			case 11:
				threshold = 27;
				break;
			case 12:
				threshold = 24;
				break;
			case 13:
				threshold = 21;
				break;
			case 14:
				threshold = 18;
				break;
			case 15:
				threshold = 15;
				break;
			case 16:
				threshold = 12;
				break;
			case 17:
				threshold = 9;
				break;
			case 18:
				threshold = 6;
				break;
			case 19:
				threshold = 3;
				break;
			case 20:
				threshold = 0;
				break;
				*/
			default:
				break;
		}
	}

	private void setSpeed(final int speed) {
		switch (speed) {
			case 0:
				notifyRate = 16000;
				break;
			case 1:
				notifyRate = 14500;
				break;
			case 2:
				notifyRate = 13000;
				break;
			case 3:
				notifyRate = 11500;
				break;
			case 4:
				notifyRate = 10000;
				break;
			case 5:
				notifyRate = 8500;
				break;
			case 6:
				notifyRate = 7000;
				break;
			case 7:
				notifyRate = 5500;
				break;
			case 8:
				notifyRate = 4000;
				break;
			case 9:
				notifyRate = 2500;
				break;
			case 10:
				notifyRate = 1000;
				break;
				/*
			case 11:
				notifyRate = 8200;
				break;
			case 12:
				notifyRate = 7400;
				break;
			case 13:
				notifyRate = 6600;
				break;
			case 14:
				notifyRate = 5800;
				break;
			case 15:
				notifyRate = 5000;
				break;
			case 16:
				notifyRate = 4200;
				break;
			case 17:
				notifyRate = 3400;
				break;
			case 18:
				notifyRate = 2600;
				break;
			case 19:
				notifyRate = 1800;
				break;
			case 20:
				notifyRate = 1000;
				break;
				*/
			default:
				break;
		}
	}
}
