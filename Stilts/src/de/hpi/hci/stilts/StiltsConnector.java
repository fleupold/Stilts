package de.hpi.hci.stilts;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
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
	private static final String LEFT_DEVICE_NAME = "RNBT-C628";

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
					mRightSocket = createSocket(device);
				}
				if (device.getName().equals(LEFT_DEVICE_NAME)) {
					mLeftSocket = createSocket(device);
				}

			}
		}
		if (mLeftSocket == null) {
			throw new IOException("No left socket");
		}
		if (mRightSocket == null) {
			throw new IOException("No right socket");
		}
	}
	
	private BluetoothSocket createSocket(BluetoothDevice device) throws IOException {
		BluetoothSocket socket = device.createRfcommSocketToServiceRecord(MY_UUID);
		try {						
			socket.connect();
		} catch (IOException e) {
			//evil hack: http://stackoverflow.com/questions/18657427/ioexception-read-failed-socket-might-closed-bluetooth-on-android-4-3/18786701#18786701
			try {
				socket =(BluetoothSocket) device.getClass().getMethod("createRfcommSocket", new Class[] {int.class}).invoke(device,1);
				socket.connect();
			} catch (Exception e1) {
				throw new IOException("Unable to connect using mport hack");
			}
		}
		return socket;
	}

	public void setLeft(int value) {
		Log.d(TAG, "Setting left stilt to " + value);
		if (mLeftSocket == null) {
			return;
		}
		
		try {
			mLeftSocket.getOutputStream().write(new byte[]{(byte) value});
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
