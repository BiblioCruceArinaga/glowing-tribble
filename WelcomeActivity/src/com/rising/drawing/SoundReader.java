package com.rising.drawing;

import java.util.Observable;

import be.hogent.tarsos.dsp.SilenceDetector;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

public class SoundReader extends Observable implements AudioRecord.OnRecordPositionUpdateListener {

	private static final int AUDIO_SAMPLING_RATE = 8000;
	private static int AUDIO_DATA_SIZE = 2048;
	private static int BUFFER_SIZE = 0;
	
	//  Velocidad a la que se notifican las muestras y
	//  volumen a partir del cual se considera un ruido válido
	//  3040, 3120, 3200
	private static int NOTIFY_RATE = 3180;
	private static float THRESHOLD = (float) 15.0;
	
	private AudioRecord audioRecord = null;
	private Thread audioReaderThread = null;
	private SilenceDetector silenceDetector = null;
	
	private int pitch = -1;
	private int historyPitch = 0;
	
	@Override
	public void onMarkerReached(AudioRecord arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPeriodicNotification(AudioRecord arg0) {
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

	public SoundReader() throws Exception {
		BUFFER_SIZE = AudioRecord.getMinBufferSize(44100, AudioFormat.CHANNEL_IN_MONO, 
					  AudioFormat.ENCODING_PCM_16BIT) * 2;
			
		audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, 
									  AUDIO_SAMPLING_RATE, AudioFormat.CHANNEL_IN_MONO, 
									  AudioFormat.ENCODING_PCM_16BIT, BUFFER_SIZE);
		audioRecord.setRecordPositionUpdateListener(this);
		
		if(audioRecord.getState() != AudioRecord.STATE_INITIALIZED) {
			throw new Exception("Could not initialize microphone.");
		}
		
		startRecorder();
		
		if(audioRecord.setPositionNotificationPeriod(NOTIFY_RATE) != AudioRecord.SUCCESS) {
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
					if (audioRecord == null) break;
					
					short[] audioData = new short[AUDIO_DATA_SIZE];
					int shortsRead = audioRecord.read(audioData, 0, AUDIO_DATA_SIZE);
					
					if (shortsRead < 0) {
						Log.e("NO READ", "Could not read audio data.");
					} else {
						if (silenceDetector.isSilence(turnToFloatArray(audioData), THRESHOLD))
							pitch = 0;
						else
							pitch = ++historyPitch;
						
						setChanged();
					}
				}
			}
		});
		
		audioReaderThread.setDaemon(false);
		audioReaderThread.start();
	}
	
	private float[] turnToFloatArray(short[] shortArray) {
		float[] floatArray = new float[shortArray.length];
		
		for (int i=0; i<shortArray.length; i++)
			floatArray[i] = shortArray[i];
		
		return floatArray;
	}
	
	//  El músico podrá configurar el "volumen" al que
	//  deberá tocar para que la aplicación considere el 
	//  sonido recogido como válido. Esto permitirá al
	//  músico usar la aplicación sin problemas en entornos
	//  donde haya ruido de fondo
	public void setSensitivity(int sensitivity) {
		switch (sensitivity) {
			case 0:
				THRESHOLD = 30;
				break;
			case 1:
				THRESHOLD = 27;
				break;
			case 2:
				THRESHOLD = 24;
				break;
			case 3:
				THRESHOLD = 21;
				break;
			case 4:
				THRESHOLD = 18;
				break;
			case 5:
				THRESHOLD = 15;
				break;
			case 6:
				THRESHOLD = 12;
				break;
			case 7:
				THRESHOLD = 9;
				break;
			case 8:
				THRESHOLD = 6;
				break;
			case 9:
				THRESHOLD = 3;
				break;
			case 10:
				THRESHOLD = 0;
				break;
			default:
				break;
		}
	}
}
