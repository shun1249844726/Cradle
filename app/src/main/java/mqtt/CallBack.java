package mqtt;

import android.os.*;
import com.ibm.micro.client.mqttv3.*;

public class CallBack implements MqttCallback {
	private String instanceData = "";
	private Handler handler;

	public CallBack(String instance, Handler handler) {
		instanceData = instance;
		this.handler = handler;
	}

	public void messageArrived(MqttTopic topic, MqttMessage message) {
		try {
			Message msg = Message.obtain();
			Bundle bundle = new Bundle();
			bundle.putString("content", message.toString());
			msg.what = 2;
			msg.setData(bundle);
			handler.sendMessage(msg);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void connectionLost(Throwable throwable) {
		try{
			Message msg = new Message();
			msg.what = 3;
			handler.sendMessage(msg);


		}catch (Exception e){
			e.printStackTrace();
		}
	}

	public void deliveryComplete(MqttDeliveryToken token) {

	}
}