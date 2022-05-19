package client;


import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws IOException {
        String dataFilePath = "C:\\Users\\Admin\\IdeaProjects\\File-Server\\File Server\\task\\src\\client\\data";
        Socket clientSocket = new Socket("localhost", 4004);
        BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
        Scanner scanner = new Scanner(System.in);
        String instruction;
        System.out.println("Enter action (1 - get a file, 2 - create a file, 3 - delete a file):");
        instruction = scanner.nextLine();
        out.write(instruction + "\n");
        String response;
        if (!instruction.equals("exit")) {
            switch (instruction) {
                case "1": {
                    System.out.println("Do you want to get the file by name or by id (1 — name, 2 — id):");
                    String byIdOrName = scanner.nextLine();
                    out.write(byIdOrName + "\n");
                    if (byIdOrName.equals("1")) {
                        System.out.println("Enter filename:");
                    } else {
                        System.out.println("Enter id:");
                    }
                    String idOrMessage = scanner.nextLine();
                    out.write(idOrMessage + "\n");
                    out.flush();
                    System.out.println("The request was sent.");
                    response = in.readLine();
                    System.out.println(response);
                    if (response.equals("200")) {
                        System.out.println("The file was downloaded! Specify a name for it:");
                        String savedFilesName = scanner.nextLine();
                        File file = new File(dataFilePath + "\\" + savedFilesName);
                        FileOutputStream savingFileStream = new FileOutputStream(file);
                        int messageLength = in.read();
                        for (int i = 0; i < messageLength; i++) {
                            savingFileStream.write(in.read());
                        }
                        savingFileStream.close();
                        System.out.println("File saved on the hard drive!");
                    } else {
                        System.out.println("The response says that this file is not found!");
                    }
                    break;
                }
                case "2":
                    System.out.println("Enter name of the file: ");
                    String actualFilename = scanner.nextLine();
                    out.write(actualFilename + "\n");
                    System.out.println("Enter name of the file to be saved on server: ");
                    String OnServerFilename = scanner.nextLine();
                    out.write(OnServerFilename + "\n");
                    out.flush();
                    File file = new File(dataFilePath + "\\" + actualFilename);
                    FileInputStream fileInputStream = new FileInputStream(file);
                    byte[] message = fileInputStream.readAllBytes();
                    int messageLength = message.length;
                    out.write(messageLength);
                    for (byte b : message) {
                        out.write(b);
                    }
                    fileInputStream.close();
                    out.flush();
                    System.out.println("The request was sent.");
                    response = in.readLine();
                    String id = response.substring(4);
                    response = response.substring(0, 3);
                    if (response.equals("200")) {
                        System.out.println("Response says that file is saved! ID = " + id);
                    } else {
                        System.out.println("err");
                    }
                    break;
                case "3": {
                    System.out.println("Do you want to delete the file by name or by id (1 — name, 2 — id):");
                    String byIdOrName = scanner.nextLine();
                    out.write(byIdOrName + "\n");
                    if (byIdOrName.equals("1")) {
                        System.out.println("Enter filename:");
                    } else {
                        System.out.println("Enter id:");
                    }
                    String idOrMessage = scanner.nextLine();
                    out.write(idOrMessage + "\n");
                    out.flush();
                    System.out.println("The request was sent.");
                    response = in.readLine();
                    if (response.equals("200")) {
                        System.out.println("The response says that this file was deleted successfully!");
                    } else {
                        System.out.println("not found");
                    }
                    break;
                }
            }
        } else {
            out.flush();
            System.out.println("The request was sent.");
        }
        clientSocket.close();
        in.close();
        out.close();
    }
}
