package com.bz.view;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Arrays;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bz.DataModel;
import com.bz.StreamUtil;
import com.bz.UserModel;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;

public class MainUi extends Application{

	@FXML
    private Button connBtn;
	@FXML
    private Button closeConnBtn;
	@FXML
	private Button sendMsgBtn;
	@FXML
    private TextField nickNameTextField;
    @FXML
    private TextArea msgTextField;
    @FXML
    private ListView<DataModel> msgListView;
    private ObservableList<DataModel> personData = FXCollections.observableArrayList();
    @FXML
    private TableColumn<UserModel,String> nickName;
    @FXML
    private TableView<UserModel> userList;
    private ObservableList<UserModel> userListData = FXCollections.observableArrayList();
    
    private static Socket socket = null;
    private OutputStream os;
    private InputStream is;
	private boolean login = false;
    @Override
	public void start(Stage primaryStage) throws Exception {
	   Parent root = FXMLLoader.load(getClass().getResource("/com/bz/view/netty.fxml"));  
	   Scene scene = new Scene(root, 600, 400);
	   primaryStage.initStyle(StageStyle.DECORATED);  
	   primaryStage.setScene(scene);  
	   primaryStage.setTitle("Nubility Chatroom");
	   primaryStage.show();
	   primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
		   @Override
		   public void handle(WindowEvent event) {
			   closeSocket();
		   }
	   });
	}
    public static char getRandomChar() {
        return (char) (0x4e00 + (int) (Math.random() * (0x9fa5 - 0x4e00 + 1)));
    }
	@FXML
    private void initialize() {
		String name="";
		for(int i=0;i<4;i++) {
			name+=getRandomChar();
		}
		msgListView.setItems(personData);
		nickNameTextField.setText(name);

		nickName.setCellValueFactory(new PropertyValueFactory<UserModel,String>("nickName"));
		userList.setItems(userListData);
    }
	
	@FXML
	private void handleConn() {
		final String text = nickNameTextField.getText();
		if("".equals(text)) {
			return;
		}
//		connBtn.setOpacity(0.5);
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					while (true) {
						if(!login) {
							if(socket==null||socket.isClosed()) {
								socket = new Socket();  
								socket.connect(new InetSocketAddress("59.110.227.25",6061));  
								socket.setKeepAlive(true);
								socket.setSoTimeout(1000*60*60);
								os = socket.getOutputStream();
								is = socket.getInputStream();
							}
							DataModel loginModel = new DataModel();
							loginModel.setTime(System.currentTimeMillis());
							loginModel.setMsgType(DataModel.MSG_TYPE_LOGIN);
							loginModel.setBody("{\"userName\":\""+text+"\"}");
							byte[] translate2Byte = translate2Byte(loginModel);
							os.write(translate2Byte);
							os.flush();
							login=true;
						}
						byte[] buff = new byte[1024];
						is.read(buff);
						byte lengthArr[] = {buff[0],buff[1],buff[2],buff[3]};
						int length = StreamUtil.bytesToInt(lengthArr);
						byte[] bytes = Arrays.copyOfRange(buff, 0, length);
						final DataModel translate2Model = translate2Model(bytes);
						Platform.runLater(new Runnable() {
							@Override
							public void run() {
								if(translate2Model.getMsgType()==DataModel.MSG_TYPE_LOGIN) {
									JSONArray array = JSONArray.parseArray(translate2Model.getBody());
									for(int i=0;i<array.size();i++) {
										JSONObject jsonObject = array.getJSONObject(i);
										UserModel userModel = new UserModel(jsonObject.getString("userName"));
										userListData.add(userModel);
									}
								}
								if(translate2Model.getMsgType()==DataModel.MSG_TYPE_LOGIN_OUT) {
									JSONArray array = JSONArray.parseArray(translate2Model.getBody());
									for(int i=0;i<array.size();i++) {
										JSONObject jsonObject = array.getJSONObject(i);
										UserModel removeModel = null;
										for(UserModel model:userListData) {
											if(model.getNickName().equals(jsonObject.getString("userName"))) {
												removeModel = model;
											}
										}
										if(removeModel!=null) userListData.remove(removeModel);
									}
								}
								if(translate2Model.getMsgType()==DataModel.MSG_TYPE_USER_MSG) {
									msgListView.scrollTo(personData.size()-1);
									personData.add(translate2Model);
								}
							}
						});
					}
				}catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		thread.setDaemon(true);
		thread.start();
		connBtn.setDisable(true);
		closeConnBtn.setDisable(false);
		sendMsgBtn.setDisable(false);
	}
	@FXML
	private void onConnCloseEvent() {
		try {
			DataModel loginModel = new DataModel();
			loginModel.setTime(System.currentTimeMillis());
			loginModel.setMsgType(DataModel.MSG_TYPE_LOGIN_OUT);
			loginModel.setBody("loginOut");
			byte[] translate2Byte = translate2Byte(loginModel);
			os.write(translate2Byte);
			os.flush();
			closeSocket();
			login = false;
			userListData.clear();
		} catch (Exception e) {
			e.printStackTrace();
		}
		connBtn.setDisable(false);
		closeConnBtn.setDisable(true);
		sendMsgBtn.setDisable(true);
	}
	@FXML
	private void onMSGSendEvent() {
		try {
			String text = msgTextField.getText();
			if("".equals(text)||text.length()>20) {
				return;
			}
			DataModel loginModel = new DataModel();
			loginModel.setTime(System.currentTimeMillis());
			loginModel.setMsgType(DataModel.MSG_TYPE_USER_MSG);
			loginModel.setBody(text);
			byte[] translate2Byte = translate2Byte(loginModel);
			os.write(translate2Byte);
			os.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
		msgTextField.setText("");
	}
	public DataModel translate2Model(byte[] in) {
		int length = in.length;
		byte[] msgTypeArr = {in[4],in[5]};
		short msgType = StreamUtil.byteToShort(msgTypeArr);
		byte[] bodyLengthArr = {in[6],in[7],in[8],in[9]};
		int bodyLength = StreamUtil.bytesToInt(bodyLengthArr);
		byte[] bodyArr = new byte[bodyLength];
		for(int i=0;i<bodyLength;i++) {
			bodyArr[i] = in[i+10];
		}
		String body = new String(bodyArr);
		byte[] timeArr = Arrays.copyOfRange(in, in.length-8, in.length);
		long time = StreamUtil.byteToLong(timeArr);
        DataModel model = new DataModel();
        model.setLength(length);
        model.setMsgType(msgType);
        model.setBodyLength(bodyLength);
        model.setBody(body);
        model.setTime(time);
        return model;
	}

	public byte[] translate2Byte(DataModel model) {
		short msgType = model.getMsgType();
		String bodyStr = model.getBody();
		byte[] type = StreamUtil.shortToByte(msgType);
		byte[] body = bodyStr.getBytes();
		byte[] time = StreamUtil.longToByte(System.currentTimeMillis());
		byte[] bodyLength = StreamUtil.intToByte(body.length);
		byte[] length = StreamUtil.intToByte(18+body.length);
		return StreamUtil.byteArrays(length,type,bodyLength,body,time);
	}
	private void closeSocket() {
		if(socket!=null&&!socket.isClosed()) {
		   try {
			   socket.close();
		   } catch (IOException e) {
			   e.printStackTrace();
		   }
		}
	}
	public static void main(String[] args) {
		MainUi mainUi = new MainUi();
		mainUi.launch(args);
	}
}
