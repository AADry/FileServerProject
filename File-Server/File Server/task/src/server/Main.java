package server;

import java.io.*;
import java.net.Socket;
import java.net.ServerSocket;
import java.util.HashMap;

public class Main {
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        ServerSocket server;
        BufferedReader in = null;
        BufferedWriter out = null;
        Socket socketOfServer = null;
        HashMap<Integer, String> ids = new HashMap<>();
        File idFile = new File("C:\\Users\\Admin\\IdeaProjects\\File-Server\\File Server\\task\\src\\server\\data\\ids.txt");
        if (idFile.exists()) {
            FileInputStream fileInputStream = new FileInputStream(idFile);
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            ids = (HashMap<Integer, String>) objectInputStream.readObject();
            objectInputStream.close();
            fileInputStream.close();
        }
        String dataFilePath = "C:\\Users\\Admin\\IdeaProjects\\File-Server\\File Server\\task\\src\\server\\data";
        File data = new File(dataFilePath);
        if (!data.exists() || !data.isDirectory()) data.mkdirs();
        server = new ServerSocket(4004);
        System.out.println("Server started!");
        String instruction = "start";

        while (!instruction.equals("exit")) {
            socketOfServer = server.accept();
            in = new BufferedReader(new InputStreamReader(socketOfServer.getInputStream()));
            out = new BufferedWriter(new OutputStreamWriter(socketOfServer.getOutputStream()));
            instruction = in.readLine();
            if (!instruction.equals("exit")) {
                String path = "C:\\Users\\Admin\\IdeaProjects\\File-Server\\File Server\\task\\src\\server\\data\\";
                String byIdOrName;
                String filename;
                String idOrName;
                File file;
                int id;
                switch (instruction) {
                    case "1":
                        byIdOrName = in.readLine();
                        idOrName = in.readLine();
                        if (byIdOrName.equals("1")) {
                            filename = idOrName;
                            path += filename;
                        } else {
                            id = Integer.parseInt(idOrName);
                            path += ids.get(id);
                        }
                        file = new File(path);
                        if (file.exists()) {
                            out.write("200\n");
                            out.flush();
                            FileInputStream fileInputStream = new FileInputStream(file);
                            byte[] message = fileInputStream.readAllBytes();
                            int messageLength = message.length;
                            out.write(messageLength);
                            for (byte b : message) {
                                out.write(b);
                            }
                            fileInputStream.close();
                            out.flush();
                        } else {
                            out.write("404");
                        }
                        break;
                    case "2":
                        filename = in.readLine();
                        String OnServerFilename = in.readLine();
                        if (OnServerFilename.equals("")) {
                            OnServerFilename = filename;
                        }
                        path += OnServerFilename;
                        file = new File(path);
                        boolean fileCreated = file.createNewFile();
                        FileOutputStream savingFileStream = new FileOutputStream(file);
                        int messageLength = in.read();
                        for (int i = 0; i < messageLength; i++) {
                            savingFileStream.write(in.read());
                        }
                        savingFileStream.close();
                        id = generateNewId(ids);
                        ids.put(id, OnServerFilename);
                        out.write(fileCreated ? "200 " + id : "403");
                        break;
                    case "3":
                        byIdOrName = in.readLine();
                        idOrName = in.readLine();
                        if (byIdOrName.equals("1")) {
                            filename = idOrName;
                            path += filename;
                        } else {
                            id = Integer.parseInt(idOrName);
                            path += ids.get(id);
                        }
                        file = new File(path);
                            out.write(file.delete() ? "200\n" : "404\n");
                        break;
                }
                out.newLine();
                out.flush();
            }
        }
        FileOutputStream fileOutputStream = new FileOutputStream(idFile);
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
        objectOutputStream.writeObject(ids);
        objectOutputStream.flush();
        objectOutputStream.close();
        fileOutputStream.close();
        in.close();
        out.close();
        socketOfServer.close();
        server.close();
    }

    public static int generateNewId(HashMap<Integer, String> map) {
        int i = 0;
        while (map.containsKey(i)) {
            i++;
        }
        return i;
    }
}