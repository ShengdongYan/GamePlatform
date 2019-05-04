package Client;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import org.json.JSONObject;

import java.io.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;


/**
 * Created by bxs863 on 06/03/19.
 */
public class ChatController {


    public Button profileBtn;
    public Label profileLb;
    public Button settingBtn;
    public Label settingLb;
    public Button shopBtn;
    public Label shopLb;
    public Pane chatPane;
    public GridPane profilePane;
    public GridPane shopPane;
    public GridPane settingPane;

    /**
     * return back to the chat area
     * @param event
     */
    public void backToChat(MouseEvent event) {
        profileBtn.setStyle("-fx-background-color:#000000");
        profileLb.setStyle("-fx-text-fill:#ffd7d7");
        shopBtn.setStyle("-fx-background-color:#000000");
        shopLb.setStyle("-fx-text-fill:#ffd7d7");
        settingBtn.setStyle("-fx-background-color:#000000");
        settingLb.setStyle("-fx-text-fill:#ffd7d7");
        chatPane.setVisible(true);
        profilePane.setVisible(false);
        shopPane.setVisible(false);
        settingPane.setVisible(false);
    }

    /**
     * go to the profile page
     * @param event
     */
    public void goProfilePage(MouseEvent event) {
        profileBtn.setStyle("-fx-background-color:#eb4d4d");
        profileLb.setStyle("-fx-text-fill:#ffa600");
        shopBtn.setStyle("-fx-background-color:#000000");
        shopLb.setStyle("-fx-text-fill:#ffd7d7");
        settingBtn.setStyle("-fx-background-color:#000000");
        settingLb.setStyle("-fx-text-fill:#ffd7d7");
        chatPane.setVisible(false);
        profilePane.setVisible(true);
        shopPane.setVisible(false);
        settingPane.setVisible(false);
    }

    /**
     * go to the setting page
     * @param event
     */
    public void goSettingPage(MouseEvent event) {
        profileBtn.setStyle("-fx-background-color:#000000");
        profileLb.setStyle("-fx-text-fill:#ffd7d7");
        shopBtn.setStyle("-fx-background-color:#000000");
        shopLb.setStyle("-fx-text-fill:#ffd7d7");
        settingBtn.setStyle("-fx-background-color:#eb4d4d");
        settingLb.setStyle("-fx-text-fill:#ffa600");
        chatPane.setVisible(false);
        profilePane.setVisible(false);
        shopPane.setVisible(false);
        settingPane.setVisible(true);
    }

    /**
     * go to the shop page
     * @param event
     */
    public void goShopPage(MouseEvent event) {
        profileBtn.setStyle("-fx-background-color:#000000");
        profileLb.setStyle("-fx-text-fill:#ffd7d7");
        shopBtn.setStyle("-fx-background-color:#eb4d4d");
        shopLb.setStyle("-fx-text-fill:#ffa600");
        settingBtn.setStyle("-fx-background-color:#000000");
        settingLb.setStyle("-fx-text-fill:#ffd7d7");
        chatPane.setVisible(false);
        profilePane.setVisible(false);
        shopPane.setVisible(true);
        settingPane.setVisible(false);
    }


    /**
     * For contact list cell. use profile photo
     */
    //INNER CLASS
    class ContactListCell extends ListCell<Profile> {
        @Override
        public void updateItem(Profile item, boolean empty) {
            super.updateItem(item, empty);
            if (item != null) {
                setGraphic(item.getItem());
            } else {
                setGraphic(null);
            }
        }
    }

    /**
     * For message cell
     */
    class MessageCell extends ListCell<String>{

        @Override
        public void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);
            HBox hBox;
            if (item != null) {
                if(item.charAt(0)=='0') {
                    Label label = new Label(item.substring(1));
                    label.setMinWidth(chatArea.getWidth()/10);
                    label.setPrefWidth(chatArea.getWidth()/2);
                    label.setWrapText(true);
                    this.setStyle("-fx-background-color: #222831");
                    label.setStyle("-fx-background-color: #222831");
                    label.setTextFill(Color.WHITE);
                    label.setAlignment(Pos.CENTER_LEFT);
                    label.setWrapText(true);
                    label.setMaxWidth(chatArea.getPrefWidth()/2);
                    hBox = new HBox(10);
                    hBox.setAlignment(Pos.CENTER_LEFT);
                    hBox.getChildren().addAll(label);
                }
                else {
                    Label label = new Label(item.substring(1));
                    label.setMinWidth(chatArea.getWidth()/10);
                    label.setPrefWidth(chatArea.getWidth()/2);
                    label.setWrapText(true);
                    this.setStyle("-fx-background-color: #393e46");
                    label.setStyle("-fx-background-color: #393e46");
                    label.setAlignment(Pos.CENTER_RIGHT);
                    label.setWrapText(true);
                    label.setTextFill(Color.WHITE);
                    label.setMaxWidth(chatArea.getPrefWidth()/2);
                    hBox = new HBox(10);
                    hBox.setAlignment(Pos.CENTER_RIGHT);
                    hBox.getChildren().addAll(label);
                }
                setGraphic(hBox);
            } else {
                setGraphic(null);
            }
        }
    }

    /**
     * For game list
     */
    class GameCell extends ListCell<File>{
        @Override
        public void updateItem(File item, boolean empty) {
            super.updateItem(item, empty);
            double picWidth = gameList.getPrefHeight()-10;
            double picHeight = gameList.getPrefHeight()-10;
            if (item != null && item.listFiles()!=null) {
                String url = null;
                for(File file : item.listFiles()){
                    if(file.getName().endsWith("jpg")||file.getName().endsWith("jpeg")||file.getName().endsWith("png")){
                        url = file.getAbsolutePath();
                    }
                }
                if(url!=null){
                    File sourceimage = new File(url);
                    ImageView imageView = new ImageView(new Image(sourceimage.toURI().toString()));
                    imageView.setFitWidth(picWidth);
                    imageView.setFitHeight(picHeight);
                    imageView.setOpacity(1);
                    this.setOpacity(1);
                    setGraphic(imageView);
                }
                else{
                    ImageView imageView = new ImageView("Client/View/logo.jpg");
                    imageView.setFitWidth(picWidth);
                    imageView.setFitHeight(picHeight);
                    imageView.setOpacity(1);
                    this.setOpacity(1);
                    setGraphic(imageView);
                }
            } else {
                setGraphic(null);
            }
        }
    }

    /**
     * The class for profile
     */
    class Profile{
        private final String username;
        private ImageView icon;
        private HBox item = new HBox(5);
        private Label label;
        private ListView<String> listView = new ListView<>();

        public Profile(String username){
            this.username = username;
            Random random = new Random();
            this.icon = new ImageView();
            icon.setImage(new Image(String.format("Client/View/icon/%d.png",random.nextInt(9)+1),30,30,false,false));
            item.getChildren().add(icon);
            VBox vBox = new VBox();
            vBox.setSpacing(0);
            label = new Label(username);
            label.setTextFill(Color.WHITE);
            label.setStyle("-fx-font-weight: bold");
            vBox.getChildren().add(label);
            item.getChildren().add(vBox);
            listView.setCellFactory(list->new MessageCell());
        }

        public HBox getItem() {
            return item;
        }

        public String getUsername() {
            return username;
        }

        public void setLabelColor(Color color){
            label.setTextFill(color);
        }

        public ListView<String> getListView() {
            return listView;
        }
    }
    //---------------------------------------------------


    public static String username;
    public static String password;

    private ExecutorService es = Executors.newFixedThreadPool(10, r -> {
        Thread t = Executors.defaultThreadFactory().newThread(r);
        t.setDaemon(true);
        return t;
    });


    @FXML
    private ListView<Profile> contactList;
    @FXML
    private ListView<File> gameList;
    @FXML
    private TextArea inputArea;
    @FXML
    private Label usernameLabel;
    @FXML
    private Button sendBtn;
    @FXML
    private Button startBtn;
    @FXML
    public ListView<String> chatArea;


    /**
     * initialize the UI
     */
    @FXML
    public void initialize() {
        contactList.setCellFactory(list -> new ContactListCell());
        contactList.getSelectionModel().selectedItemProperty().addListener(
                (ov, old_val, new_val) -> {
                    if (old_val != null) {
                        old_val.getListView().setItems(chatArea.getItems());
                        if (new_val != null) {
                            chatArea.setItems(new_val.getListView().getItems());
                            old_val.setLabelColor(Color.WHITE);
                            new_val.setLabelColor(Color.BLACK);
                        }
                        else {
                            chatArea.setItems(old_val.getListView().getItems());
                            old_val.setLabelColor(Color.WHITE);
                        }
                    }
                });
        chatArea.setCellFactory(list -> new MessageCell());
        gameList.setCellFactory(list -> new GameCell());
        es.execute(() -> { // receive the message all the time
            while (true) {
                checkConnection();
                JSONObject response;
                if ((response = Client.getClient().findNext("forward_new_message")) != null) {
                    processNewMessage(response);
                }
                if ((response = Client.getClient().findNext("forward_response")) != null) {
                    processResponse(response);
                }
                if ((response = Client.getClient().findNext("contact_list")) != null) {
                    updateContactList(response);
                }
                if ((response = Client.getClient().findNext("start_game_response")) != null) {
                    processStartGameResponse(response);
                }
                if ((response = Client.getClient().findNext("start_game_answer")) != null) {
                    processStartGameAnswer(response);
                }
                if ((response = Client.getClient().findNext("invitation")) != null) {
                    processInvitation(response);
                }
                try {
                    Thread.sleep(20);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        usernameLabel.setText(username);

        File gamesDir = new File("games/");
        if (!gamesDir.exists())
            gamesDir.mkdir();
        File[] games = gamesDir.listFiles();
        if (games != null) {
            gameList.getItems().addAll(games);
            gameList.refresh();
        }
        if(gameList.getItems().size()>0){
            gameList.getSelectionModel().selectFirst();
        }
    }


    /**
     * Process the invitation
     * @param invite
     */
    private void processInvitation(JSONObject invite) {
        Platform.runLater(()->{
            String answer = "no";
            answer = MsgBox2Controller.display("Invitation",invite.getString("message"));
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("type","data");
            jsonObject.put("sub_type","start_game_answer");
            jsonObject.put("from_user",invite.getString("to_user"));
            jsonObject.put("to_user",invite.getString("from_user"));
            jsonObject.put("game",invite.getString("game"));
            jsonObject.put("reply",answer);
            Client.getClient().sendMessage(jsonObject);
        });

    }

    /**
     * Start the game if the two people want to start
     * @param response
     */
    private void processStartGameAnswer(JSONObject response) {
        if(response.getString("reply").equals("yes")){
            File gamesFile = new File("games/"+response.getString("game")+"/");
            File gameExcute = null;
            File[] games = gamesFile.listFiles();
            if(games!=null) {
                for (File file : games) {
//                    if(file.getName().endsWith(".jar") && file.getName().startsWith(response.getString("game"))){
                    if(file.getName().endsWith(".sh")){
                        gameExcute = file;
                        break;
                    }
                }
                if(gameExcute!=null){
                    try {
                        ProcessBuilder p = new ProcessBuilder("bash",gameExcute.getAbsolutePath(),response.getString("gameID"),username, response.getString("master"));
                        p.inheritIO();
                        Process pp = p.start();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                else{
                    Platform.runLater(()->MsgBoxController.display("No","There is no such game"));
                }
            }
            else{
                Platform.runLater(()->MsgBoxController.display("No","There is no such game"));
            }
        }
        else{
            Platform.runLater(()->MsgBoxController.display("No","Your friend refuses to play with you, sad."));
        }
    }


    private void processStartGameResponse(JSONObject response) {
        //Do nothing
    }


    /**
     * Send a new invitation
     * @param mouseEvent
     */
    public void startNewGame(MouseEvent mouseEvent) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("type","data");
        jsonObject.put("sub_type","start_game");
        jsonObject.put("first_user",username);
        jsonObject.put("second_user",contactList.getSelectionModel().getSelectedItem().getUsername());
        jsonObject.put("game",gameList.getSelectionModel().getSelectedItem()==null?"null":gameList.getSelectionModel().getSelectedItem().getName());
        es.execute(()->Client.getClient().sendMessage(jsonObject));
        Platform.runLater(()->MsgBoxController.display("Message has sent","You message has been sent"));

    }

    /**
     * Check if the network is working
     */
    private void checkConnection(){
        if (!Client.getClient().isConnected()){
            Timer timer = new Timer(true);
            timer.schedule(new TimerTask(){
                @Override
                public void run() {
                    Platform.exit();
                }
            },60000);
            while(!Client.getClient().isConnected()) try {
                Platform.runLater(()->{inputArea.setDisable(true);sendBtn.setDisable(true);});
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            JSONObject cookie = new JSONObject();
            cookie.put("type","login");
            cookie.put("username",username);
            cookie.put("password",password);
            Client.getClient().sendMessage(cookie);
            Platform.runLater(()->{inputArea.setDisable(false);sendBtn.setDisable(false);});
            timer.cancel();
            timer.purge();
        }
    }

    /**
     * Update the contact list
     * @param jsonObject
     */
    private void updateContactList(JSONObject jsonObject) {
        boolean isEmpty = false;
        if (contactList.getItems().isEmpty()) {
            isEmpty = true;
        }
        List<String> nameList = jsonObject.getJSONArray("contact_names").toList().stream().map(Object::toString).collect(Collectors.toList());
        Profile[] offlineList = filterOffline(nameList);
        nameList = filterExist(nameList);
        String[] name = new String[nameList.size()];
        name = nameList.toArray(name);
        String[] finalName = name;
        boolean finalIsEmpty = isEmpty;
        Platform.runLater(() -> {
            contactList.getItems().addAll(stringsToProfiles(finalName));
            contactList.getItems().removeAll(offlineList);
            if (finalIsEmpty) contactList.getSelectionModel().selectFirst();
            contactList.refresh();
        });
    }

    /**
     * Handler the new message
     * @param jsonObject
     */
    private void processNewMessage(JSONObject jsonObject) {
        String fromUser = jsonObject.getString("from_user");
        String message = jsonObject.getString("message");
        for (Profile p : contactList.getItems()) {
            if (p.getUsername().equals(fromUser)) {
                Platform.runLater(() -> {
                    p.getListView().getItems().add("0"+message);
                    if (p.getUsername().equals(contactList.getSelectionModel().getSelectedItem().getUsername())) {
                        contactList.getItems().remove(p);
                        contactList.getItems().add(0, p);
                        contactList.getSelectionModel().selectFirst();
                    } else {
                        contactList.getItems().remove(p);
                        contactList.getItems().add(0, p);
                        scrollToBottom(p.getListView());
                    }
                });
                break;
            }
        }
    }

    private void processResponse(JSONObject jsonObject) {
        //RESERVE FOR FUTURE USE
    }


    /**
     * Use key board to send the message
     * @param keyEvent
     */
    public void sendByKeyboard(KeyEvent keyEvent) {
        if(keyEvent.getCode() == KeyCode.ENTER){
            sendMessage();
        }
    }

    public void sendMessage(MouseEvent mouseEvent) {
        sendMessage();
    }


    /**
     * Send the message and clear the input area
     */
    private void sendMessage(){
        String msg = inputArea.getText();
        if(msg.trim().equals("")) return;
        Profile p = contactList.getSelectionModel().getSelectedItem();
        chatArea.getItems().add("1"+msg);
        inputArea.clear();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("type","forward");
        jsonObject.put("sub_type","forward");
        jsonObject.put("to_user",contactList.getSelectionModel().getSelectedItem().getUsername());
        jsonObject.put("message",msg);
        jsonObject.put("from_user",username);
        es.execute(()->Client.getClient().sendMessage(jsonObject));
        scrollToBottom(chatArea);
        contactList.getItems().remove(p);
        contactList.getItems().add(0,p);
        contactList.getSelectionModel().selectFirst();

    }

    /**
     * give an array of name, and change them to profile
     * @param args
     * @return
     */
    private Profile[] stringsToProfiles(String[] args){
        Profile[] profiles = new Profile[args.length];
        for(int i = 0;i<args.length;i++){
            profiles[i]=new Profile(args[i]);
        }
        return profiles;
    }

    /**
     * filter the exist contact in the list
     * @param names
     * @return
     */
    private List<String> filterExist(List<String> names){
        List<String> existName = new ArrayList<>();
        for(Profile i : contactList.getItems()){
            existName.add(i.getUsername());
        }
        List<String> result = new ArrayList<>();
        for(String name:names){
            if(existName.contains(name)) continue;
            result.add(name);
        }
        return result;
    }

    /**
     * remove the offline guys
     * @param nameList
     * @return
     */
    private Profile[] filterOffline(List<String> nameList) {
        List<Profile> offline = new ArrayList<Profile>();
        for(Profile p : contactList.getItems()){
            if(!nameList.contains(p.getUsername())){
                offline.add(p);
            }
        }
        return offline.toArray(new Profile[0]);
    }

    /**
     *
     * @param listView
     */
    private void scrollToBottom(ListView<String> listView){
        Platform.runLater(()->listView.scrollTo(chatArea.getItems().size()-1));
    }

    @FXML
    private void quit(MouseEvent event){
        Platform.exit();
    }
}

