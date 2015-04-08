package de.hpi.hci.stilts;

import java.io.IOException;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class StiltsActivity extends Activity implements OnSeekBarChangeListener {
	private final static int REQUEST_CODE = 0;
	private final static String TAG = "StiltSlider";
	
	private StiltsConnector mStiltConnector;
	private SeekBar mLeftSeekbar, mRightSeekbar;
	private TextView mLeftTextView, mRightTextView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_stilts);
		
		mLeftSeekbar = (SeekBar) findViewById(R.id.leftSeekBar);
		mLeftSeekbar.setOnSeekBarChangeListener(this);
		
		mRightSeekbar = (SeekBar) findViewById(R.id.rightSeekBar);
		mRightSeekbar.setOnSeekBarChangeListener(this);

		mLeftTextView = (TextView) findViewById(R.id.leftValue);
		mRightTextView = (TextView) findViewById(R.id.rightValue);
		
		mLeftSeekbar.setMax(255);
		mRightSeekbar.setMax(255);

		mLeftTextView.setText("0");
		mRightTextView.setText("0");

		setupConnection();
	}
	
	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		//Called when returning from bluetooth setting screen.
		super.onActivityResult(arg0, arg1, arg2);
		setupConnection();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.stilts, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		mStiltConnector.disconnect();
	}
	
	private void setupConnection() {
		BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
		    showAlert(getString(R.string.bluetooth_disabled));
		}
		mStiltConnector = new StiltsConnector(bluetoothAdapter);
		try {
			mStiltConnector.connect();
		} catch (IOException e) {
			showAlert(e.getMessage());
		}
	}
	
	private void showAlert(String title) {
		new AlertDialog.Builder(this)
		    .setTitle(title)
		    .setMessage(R.string.alert_message)
		    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
		        public void onClick(DialogInterface dialog, int which) { 
		        	Intent intentOpenBluetoothSettings = new Intent();
	            	intentOpenBluetoothSettings.setAction(android.provider.Settings.ACTION_BLUETOOTH_SETTINGS); 
	            	startActivityForResult(intentOpenBluetoothSettings, REQUEST_CODE);
		        }
		     })
		     .show();
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean unused) {
		if (seekBar == mLeftSeekbar) {
			mLeftTextView.setText(String.valueOf(progress));
		}
		if (seekBar == mRightSeekbar) {
			mRightTextView.setText(String.valueOf(progress));
		}
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		if (seekBar == mLeftSeekbar) {
			mStiltConnector.setLeft(seekBar.getProgress());
		}
		if (seekBar == mRightSeekbar) {
			mStiltConnector.setRight(seekBar.getProgress());
		}
	}
}
