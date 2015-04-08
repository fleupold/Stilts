package de.hpi.hci.stilts;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

public class StiltsConnector {
	private static final String TAG = "StiltsConnector";

	private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
	private static final String RIGHT_DEVICE_NAME = "RNBT-C7EF"; 
	private static final String LEFT_DEVICE_NAME = "RNBT-C7EFK"; //TODO change to right name

	private BluetoothAdapter mBluetoothAdapter;
	private BluetoothSocket mLeftSocket, mRightSocket;

	public StiltsConnector(BluetoothAdapter adapter) {
		mBluetoothAdapter = adapter;
	}

	public void connect() throws IOException{
		Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
		if (pairedDevices.size() > 0) {
			for (BluetoothDevice device : pairedDevices) {
				Log.d(TAG, device.getName() + "\n" + device.getAddress());
				if (device.getName().equals(RIGHT_DEVICE_NAME)) {
					mRightSocket = device.createRfcommSocketToServiceRecord(MY_UUID);
					mRightSocket.connect();
				}
				if (device.getName().equals(LEFT_DEVICE_NAME)) {
					mLeftSocket = device.createInsecureRfcommSocketToServiceRecord(MY_UUID);
					mLeftSocket.connect();
				}

			}
		}
		if (mLeftSocket == null) {
			//throw new IOException("No left socket");
		}
		if (mRightSocket == null) {
			throw new IOException("No right socket");
		}
	}

	public void setLeft(int value) {
		Log.d(TAG, "Setting left stilt to " + value);
		if (mLeftSocket == null) {
			return;
		}
		
		try {
			mRightSocket.getOutputStream().write(new byte[]{(byte) value});
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void setRight(int value) {
		Log.d(TAG, "Setting right stilt to " + value);
		if (mRightSocket == null) {
			return;
		}
		
		try {
			mRightSocket.getOutputStream().write(new byte[]{(byte) value});
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void disconnect() {
		try {
			if (mLeftSocket != null) {				
				mLeftSocket.close();
			}
			if (mRightSocket != null) {				
				mRightSocket.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
