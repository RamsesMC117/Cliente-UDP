package com.mycompany.clienteudp;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class ClienteUDP {

    private DatagramSocket socket;
    private InetAddress address;
    private JTextArea textArea;
    private JTextField messageField;

    public ClienteUDP() {
        try {
            socket = new DatagramSocket(); // Crea un socket UDP
            address = InetAddress.getByName("localhost"); // Cambiar si es necesario
        } catch (Exception e) {
            showMessage("Error al crear el socket: " + e.getMessage());
        }

        createGUI();
    }

    private void createGUI() {
        JFrame frame = new JFrame("Cliente UDP");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);
        frame.setLayout(new BorderLayout());

        textArea = new JTextArea();
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);
        frame.add(scrollPane, BorderLayout.CENTER);

        messageField = new JTextField();
        frame.add(messageField, BorderLayout.SOUTH);

        JButton sendButton = new JButton("Enviar");
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        });
        frame.add(sendButton, BorderLayout.EAST);

        frame.setVisible(true);
    }

    private void sendMessage() {
        String message = messageField.getText();
        if (!message.isEmpty()) {
            try {
                byte[] buffer = message.getBytes();
                // Enviar el mensaje al servidor en el puerto 8080
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address, 8080);
                socket.send(packet);
                showMessage("Cliente UDP: " + message);
                messageField.setText(""); // Limpiar el campo de entrada
            } catch (Exception e) {
                showMessage("Error al enviar el mensaje: " + e.getMessage());
            }
        }
    }

    private void showMessage(String message) {
        textArea.append(message + "\n");
    }

    public void receiveMessages() {
        while (true) {
            try {
                byte[] buffer = new byte[1024];
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet); // Escuchar mensajes en el socket
                String response = new String(packet.getData(), 0, packet.getLength());
                showMessage(response); // Mostrar el mensaje recibido
            } catch (Exception e) {
                showMessage("Error al recibir el mensaje: " + e.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        ClienteUDP clienteUDP = new ClienteUDP();

        // Iniciar un hilo para recibir mensajes
        new Thread(clienteUDP::receiveMessages).start();
    }
}
