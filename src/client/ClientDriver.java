package client;

import client.services.AuthUiServices;
import client.services.ChatUiServices;
import client.services.ClientServices;
import client.services.GroupUiServices;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

class ClientDriver {
    public static void main(String[] args) {
        Socket socket;
        ObjectOutputStream writer;
        ObjectInputStream reader;
        ErrorHandler errHandler = null;
        try {
            socket = new Socket("localhost", 8000);
            writer = new ObjectOutputStream(socket.getOutputStream());
            reader = new ObjectInputStream(socket.getInputStream());
            errHandler = new ErrorHandler(socket, writer, reader);
            AuthUiServices authUi = new AuthUi(writer, reader, errHandler);
            authUi.authenticate();
            ChatUiServices chatUi = new ChatUi(socket, writer, reader, errHandler);
            ParticipantsUi participantsUi = new ParticipantsUi(writer, reader, errHandler);
            GroupUiServices group = new GroupUi(writer, reader, errHandler, chatUi, participantsUi);
            ClientServices client = new ClientUi(writer, reader, authUi, group, errHandler);
            client.displayMenu();
        } catch (Exception e) {
            if (errHandler != null) errHandler.closeConnection();
            else System.out.print("Server is not responding");
        }
    }
}